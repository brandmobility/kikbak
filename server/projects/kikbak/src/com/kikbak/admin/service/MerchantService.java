package com.kikbak.admin.service;

import java.util.Collection;

import com.kikbak.jaxb.LocationType;
import com.kikbak.jaxb.MerchantType;
import com.kikbak.jaxb.OfferType;

public interface MerchantService {

	public Collection<MerchantType> getMerchants() throws Exception;
	
	public MerchantType addOrUpdateMerchant(MerchantType mt) throws Exception;
	
	public LocationType addOrUpdateLocation(LocationType lt);
	
	public OfferType addOrUpdateOffer(OfferType ot);
	
	public Collection<OfferType> getOffersByMerchant(MerchantType mt) throws Exception;
	
}
