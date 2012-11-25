package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.User2friend;

public interface ReadOnlyUser2FriendDAO {

	public User2friend findById(Long id);
	
	public Collection<User2friend> listFriendsForUser(Long userId);
}
