package com.kikbak.dao;

import com.kikbak.dto.User;

public interface ReadWriteUserDAO{

	public void makePersistent(User entity);
	
}
