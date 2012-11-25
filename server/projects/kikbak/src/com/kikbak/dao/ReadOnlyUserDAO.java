package com.kikbak.dao;


import com.kikbak.dto.User;


public interface ReadOnlyUserDAO {

	public User findById(Long id);
	
	public User findByFacebookId(Long facebookId);
	
}
