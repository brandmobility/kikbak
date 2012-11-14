package com.kikbak.client.services.dao.impl;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.services.dao.ReadOnlyUserDAO;
import com.kikbak.dao.generic.ReadOnlyGenericDAOImpl;
import com.kikbak.dto.User;

public class ReadOnlyUserDAOImpl extends ReadOnlyGenericDAOImpl<User, Long> implements ReadOnlyUserDAO{

	private final CommonUserDAOImpl impl;
	
	public ReadOnlyUserDAOImpl() {
		impl = new CommonUserDAOImpl(this);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
	public User findByFacebookId(Long facebookId){
		return impl.findByFacebookId(facebookId);
	}
	
}
