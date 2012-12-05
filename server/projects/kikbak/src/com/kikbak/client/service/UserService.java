package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.DeviceTokenType;
import com.kikbak.jaxb.FriendType;
import com.kikbak.jaxb.OfferType;
import com.kikbak.jaxb.UserIdType;
import com.kikbak.jaxb.UserLocationType;
import com.kikbak.jaxb.UserType;

public interface UserService {
		
	public UserIdType registerUser(UserType userType);
	public void updateFriends(final long userId, final Collection<FriendType> friends);
	public Collection<OfferType> getOffers(final Long userId, final UserLocationType userLocation);
	public void persistDeviceToken(final Long userId, final DeviceTokenType deviceToken);

}
