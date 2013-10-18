package com.kikbak.client.service.v2;

import java.util.Collection;

import com.kikbak.client.service.v1.UserService;
import com.kikbak.jaxb.v1.userlocation.UserLocationType;
import com.kikbak.jaxb.v2.offer.ClientOfferType;

public interface UserService2 extends UserService{
    public Collection<ClientOfferType> getOffers2(final Long userId, final UserLocationType userLocation);
//    public Collection<ClientOfferType> getOffers2(final Long userId, String merchantName);
//    public Collection<ClientOfferType> hasOffers2(final UserLocationType userLocation);
}
