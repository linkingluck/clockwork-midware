package com.linkingluck.midware.event.core;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class ReceiverDefintion implements IReceiverInvoke {
	private final Object bean;
	private final Method method;
	private final Class<?> eventClz;

	private ReceiverDefintion(Object bean, Method method, Class<?> eventClz) {
		this.bean = bean;
		this.method = method;
		this.eventClz = eventClz;
	}

	public static ReceiverDefintion valueOf(Object bean, Method method, Class<?> eventClz) {
		return new ReceiverDefintion(bean, method, eventClz);
	}

	public Class<?> getEventClz() {
		return eventClz;
	}

	@Override
	public Object invoke(IEventContext eventContext) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] parameters = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameters[i] = eventContext.getParam(method, parameterTypes, i);
		}
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, bean, parameters);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bean == null) ? 0 : bean.hashCode());
		result = prime * result + ((eventClz == null) ? 0 : eventClz.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReceiverDefintion other = (ReceiverDefintion) obj;
		if (bean == null) {
			if (other.bean != null)
				return false;
		} else if (!bean.equals(other.bean))
			return false;
		if (eventClz == null) {
			if (other.eventClz != null)
				return false;
		} else if (!eventClz.equals(other.eventClz))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}

}
