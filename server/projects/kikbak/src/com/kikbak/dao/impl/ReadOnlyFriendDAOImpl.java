package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyFriendDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.Friend;

@Repository
public class ReadOnlyFriendDAOImpl extends ReadOnlyGenericDAOImpl<Friend, Long> implements ReadOnlyFriendDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public Friend findByFacebookId(Long facebookId) {
		return findByCriteria(Restrictions.eq("facebookId", facebookId));
	}

}
