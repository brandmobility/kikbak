package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.User2friend;

public interface ReadOnlyUser2FriendDAO {

	public User2friend findById(Long id);
	public Collection<Long> listFriendsForUser(Long userId);
	public Collection<Long> listFriendsToDelete(Long userId, Collection<Long> facebookIds);

	/**
	 * List user ids of friends. 
	 */
    public Collection<Long> listFriends(Long userId);

}
