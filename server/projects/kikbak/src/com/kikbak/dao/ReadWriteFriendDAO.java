package com.kikbak.dao;

import com.kikbak.dto.Friend;

public interface ReadWriteFriendDAO {

	public void makePersistent(Friend friend);
	public void makeTransient(Friend friend);
}
