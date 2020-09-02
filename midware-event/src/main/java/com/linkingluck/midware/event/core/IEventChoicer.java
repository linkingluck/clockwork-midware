package com.linkingluck.midware.event.core;

import java.lang.reflect.Method;

public interface IEventChoicer {

	Class ChoicerEvent(Method method);
}
