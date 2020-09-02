package com.linkingluck.midware.event.core;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventContext implements IEventContext {

	private Map<Class<?>, Object> class2ParamMap;

	public static EventContext valueOf(Object event, Object... contexts) {
		EventContext vo = new EventContext();
		if (contexts == null) {
			vo.class2ParamMap = Collections.singletonMap(event.getClass(), event);
		} else {
			vo.class2ParamMap = new HashMap<>(contexts.length + 1);
			vo.class2ParamMap.put(event.getClass(), event);
			for (Object context : contexts) {
				if (context == null) {
					continue;
				}
				if (vo.class2ParamMap.put(context.getClass(), context) != null) {
					throw new RuntimeException("param same class not support!");
				}
			}
		}

		return vo;
	}

	@Override
	public Object getParam(Method method, Class[] parameterTypes, int index) {
		Class<?> clz = parameterTypes[index];
		clz = !clz.isPrimitive() ? clz : getClassByPrimitiveClass(clz);

		for (Map.Entry<Class<?>, Object> entry : class2ParamMap.entrySet()) {
			if (clz.isAssignableFrom(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Class<?> getClassByPrimitiveClass(Class<?> primitiveClass) {
		if (!primitiveClass.isPrimitive()) {
			return primitiveClass;
		}

		if (primitiveClass == char.class) {
			return Character.class;
		}

		if (primitiveClass == boolean.class) {
			return Boolean.class;
		}

		if (primitiveClass == byte.class) {
			return Byte.class;
		}

		if (primitiveClass == short.class) {
			return Short.class;
		}

		if (primitiveClass == int.class) {
			return Integer.class;
		}

		if (primitiveClass == long.class) {
			return Long.class;
		}

		if (primitiveClass == float.class) {
			return Float.class;
		}

		if (primitiveClass == double.class) {
			return Double.class;
		}

		if (primitiveClass == void.class) {
			return Void.class;
		}

		return Void.class;
	}

	public int getParameterCount() {
		return class2ParamMap.size();
	}

	public boolean hasParameterType(Class<?> clz) {
		clz = !clz.isPrimitive() ? clz : getClassByPrimitiveClass(clz);
		for (Map.Entry<Class<?>, Object> entry : class2ParamMap.entrySet()) {
			if (clz.isAssignableFrom(entry.getKey())) {
				return true;
			}
		}
		return false;
	}
}
