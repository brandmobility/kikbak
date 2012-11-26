package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyFriendDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Friend;

public class ReadOnlyFriendDAOImpl extends ReadOnlyGenericDAOImpl<Friend, Long> implements ReadOnlyFriendDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Friend findByFacebookId(Long facebookId) {
		return findByCriteria(Restrictions.eq("facebookId", facebookId));
	}

}
