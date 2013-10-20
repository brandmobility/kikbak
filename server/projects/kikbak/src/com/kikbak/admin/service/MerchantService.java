package com.kikbak.admin.service;

import java.util.Collection;

import com.kikbak.jaxb.v1.admin.LocationType;
import com.kikbak.jaxb.v1.admin.MerchantType;
import com.kikbak.jaxb.v1.admin.OfferType;

public interface MerchantService {

	public Collection<MerchantType> getMerchants();
	public MerchantType addOrUpdateMerchant(MerchantType mt) throws Exception;
	public LocationType addOrUpdateLocation(LocationType lt);
	public OfferType addOrUpdateOffer(OfferType ot);
	public Collection<OfferType> getOffersByMerchant(MerchantType mt) throws Exception;
	
}
