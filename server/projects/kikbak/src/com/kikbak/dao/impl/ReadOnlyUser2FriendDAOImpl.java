package com.kikbak.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyUser2FriendDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.User2friend;

public class ReadOnlyUser2FriendDAOImpl extends ReadOnlyGenericDAOImpl<User2friend, Long> implements ReadOnlyUser2FriendDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Collection<User2friend> listFriendsForUser(Long userId) {
		return listByCriteria(Restrictions.eq("userId", userId));
	}

}
