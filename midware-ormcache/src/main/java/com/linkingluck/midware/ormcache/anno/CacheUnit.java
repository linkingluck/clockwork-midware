package com.linkingluck.midware.ormcache.anno;

/**
 *
 */
public enum CacheUnit {

	/** 实体 */
	ENTITY,
	/**
	 * 区域 PS:不建议使用,该缓存方式存在还未解决的BUG
	 */
	@Deprecated
	REGION;
}
