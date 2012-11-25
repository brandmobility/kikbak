package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.User2friend;


public interface ReadWriteUser2FriendDAO {

	public void makePersistent(User2friend friend);
	
	public void batchInsert(Collection<User2friend> friends);
}
