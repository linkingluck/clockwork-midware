package com.linkingluck.midware.ormcache.exception;

/**
 * 状态异常
 * @author frank
 */
public class StateException extends CacheException {

	private static final long serialVersionUID = 3236441373096075879L;

	public StateException() {
		super();
	}

	public StateException(String message, Throwable cause) {
		super(message, cause);
	}

	public StateException(String message) {
		super(message);
	}

	public StateException(Throwable cause) {
		super(cause);
	}

}
