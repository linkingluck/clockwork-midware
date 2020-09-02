package com.linkingluck.midware.event.core;

public abstract class AbstractEventBusManagerAdapter extends AbstractEventBusManager {

	@Override
	public void asyncSubmit(Object event, String eventName, int dispatcherCode) {

	}

	@Override
	public void asyncSubmit(Object event, String eventName, int dispatcherCode, IEventCallBack callBack) {

	}

	@Override
	public void asyncSubmitWithContext(Object event, String eventName, int dispatcherCode, Object... contexts) {

	}

	@Override
	public void asyncSubmitWithContext(Object event, String eventName, int dispatcherCode, IEventCallBack callBack, Object... contexts) {

	}

	@Override
	public void syncSubmit(Object event) {

	}

	@Override
	public void syncSubmit(Object event, IEventCallBack callBack) {

	}

	@Override
	public void syncSubmitWithContext(Object event, Object... contexts) {

	}

	@Override
	public void syncSubmitWithContext(Object event, IEventCallBack callBack, Object... contexts) {

	}
}
