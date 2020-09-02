package com.linkingluck.midware.resource.reader;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReaderHolder implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private Map<String, ResourceReader> readers = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		for (ResourceReader reader : applicationContext.getBeansOfType(ResourceReader.class).values()) {
			readers.putIfAbsent(reader.getSuffix(), reader);
		}
	}

	public boolean isContainReader(String format) {
		return readers.containsKey(format);
	}

	public ResourceReader getResourceReader(String format) {
		return readers.get(format);
	}

}
