package com.linkingluck.midware.event.core;

public abstract class AbstractEventBusManager implements IEventBusManager {

	public static final String EVENT_CHOICER = "eventChoicer";

	private IEventChoicer eventChoicer;

	public IEventChoicer getEventChoicer() {
		return eventChoicer;
	}

	public void setEventChoicer(IEventChoicer eventChoicer) {
		this.eventChoicer = eventChoicer;
	}
}
