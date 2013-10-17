package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.v1.devicetoken.DeviceTokenType;
import com.kikbak.jaxb.v2.offer.ClientOfferType;
import com.kikbak.jaxb.v1.register.UserIdType;
import com.kikbak.jaxb.v1.register.UserType;
import com.kikbak.jaxb.v1.userlocation.UserLocationType;

public interface UserService {
		
	public UserIdType registerUser(UserType userType);
    public void updateFriendsList(final long userId, final Collection<Long> friends);
	public Collection<com.kikbak.jaxb.v1.offer.ClientOfferType> getOffers(final Long userId, final UserLocationType userLocation);
	public Collection<com.kikbak.jaxb.v1.offer.ClientOfferType> getOffers(final Long userId, String merchantName);
	public Collection<com.kikbak.jaxb.v1.offer.ClientOfferType> hasOffers(final UserLocationType userLocation);
	public void persistDeviceToken(final Long userId, final DeviceTokenType deviceToken);
	public String getUserToken(long userId);
	public boolean verifyUserToken(long userId, String token);
    public Collection<ClientOfferType> getOffers2(final Long userId, final UserLocationType userLocation);
    public Collection<ClientOfferType> getOffers2(final Long userId, String merchantName);
    public Collection<ClientOfferType> hasOffers2(final UserLocationType userLocation);
	
}
