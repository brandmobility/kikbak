package com.kikbak.dao.generic;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class ReadOnlyGenericDAOImpl<T, ID extends Serializable> implements ReadOnlyGenericDAO<T, ID> {
    
    protected SessionFactory sessionFactory;

    protected Logger log = Logger.getLogger(getClass());

    private Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    protected ReadOnlyGenericDAOImpl() {
        Class<?> c = getClass();
        Class<?> sc = c.getSuperclass();
        while ((sc != ReadOnlyGenericDAOImpl.class) && (sc != GenericDAOImpl.class)) {
            c = sc;
            sc = c.getSuperclass();
        }

        ParameterizedType pt = (ParameterizedType) c.getGenericSuperclass();
        this.persistentClass = (Class<T>) pt.getActualTypeArguments()[0];
    }
    
    protected ReadOnlyGenericDAOImpl(Class<T> type) {
        this.persistentClass = type;
    }
    
    protected Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public T findById(ID id) {
        Session session = sessionFactory.getCurrentSession();

        return (T) session.get(getPersistentClass(), id);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<T> listAll() {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("from " + getPersistentClass().getName());
        return query.list();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public int size() {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("select count(*) from " + getPersistentClass().getName());
        return ((Long) query.uniqueResult()).intValue();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<T> listAll(int firstResult, int maxResults) {
        return restrictedListByCriteria(firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<T> listByCriteria(Criterion... criterion) {
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(getPersistentClass());
        for (Criterion c : criterion)
            crit.add(c);

        return crit.list();
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public Collection<T> listByCriteriaAndFetch(Map<String, FetchMode> fetchModeMap, Criterion... criterion) {
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(getPersistentClass());
        for (Criterion c : criterion){
            criteria.add(c);
        }
        
        if(fetchModeMap != null) {
            for (Entry<String, FetchMode> entry : fetchModeMap.entrySet()) {
                if(entry.getKey() != null && entry.getValue() != null) {
                    criteria.setFetchMode(entry.getKey(), entry.getValue());
                }
            }
        }
        return criteria.list();
	}

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Collection<T> restrictedListByCriteria(int firstResult, int maxResults, Criterion... criterion) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(getPersistentClass());
        for (Criterion c : criterion)
            crit.add(c);
        crit.setMaxResults(maxResults);
        crit.setFirstResult(firstResult);
        return crit.list();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public T findByCriteria(Criterion... criterion) {
        return findByCriteriaAndFetch(null, criterion);
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public T findByCriteriaAndFetch(Map<String, FetchMode> fetchModeMap, Criterion... criterion) {
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        
        if(fetchModeMap != null) {
            for (Entry<String, FetchMode> entry : fetchModeMap.entrySet()) {
                if(entry.getKey() != null && entry.getValue() != null) {
                    crit.setFetchMode(entry.getKey(), entry.getValue());
                }
            }
        }
        
        // The following throws HibernateException if multiple results are found.
        return (T) crit.uniqueResult();
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<?> executeQuery(String query, int limit) {
    	Session session = sessionFactory.getCurrentSession();
    	
    	Query hbQuery = session.createQuery(query);
    	hbQuery.setMaxResults(limit);  
    	
    	List<?> list = hbQuery.list();
    	
    	return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public long maxId() {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("select max(id) from " + getPersistentClass().getName());
        if (null == query || null == query.uniqueResult()) {
            return 0;
        }
        
        return ((Long) query.uniqueResult()).longValue();
    }
}
