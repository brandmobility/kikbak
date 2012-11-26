package com.kikbak.dao;

import com.kikbak.dto.Transaction;

public interface ReadWriteTransactionDAO {

	public void makePersistent(Transaction transaction);
	public void makeTransient(Transaction transaction);
	
}
