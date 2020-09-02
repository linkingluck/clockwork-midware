package com.linkingluck.midware.ormcache.service;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.anno.CachedEntityConfig;
import com.linkingluck.midware.ormcache.exception.InvalidEntityException;
import com.linkingluck.midware.ormcache.exception.UniqueFieldException;
import com.linkingluck.midware.ormcache.persist.Persister;

import java.io.Serializable;
import java.util.List;

/**
 * 缓存服务接口<br/>
 * 缓存内容使用LRU策略自行清理，使用者不需要负责清理
 * 
 *
 * @param <T>
 *            实体类型
 * @param <PK>
 *            实体的主键类型
 */
public interface EntityCacheService<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> {

	/**
	 * 加载指定主键的实体(同步)
	 * 
	 * @param id
	 *            主键
	 * @return 不存在时返回null
	 */
	T load(PK id);

	/**
	 * 加载指定唯一属性值的实体(同步)
	 * 
	 * @param name
	 *            属性域名
	 * @param value
	 *            属性值
	 * @return 不存在时返回null
	 */
	T unique(String name, Object value);

	/**
	 * 加载指定主键的实体(半异步)
	 * 
	 * @param id
	 *            主键
	 * @param builder
	 *            实体不存在时的实体创建器，允许为null
	 * @return 不会返回null
	 * @throws InvalidEntityException
	 *             无法创建合法的实体时抛出
	 * @throws UniqueFieldException
	 *             实体的唯一属性域值重复时抛出
	 */
	T loadOrCreate(PK id, EntityBuilder<PK, T> builder);

	T create(PK id, EntityBuilder<PK, T> builder);

	/**
	 * 将缓存中的指定实体回写到存储层(异步)
	 * 
	 * @param id
	 *            主键
	 * @param T
	 *            回写实体实例
	 */
	void writeBack(PK id, T entity);

	/**
	 * 移除并删除指定实体(异步)
	 * 
	 * @param id
	 *            主键
	 * @return 缓存中与实体主键相关联的旧实例；如果没有则返回 null。
	 */
	T remove(PK id);

	/**
	 * 清理指定主键的缓存数据
	 * 
	 * @param id
	 *            主键
	 */
	void clear(PK id);

	/**
	 * 获取对应的缓存实体查询器
	 */
	CacheFinder<PK, T> getFinder();

	/**
	 * 获取实体缓存配置信息
	 * 
	 * @return
	 */
	CachedEntityConfig getEntityConfig();

	/**
	 * 检查指定的唯一属性域值是否存在
	 * 
	 * @param name
	 *            属性域名
	 * @param value
	 *            属性值
	 * @return
	 */
	boolean hasUniqueValue(String name, Object value);

	/**
	 * 获取对应的持久化处理器
	 * 
	 * @return
	 */
	Persister getPersister();

	/**
	 * 获取指定命名查询的查询结果(返回实体列表)
	 * @param clz 实体类型
	 * @param queryname 查询名
	 * @param params 查询参数
	 * @return
	 */
	<T> List<T> list(Class<T> clz, String queryname, Object... params);

}
