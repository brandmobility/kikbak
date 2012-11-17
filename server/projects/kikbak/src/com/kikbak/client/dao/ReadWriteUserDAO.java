package com.kikbak.client.dao;

import com.kikbak.dto.User;

public interface ReadWriteUserDAO{

	public void makePersistent(User entity);
}
