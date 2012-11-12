package com.kikbak.dao.generic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.FetchMode;
import org.hibernate.criterion.Criterion;


public interface ReadOnlyGenericDAO<T, ID extends Serializable> {	
    public SessionFactory getSessionFactory();
    
    public T findById(ID id);

    public Collection<T> listAll();

    public int size();

    public Collection<T> listAll(int firstResult, int maxResults);

    public Collection<T> listByCriteria(Criterion... criterion);
    
    public Collection<T> listByCriteriaAndFetch(Map<String, FetchMode> fetchModeMap, Criterion... criterion);

    public Collection<T> restrictedListByCriteria(int firstResult, int maxResults, Criterion... criterion);

    public T findByCriteria(Criterion... criterion);
    
    public List<?> executeQuery(String query, int limit); 
    
    public long maxId();

    public T findByCriteriaAndFetch(Map<String, FetchMode> fetchModeMap, Criterion... criterion);
    
}
