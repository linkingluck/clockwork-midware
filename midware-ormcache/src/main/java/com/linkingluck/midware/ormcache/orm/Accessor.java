package com.linkingluck.midware.ormcache.orm;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.exception.DataException;
import com.linkingluck.midware.ormcache.exception.EntityExistsException;
import com.linkingluck.midware.ormcache.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.List;

/**
 * 物理存储层数据访问器接口<br/>
 * 用于给不同的ORM底层实现
 * 
 */
@SuppressWarnings("rawtypes")
public interface Accessor {

	// CRUD 方法部分

	/**
	 * 从存储层加载指定的实体对象实例
	 * 
	 * @param clz
	 *            实体类型
	 * @param id
	 *            实体主键
	 * @return 实体实例,不存在应该返回null
	 */
	<PK extends Serializable, T extends IEntity> T load(Class<T> clz, PK id);

	/**
	 * 持久化指定的实体实例,并返回实体的主键值对象
	 * 
	 * @param clz
	 *            实体类型
	 * @param entity
	 *            被持久化的实体实例(当持久化成功时,该实体的主键必须被设置为正确的主键值)
	 * @return 持久化实体的主键值对象
	 * @throws EntityExistsException
	 *             实体已经存在时抛出
	 * @throws DataException
	 *             实体数据不合法时抛出
	 */
	<PK extends Serializable, T extends IEntity> PK save(Class<T> clz, T entity);

	/**
	 * 从存储层移除指定实体
	 * 
	 * @param clz
	 *            实体类型
	 * @param id
	 *            实体主键
	 */
	<PK extends Serializable, T extends IEntity> void remove(Class<T> clz, PK id);

	/**
	 * 更新存储层的实体数据(不允许更新实体的主键值)
	 * 
	 * @param entity
	 *            被更新实体对象实例
	 * @param clz
	 *            实体类型
	 * @throws EntityNotFoundException
	 *             被更新实体在存储层不存在时抛出
	 */
	<PK extends Serializable, T extends IEntity> void update(Class<T> clz, T entity);

	/**
	 * 批量存储
	 * 
	 * @param entitys
	 *            被存储的集合
	 */
	<PK extends Serializable, T extends IEntity> void batchSave(final List<T> entitys);

	/**
	 * 批量更新
	 * 
	 * @param entitys
	 *            被更新的集合
	 */
	<PK extends Serializable, T extends IEntity> void batchUpdate(final List<T> entitys);

	/**
	 * 批量删除
	 * 
	 * @param entitys
	 *            被更新的集合
	 */
	<PK extends Serializable, T extends IEntity> void batchDelete(final List<T> entitys);
}
