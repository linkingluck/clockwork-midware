package com.linkingluck.midware.ormcache.exception;

/**
 * 无效的实体异常<br/>
 * 当实体主键已经被占用时抛出
 */
public class InvalidEntityException extends CacheException {

	private static final long serialVersionUID = 1622804717507686589L;

	public InvalidEntityException() {
		super();
	}

	public InvalidEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidEntityException(String message) {
		super(message);
	}

	public InvalidEntityException(Throwable cause) {
		super(cause);
	}
}
