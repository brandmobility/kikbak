package com.kikbak.dao;

import com.kikbak.dto.Redeemzipcode;

public interface ReadWriteRedeemZipcodeDAO {

	public void makePersistent(Redeemzipcode offer);
	public void makeTransient(Redeemzipcode offer);
	
}
