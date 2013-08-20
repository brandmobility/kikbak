package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyUserTokenDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.UserToken;

@Repository
public class ReadOnlyUserTokenDAOImpl extends ReadOnlyGenericDAOImpl<UserToken, Long> implements ReadOnlyUserTokenDAO{

	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public UserToken findByUserId(long userId) {
		return findByCriteria(Restrictions.eq("userId", userId));
	}

}
