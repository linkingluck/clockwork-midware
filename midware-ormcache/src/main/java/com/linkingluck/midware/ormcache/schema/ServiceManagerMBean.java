package com.linkingluck.midware.ormcache.schema;

import com.linkingluck.midware.ormcache.anno.CachedEntityConfig;

import java.util.Map;

/**
 * 缓存服务管理的JMX接口
 * @author frank
 */
public interface ServiceManagerMBean {

	/**
	 * 获取全部持久化处理器的当前状态信息
	 * @return
	 */
	Map<String, Map<String, String>> getAllPersisterInfo();
	
	/**
	 * 获取指定的持久化处理器的当前状态信息
	 * @param name 持久化处理器名
	 * @return
	 */
	Map<String, String> getPersisterInfo(String name);
	
	/**
	 * 获取全部的缓存实体配置信息
	 * @return
	 */
	Map<String, CachedEntityConfig> getAllCachedEntityConfig();
}
