package com.linkingluck.midware.event.core;

import java.lang.reflect.Method;

public interface IReceiverInvoke {

	Object getBean();

	Method getMethod();

	Object invoke(IEventContext eventContext);
}
