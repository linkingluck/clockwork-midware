package com.linkingluck.midware.ormcache.service;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.anno.CachedEntityConfig;

import java.io.Serializable;

/**
 * 增强服务接口
 * @author frank
 *
 * @param <PK>
 * @param <T>
 */
public interface EnhanceService<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> {

	/**
	 * 将缓存中的指定实体回写到存储层(异步)
	 * @param id 主键
	 * @param T 回写实体实例
	 */
	void writeBack(PK id, T entity);

	/**
	 * 获取实体缓存配置信息
	 * @return
	 */
	CachedEntityConfig getEntityConfig();

}
