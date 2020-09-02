package com.linkingluck.midware.resource.bean;

import com.linkingluck.midware.resource.model.ResourceDefinition;
import com.linkingluck.midware.utility.threadpool.IoThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class StorageManagerFactory implements FactoryBean<StorageManager>, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(StorageManagerFactory.class);

//	private static final int warnSize = File.


	private final ExecutorService executorService = Executors.newFixedThreadPool(10);


	/**
	 * 资源列表
	 */
	private List<ResourceDefinition> definitions;

	public List<ResourceDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<ResourceDefinition> definitions) {
		this.definitions = definitions;
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public StorageManager getObject() throws Exception {
		StorageManager storageManager = applicationContext.getAutowireCapableBeanFactory().createBean(StorageManager.class);
		CountDownLatch countDownLatch = new CountDownLatch(definitions.size());

		AtomicBoolean result = new AtomicBoolean(true);
		for (ResourceDefinition resourceDefinition : definitions) {
			Storage storage = storageManager.initialize(resourceDefinition);
			IoThreadPool.execute("resourceLoad", () -> {
				try {
					if (resourceDefinition.isRequired()) {
//					try {
//						Resource resource = ResourceUtils.findResource(applicationContext, resourceDefinition.getLocation());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}

						storage.initialize(resourceDefinition);
						logger.info("load resource {} success", resourceDefinition.getLocation());
					}
				} catch (Exception e) {
					logger.error("load resource {} error:{}", resourceDefinition.getLocation(), e.getMessage());
					e.printStackTrace();
					result.set(false);
				} finally {
					countDownLatch.countDown();
				}


			});
		}

		countDownLatch.await();

		if (result.get()) {
			logger.info("load All Resource Success!");
		} else {
			System.exit(1);
		}

		return storageManager;
	}

	@Override
	public Class<?> getObjectType() {
		return StorageManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
