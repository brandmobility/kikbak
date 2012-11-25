package com.kikbak.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.User;

@Repository
public class ReadOnlyUserDAOImpl extends ReadOnlyGenericDAOImpl<User, Long> implements ReadOnlyUserDAO{

	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public User findByFacebookId(Long facebookId){
		return findByCriteria(Restrictions.eq("facebookId", facebookId));
	}
	
}
