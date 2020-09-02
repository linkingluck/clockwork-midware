package com.linkingluck.midware.event.core;

import java.lang.reflect.Method;

/**
 * 事件上下文参数
 */
public interface IEventContext {


	Object getParam(Method method, Class[] parameterTypes, int index);
}
