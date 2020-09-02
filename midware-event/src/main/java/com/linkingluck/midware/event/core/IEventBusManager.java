package com.linkingluck.midware.event.core;

public interface IEventBusManager {

	/**
	 * 注册事件监听者
	 *
	 * @param bean
	 */
	void registerReceiver(Object bean);


	/**
	 * 异步抛出事件
	 *
	 * @param event          事件
	 * @param eventName      事件名
	 * @param dispatcherCode 线程分发号
	 */
	void asyncSubmit(final Object event, final String eventName, final int dispatcherCode);

	/**
	 * 异步抛出事件
	 *
	 * @param event          事件
	 * @param eventName      事件名
	 * @param dispatcherCode 线程分发号
	 * @param callBack       事件执行结果后回调
	 */
	void asyncSubmit(final Object event, final String eventName, final int dispatcherCode, IEventCallBack callBack);


	/**
	 * 异步抛出事件
	 *
	 * @param event          事件
	 * @param eventName      事件名
	 * @param dispatcherCode 线程分发号
	 * @param contexts       事件上下文
	 */
	void asyncSubmitWithContext(final Object event, final String eventName, final int dispatcherCode, Object... contexts);


	/**
	 * 异步抛出事件
	 *
	 * @param event          事件
	 * @param eventName      事件名
	 * @param dispatcherCode 线程分发号
	 * @param callBack       事件执行结果后回调
	 * @param contexts       事件上下文
	 */
	void asyncSubmitWithContext(final Object event, final String eventName, final int dispatcherCode, IEventCallBack callBack, Object... contexts);


	/**
	 * 同步抛出事件
	 *
	 * @param event 事件
	 */
	void syncSubmit(final Object event);


	/**
	 * 同步抛出事件
	 *
	 * @param event    事件
	 * @param callBack 事件执行结果后回调
	 */
	void syncSubmit(final Object event, IEventCallBack callBack);


	/**
	 * 同步抛出事件
	 *
	 * @param event    事件
	 * @param contexts 事件上下文
	 */
	void syncSubmitWithContext(final Object event, Object... contexts);


	/**
	 * 同步抛出事件
	 *
	 * @param event    事件
	 * @param callBack 事件执行结果后回调
	 * @param contexts 事件上下文
	 */
	void syncSubmitWithContext(final Object event, IEventCallBack callBack, Object... contexts);

}
