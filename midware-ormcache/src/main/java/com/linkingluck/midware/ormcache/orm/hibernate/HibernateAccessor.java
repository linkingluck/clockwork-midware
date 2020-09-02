package com.linkingluck.midware.ormcache.orm.hibernate;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.orm.Accessor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.io.Serializable;
import java.util.List;

/**
 * {@link Accessor} 的 Hibernate 实现
 * 
 * @author frank
 */
@SuppressWarnings("rawtypes")
public class HibernateAccessor extends HibernateDaoSupport implements Accessor {

	@Override
	public <PK extends Serializable, T extends IEntity> T load(Class<T> clz, PK id) {
		return execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException {
				session.beginTransaction();
				T object = getHibernateTemplate().get(clz, id);
				session.getTransaction().commit();
				return object;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <PK extends Serializable, T extends IEntity> PK save(Class<T> clz, T entity) {
		return (PK) getHibernateTemplate().save(entity);
	}

	@Override
	public <PK extends Serializable, T extends IEntity> void remove(Class<T> clz, PK id) {
		T entity = load(clz, id);
		if (entity == null) {
			return;
		}
		getHibernateTemplate().delete(entity);
	}

	@Override
	public <PK extends Serializable, T extends IEntity> void update(Class<T> clz, T entity) {
		getHibernateTemplate().update(entity);
	}

	@Override
	public <PK extends Serializable, T extends IEntity> void batchSave(final List<T> entitys) {
		getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				int size = entitys.size();
				session.beginTransaction();
				for (int i = 0; i < size; i++) {
					T eneity = entitys.get(i);
					session.save(eneity);
					if ((i + 1) % 50 == 0) {
						session.flush();
						session.clear();
					}
				}
				session.flush();
				session.clear();
				session.getTransaction().commit();
				return size;
			}
		});
	}

	@Override
	public <PK extends Serializable, T extends IEntity> void batchUpdate(final List<T> entitys) {
		getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				int size = entitys.size();
				session.beginTransaction();
				for (int i = 0; i < size; i++) {
					T eneity = entitys.get(i);
					session.update(eneity);
					if ((i + 1) % 10 == 0) {
						session.flush();
						session.clear();
					}
				}
				session.flush();
				session.clear();
				session.getTransaction().commit();
				return size;
			}
		});
	}

	@Override
	public <PK extends Serializable, T extends IEntity> void batchDelete(final List<T> entitys) {
		getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				int size = entitys.size();
				session.beginTransaction();
				for (int i = 0; i < size; i++) {
					T eneity = entitys.get(i);
					session.delete(eneity);
					if ((i + 1) % 1000 == 0) {
						session.flush();
						session.clear();
					}
				}
				session.flush();
				session.clear();
				session.getTransaction().commit();
				return size;
			}
		});
	}

	public <T> T execute(HibernateCallback<T> action) throws DataAccessException {
		return getHibernateTemplate().execute(action);
	}

}
