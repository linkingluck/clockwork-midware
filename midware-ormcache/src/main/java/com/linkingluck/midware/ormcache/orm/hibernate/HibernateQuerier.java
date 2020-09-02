package com.linkingluck.midware.ormcache.orm.hibernate;

import com.linkingluck.midware.ormcache.orm.Paging;
import com.linkingluck.midware.ormcache.orm.Querier;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.List;

/**
 * {@link Querier} 的 Hibernate 实现
 * @author frank
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HibernateQuerier extends HibernateDaoSupport implements Querier {

	@Override
	public <T> List<T> all(Class<T> clz) {
		return getHibernateTemplate().loadAll(clz);
	}

	@Override
	public <T> List<T> list(Class<T> clz, String queryname, Object... params) {
		return (List<T>) getHibernateTemplate().findByNamedQuery(queryname, params);
	}

	@Override
	public <E> List<E> list(Class entityClz, Class<E> retClz, String queryname, Object... params) {
		return (List<E>) getHibernateTemplate().findByNamedQuery(queryname, params);
	}

	@Override
	public <T> T unique(Class<T> clz, final String queryname, final Object... params) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException {
				Query query = session.getNamedQuery(queryname);
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
				return (T) query.uniqueResult();
			}
		});
	}

	@Override
	public <E> E unique(Class entityClz, Class<E> retClz, final String queryname, final Object... params) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<E>() {
			@Override
			public E doInHibernate(Session session) throws HibernateException {
				Query query = session.getNamedQuery(queryname);
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
				return (E) query.uniqueResult();
			}
		});
	}

	@Override
	public <T> List<T> paging(Class<T> clz, final String queryname, final Paging paging, final Object... params) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session) throws HibernateException {
				Query query = session.getNamedQuery(queryname);
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
				query.setFirstResult(paging.getFirst());
				query.setMaxResults(paging.getSize());
				return query.list();
			}
		});
	}

	@Override
	public <E> List<E> paging(Class entityClz, Class<E> retClz, final String queryname, final Paging paging,
			final Object... params) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<E>>() {
			@Override
			public List<E> doInHibernate(Session session) throws HibernateException {
				Query query = session.getNamedQuery(queryname);
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
				query.setFirstResult(paging.getFirst());
				query.setMaxResults(paging.getSize());
				return query.list();
			}
		});
	}

}
