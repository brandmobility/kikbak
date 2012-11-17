package com.kikbak.admin.dao;

import com.kikbak.dto.Merchant;

public interface ReadWriteMerchantDAO {

	public void makePersistent(Merchant merchant);
	
	public void makeTransient(Merchant merchant);
}
