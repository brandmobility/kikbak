package com.kikbak.dao;

import java.math.BigInteger;
import java.util.Collection;

import com.kikbak.dto.User2friend;


public interface ReadWriteUser2FriendDAO {

	public void makePersistent(User2friend friend);
	public void makeTransient(User2friend friend);
	public void batchInsert(final Collection<User2friend> friends);
	public void batchDelete(final Long userId, final Collection<BigInteger> ids);
}
