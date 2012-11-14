package com.kikbak.client.services.dao;

import com.kikbak.dto.User;

public interface ReadWriteUserDAO extends ReadOnlyUserDAO{

	public void makePersistent(User entity);
}
