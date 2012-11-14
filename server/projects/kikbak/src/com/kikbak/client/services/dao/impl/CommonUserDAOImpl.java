package com.kikbak.client.services.dao.impl;

import org.hibernate.criterion.Restrictions;

import com.kikbak.client.services.dao.ReadOnlyUserDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.User;

public class CommonUserDAOImpl implements ReadOnlyUserDAO {
	
	private final ReadOnlyGenericDAOImpl<User, Long> baseDAO;

	protected CommonUserDAOImpl( ReadOnlyGenericDAOImpl<User, Long> baseDAO ){
		this.baseDAO = baseDAO;
	}
	
	
	@Override
	public User findById(Long id) {
		return baseDAO.findById(id);
	}

	@Override
	public User findByFacebookId(Long facebookId) {
		return baseDAO.findByCriteria(Restrictions.eq("facebookId", facebookId));
	}
		
}
