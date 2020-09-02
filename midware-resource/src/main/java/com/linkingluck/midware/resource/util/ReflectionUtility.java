package com.linkingluck.midware.resource.util;

import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtility extends ReflectionUtils {

	/**
	 * @param clz             处理的类
	 * @param annotationClass 标注的注解
	 * @return 类中有标注的注解的字段
	 */
	public static Field[] getDeclaredFieldsWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clz.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotationClass)) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[0]);
	}

	/**
	 * @param clz             处理的类
	 * @param annotationClass 标注的注解
	 * @return 类中有标注的注解的方法
	 */
	public static Method[] getDeclaredGetMethodsWith(Class<?> clz, Class<? extends Annotation> annotationClass) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : clz.getDeclaredMethods()) {
			if (method.getAnnotation(annotationClass) == null) {
				continue;
			}
			if (method.getReturnType().equals(void.class)) {
				continue;
			}
			if (method.getParameterTypes().length > 0) {
				continue;
			}
			methods.add(method);
		}
		return methods.toArray(new Method[0]);
	}

}
