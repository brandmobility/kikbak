package com.kikbak.dao;

import com.kikbak.dto.Shared;


public interface ReadWriteSharedDAO extends ReadOnlySharedDAO {

	public void makePersistent(Shared shared);
	public void makeTransient(Shared shared);
}
