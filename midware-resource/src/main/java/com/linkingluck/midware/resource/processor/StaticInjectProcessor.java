package com.linkingluck.midware.resource.processor;

import com.linkingluck.midware.resource.anno.ResourceInject;
import com.linkingluck.midware.resource.bean.Storage;
import com.linkingluck.midware.resource.bean.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class StaticInjectProcessor extends InstantiationAwareBeanPostProcessorAdapter implements Ordered {

	private static final Logger logger = LoggerFactory.getLogger(StaticInjectProcessor.class);

	@Autowired
	private StorageManager storageManager;

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), field -> {
			if (field.isAnnotationPresent(ResourceInject.class) && field.getType().isAssignableFrom(Storage.class)) {
				ResourceInject anno = field.getAnnotation(ResourceInject.class);
				inject(bean, field, anno);
			}
		});


		return super.postProcessAfterInstantiation(bean, beanName);
	}

	private void inject(Object bean, Field field, ResourceInject anno) {
		Type type = field.getGenericType();
		if (!(type instanceof ParameterizedType)) {
			String message = "类型声明不正确";
			logger.debug(message);
			throw new RuntimeException(message);
		}

		Type[] types = ((ParameterizedType) type).getActualTypeArguments();
		if (!(types[1] instanceof Class)) {
			String message = "类型声明不正确";
			logger.debug(message);
			throw new RuntimeException(message);
		}

		Class clz = (Class) types[1];
		ReflectionUtils.makeAccessible(field);
		Object value = storageManager.getStorage(clz);
		try {
			field.set(bean, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}
}
