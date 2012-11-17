package com.kikbak.admin.dao;

import com.kikbak.dto.Account;

public interface ReadOnlyAccountDAO{

	public Account findById(Long id);
	
	public Account findByEmailAndPassword(String email, String password);
}
