package com.linkingluck.midware.resource.model;

import com.linkingluck.midware.resource.anno.ResourceClassFieldInject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Objects;

public class InjectDefinition {

	/**
	 * 被注入的属性字段
	 **/
	private Field field;

	/**
	 * 注入方式
	 **/
	private Autowire autowire;

	/**
	 * 注入beanName
	 **/
	private String beanName;

	private int hashCode;

	public InjectDefinition(Field field) {
		if (field == null) {
			throw new RuntimeException("被注入的属性字段为空");
		}

		if (!field.isAnnotationPresent(ResourceClassFieldInject.class)) {
			throw new IllegalArgumentException("被注入的属性字段" + field.getName() + "必需加注解ResourceClassFieldInject");
		}
		field.setAccessible(true);

		this.field = field;
		ResourceClassFieldInject anno = field.getAnnotation(ResourceClassFieldInject.class);
		beanName = anno.value();
		autowire = !StringUtils.isBlank(beanName) ? Autowire.BY_NAME : Autowire.BY_TYPE;

		hashCode = Objects.hash(field);
	}

	public Object getValue(ApplicationContext applicationContext) {
		if (autowire == Autowire.BY_TYPE) {
			return applicationContext.getBean(field.getType());
		}
		return applicationContext.getBean(beanName);
	}

	public void injectValue(ApplicationContext applicationContext, Object classInstance) {
		try {
			field.set(classInstance, getValue(applicationContext));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof InjectDefinition)) {
			return false;
		}

		InjectDefinition that = (InjectDefinition) o;
		return Objects.equals(field, that.field);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public Field getField() {
		return field;
	}
}
