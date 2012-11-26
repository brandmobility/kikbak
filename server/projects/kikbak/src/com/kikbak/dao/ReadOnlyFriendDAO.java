package com.kikbak.dao;

import com.kikbak.dto.Friend;

public interface ReadOnlyFriendDAO {
	
	public Friend findByFacebookId(Long facebookId);

}
