package com.linkingluck.midware.resource.bean;

import com.linkingluck.midware.resource.model.ResourceDefinition;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageManager implements ApplicationContextAware {

	private Map<Class<?>, ResourceDefinition> definitions = new ConcurrentHashMap<>();

	private Map<Class<?>, Storage> storages = new ConcurrentHashMap<>();

	public Storage initialize(ResourceDefinition resourceDefinition) {
		Class<?> clz = resourceDefinition.getClz();
		if (null != definitions.putIfAbsent(clz, resourceDefinition)) {
			String message = MessageFormat.format("已经在StorageManager中初始化了[{0}]的资源配置定义", clz.getSimpleName());
			throw new RuntimeException(message);
		}

		//创建storage
		return initializeStorage(clz);
	}

	private Storage initializeStorage(Class<?> clz) {
		if (!definitions.containsKey(clz)) {
			String message = MessageFormat.format("在StorageManager没初始化了[{0}]的资源配置定义", clz.getSimpleName());
			throw new RuntimeException(message);
		}

		Storage storage = applicationContext.getAutowireCapableBeanFactory().createBean(Storage.class);
		Storage pre = storages.putIfAbsent(clz, storage);
		return pre == null ? storage : pre;
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public <T> T getResource(Object key, Class<T> clz) {
		Storage storage = getStorage(clz);
		return (T) storage.get(key, false);
	}

	public Storage<?, ?> getStorage(Class clz) {
		if (storages.containsKey(clz)) {
			return storages.get(clz);
		}
		return initializeStorage(clz);
	}

	public void checkAll() {

		storages.values().forEach(storage -> storage.check());

	}
}
