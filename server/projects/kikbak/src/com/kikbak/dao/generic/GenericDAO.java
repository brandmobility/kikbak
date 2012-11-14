package com.kikbak.dao.generic;

import java.io.Serializable;

public interface GenericDAO<T, ID extends Serializable> extends ReadOnlyGenericDAO<T, ID> {

    public void makePersistent(T entity);

    public void makeTransient(T entity);
    
    public boolean isReadOnly();
    
    public Object persistEntityWithCompositeKey(T entity);
}
