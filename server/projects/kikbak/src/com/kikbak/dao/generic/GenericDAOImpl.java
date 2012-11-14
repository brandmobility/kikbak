package com.kikbak.dao.generic;

import java.io.Serializable;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public abstract class GenericDAOImpl<T, ID extends Serializable> extends ReadOnlyGenericDAOImpl<T, ID> implements GenericDAO<T, ID> {

    protected GenericDAOImpl() {
        super();
    }
    
    protected GenericDAOImpl(Class<T> type) {
        super(type);
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void makePersistent(T entity) {
        Session session = getSessionFactory().getCurrentSession();
        session.saveOrUpdate(entity);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void makeTransient(T entity) {
        Session session = getSessionFactory().getCurrentSession();
        session.delete(entity);
        session.flush();
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object persistEntityWithCompositeKey(T entity) {
        Session session = getSessionFactory().getCurrentSession();
        session.saveOrUpdate(entity);
        session.flush();
        SQLQuery sqlQuery = session.createSQLQuery("SELECT LAST_INSERT_ID()");
        return sqlQuery.uniqueResult();
    }

    protected SessionFactory readOnlySessionFactory;
    
    public void setReadOnlySessionFactory(SessionFactory readOnlySessionFactory) {
        this.readOnlySessionFactory = readOnlySessionFactory;
    }

    public SessionFactory getReadOnlySessionFactory() {
        return readOnlySessionFactory;
    }
    
    boolean readonly = false;

    protected SessionFactory readWriteSessionFactory = null;

    public boolean getReadOnly() {
        return readonly;
    }
    
    public boolean isReadOnly() {
        return getReadOnly();
    }

    public void setReadOnly(boolean readonly) {
        this.readonly = readonly;
        configSessionFactory();
    }

    synchronized void configSessionFactory() {
        if ( readonly ) {
            readWriteSessionFactory = sessionFactory;
            sessionFactory = readOnlySessionFactory;
        }
        else {
            if ( null == readWriteSessionFactory ) {
                readWriteSessionFactory = sessionFactory;
            }
            sessionFactory = readWriteSessionFactory;
        }
    }
}