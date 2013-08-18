package com.kikbak.dao;

import com.kikbak.dto.UserToken;

public interface ReadWriteUserTokenDAO {

	public void makePersistent(UserToken token);
}
