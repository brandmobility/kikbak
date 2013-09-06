package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.devicetoken.DeviceTokenType;
import com.kikbak.jaxb.offer.ClientOfferType;
import com.kikbak.jaxb.register.UserIdType;
import com.kikbak.jaxb.register.UserType;
import com.kikbak.jaxb.userlocation.UserLocationType;

public interface UserService {
		
	public UserIdType registerUser(UserType userType);
    public void updateFriendsList(final long userId, final Collection<Long> friends);
	public Collection<ClientOfferType> getOffers(final Long userId, final UserLocationType userLocation);
	public boolean hasOffers(final UserLocationType userLocation);
	public void persistDeviceToken(final Long userId, final DeviceTokenType deviceToken);
	public String getUserToken(long userId);
	public boolean verifyUserToken(long userId, String token);

}
