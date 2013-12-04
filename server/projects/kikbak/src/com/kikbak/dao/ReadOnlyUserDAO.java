package com.kikbak.dao;


import java.util.Collection;

import com.kikbak.dto.User;


public interface ReadOnlyUserDAO {

	public User findById(Long id);
	
	public User findByFacebookId(Long facebookId);
	
    public Collection<Long> listEligibleForOfferFromUser(Long offerId, Long fromUserId);
    
    public User findByManualPhoneNotFb(String phone);
    
}
