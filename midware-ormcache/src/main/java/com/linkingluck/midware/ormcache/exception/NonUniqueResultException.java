package com.linkingluck.midware.ormcache.exception;


/**
 * 非唯一结果查询异常
 * @author frank
 */
public class NonUniqueResultException extends OrmException {

	private static final long serialVersionUID = -1710027252202582783L;

	public NonUniqueResultException() {
		super();
	}

	public NonUniqueResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public NonUniqueResultException(String message) {
		super(message);
	}

	public NonUniqueResultException(Throwable cause) {
		super(cause);
	}

}
