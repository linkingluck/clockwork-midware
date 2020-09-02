package com.linkingluck.midware.ormcache.persist;

/**
 * 存储策略.
 * 
 */
public enum PersisterType {

	/** 定时 */
	TIMING,
	/**
	 * 队列存储使用的场合不多(可以靠调快定时队列的频率比如1s,来实现比较及时的存储策略)
	 * 
	 * 暂时不保证这部分的功能正常.不建议生产环境下使用.
	 */
	@Deprecated
	QUEUE;

}
