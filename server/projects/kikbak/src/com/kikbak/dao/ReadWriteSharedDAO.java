package com.kikbak.dao;

import com.kikbak.dto.Shared;


public interface ReadWriteSharedDAO {

	public void makePersistent(Shared shared);
	public void makeTransient(Shared shared);
}
