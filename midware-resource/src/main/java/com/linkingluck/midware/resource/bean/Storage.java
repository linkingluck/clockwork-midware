package com.linkingluck.midware.resource.bean;

import com.linkingluck.midware.resource.model.ResourceDefinition;
import com.linkingluck.midware.resource.model.Resourcecheck;
import com.linkingluck.midware.resource.reader.ReaderHolder;
import com.linkingluck.midware.resource.reader.ResourceReader;
import com.linkingluck.midware.resource.support.Getter;
import com.linkingluck.midware.resource.support.GetterBuilder;
import com.linkingluck.midware.resource.support.IndexGetter;
import com.linkingluck.midware.resource.util.ResourceUtils;
import com.linkingluck.midware.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Storage<K, V> implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(StorageManagerFactory.class);

	@Autowired
	private ReaderHolder readerHolder;


	/**
	 * 读锁
	 */
	private Lock readLock;

	private Lock writeLock;

	public Storage() {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	private boolean initialized;

	private ResourceDefinition resourceDefinition;

	private ResourceReader reader;

	private Getter idGetter;

	private Map<String, IndexGetter> indexGetter;

	public void initialize(ResourceDefinition resourceDefinition) {
		//防止重复初始化
		if (initialized) {
			return;
		}

		initialized = true;

		//资源定义
		this.resourceDefinition = resourceDefinition;

		//资源读取器
		reader = readerHolder.getResourceReader(resourceDefinition.getSuffix());

		if (reader == null) {
			String message = MessageFormat.format("没有[{0}]对应的资源读取器", resourceDefinition.getSuffix());
			throw new RuntimeException(message);
		}

		//批量数据标识列
		if (reader.isBatch()) {
			idGetter = GetterBuilder.createIdGetter(resourceDefinition.getClz());
		}
		indexGetter = GetterBuilder.createIndexGetters(resourceDefinition.getClz());

		//处理静态字段注入
		resourceDefinition.getStaticInjects().forEach(injectDefinition -> injectDefinition.injectValue(applicationContext, null));

		//载入配置数据 处理实例字段注入
		this.load();

		//监听热更
	}


	private void isReady() {
		if (!isInitialized()) {
			String message = "未初始化完成，准备工作没做完";
			logger.error(message);
			throw new RuntimeException(message);
		}
	}

	private boolean isInitialized() {
		return initialized;
	}

	private Map<K, V> values = new HashMap<>();
	/**
	 * 索引存储空间
	 */
	private Map<String, Map<Object, List<V>>> indexs = new HashMap<String, Map<Object, List<V>>>();
	/**
	 * 唯一值存储空间
	 */
	private Map<String, Map<Object, V>> uniques = new HashMap<String, Map<Object, V>>();

	private void load() {
		isReady();
		writeLock.lock();
		InputStream inputStream = null;
		try {
			//资源读取
			Resource resource = getResource();
			inputStream = resource.getInputStream();

			//从资源输入流导入资源
			List<V> list = (List<V>) reader.read(resourceDefinition.getClz(), inputStream);

			//清理资源
			clear();

			//
			Iterator<V> it = list.iterator();
			while (it.hasNext()) {
				V value = it.next();
				resourceDefinition.getInstanceInjects().forEach(injectDefinition -> injectDefinition.injectValue(applicationContext, value));

				if (put(value) != null) {
					FormattingTuple message = MessageFormatter.format("[{}]资源[{}]的唯一标识重复", getClz(),
							JsonUtils.object2String(value));
					logger.error(message.getMessage());
					throw new IllegalStateException(message.getMessage());
				}
			}

			//对索引排序
//			 for (Map.Entry<String, Map<Object, List<V>>> entry : indexs.entrySet()) {
//				 String key = entry.getKey();
//				 IndexGetter getter = indexGetter.get(key);
//				 if (getter.hasComparator()) {
//					 for (List<V> values : entry.getValue().values()) {
//						 Collections.sort(values, getter.getComparator());
//					 }
//				 }
//			 }

			//加载完毕 通知监听器


		} catch (IOException e) {
			String message = MessageFormat.format("资源读取失败[{0}]", resourceDefinition.getLocation());
			logger.error(message);
			throw new RuntimeException(message);
		} finally {
			writeLock.unlock();
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("文件流关闭异常", e);
				}
			}
		}


	}

	private V put(V value) {
		// 唯一标识处理
		K key = (K) idGetter.getValue(value);
		if (key == null) {
			FormattingTuple message = MessageFormatter.format("静态资源[{}]存在标识属性为null的配置项", getClz().getName());
			logger.error(message.getMessage());
			throw new RuntimeException(message.getMessage());
		}
		V result = values.put(key, value);

		// 索引处理
		for (IndexGetter getter : indexGetter.values()) {
			String name = getter.getName();
			Object indexKey = getter.getValue(value);

			// 索引内容存储
			if (getter.isUnique()) {
				Map<Object, V> index = loadUniqueIndex(name);
				if (index.put(indexKey, value) != null) {
					FormattingTuple message = MessageFormatter.format("[{}]资源的唯一索引[{}]的值[{}]重复",
							new Object[]{getClz().getName(), name, indexKey});
					logger.debug(message.getMessage());
					throw new RuntimeException(message.getMessage());
				}
			} else {
				List<V> index = loadListIndex(name, indexKey);
				index.add(value);
			}
		}

		return result;
	}

	private List<V> loadListIndex(String name, Object key) {
		Map<Object, List<V>> index = loadListIndex(name);
		if (index.containsKey(key)) {
			return index.get(key);
		}

		List<V> result = new ArrayList<V>();
		index.put(key, result);
		return result;
	}

	private Map<Object, List<V>> loadListIndex(String name) {
		if (indexs.containsKey(name)) {
			return indexs.get(name);
		}

		Map<Object, List<V>> result = new HashMap<Object, List<V>>();
		indexs.put(name, result);
		return result;
	}

	private Map<Object, V> loadUniqueIndex(String name) {
		if (uniques.containsKey(name)) {
			return uniques.get(name);
		}

		Map<Object, V> result = new HashMap<Object, V>();
		uniques.put(name, result);
		return result;
	}

	private void clear() {
		values.clear();
		indexs.clear();
		uniques.clear();
	}

	public Class<V> getClz() {
		return (Class<V>) resourceDefinition.getClz();
	}

	private Resource getResource() throws IOException {
		return ResourceUtils.findResource(applicationContext, resourceDefinition.getLocation());
	}

	public V get(K key, boolean flag) {
		isReady();
		readLock.lock();
		try {
			V result = values.get(key);
			if (flag && result == null) {
				FormattingTuple message = MessageFormatter.format("标识为[{}]的静态资源[{}]不存在", key, getClz().getName());
				logger.error(message.getMessage());
				throw new IllegalStateException(message.getMessage());
			}
			return result;
		} finally {
			readLock.unlock();
		}
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void check() {
		values.values().forEach(v -> {
			if (v instanceof Resourcecheck) {
				Resourcecheck resourcecheck = (Resourcecheck) v;
				resourcecheck.check();
			}
		});

	}
}
