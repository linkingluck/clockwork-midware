package com.linkingluck.midware.event.core;

public interface IEventCallBack {

	/**
	 * 事件处理后回调
	 *
	 * @param returnValue 事件处理后返回值
	 */
	void callback(Object returnValue);


	/**
	 * 事件处理后抛出异常
	 *
	 * @param throwable 异常
	 */
	void exception(Throwable throwable);
}
