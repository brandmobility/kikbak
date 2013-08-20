package com.kikbak.dao;

import com.kikbak.dto.UserToken;

public interface ReadOnlyUserTokenDAO{

	public UserToken findById(Long id);

	public UserToken findByUserId(long userId);

}
