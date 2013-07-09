package com.kikbak.dao;

import com.kikbak.dto.Credit;

public interface ReadWriteCreditDAO {
	
	public void makePersistent(Credit kikbak);
	public void makeTransient(Credit kikbak);
	
}
