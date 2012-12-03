package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Gift;

public interface ReadOnlyGiftDAO {
	
	public Gift findById(Long id);
	public Collection<Gift> listByUserId(Long userId);
	public Collection<Gift> listByMerchantId(Long merchantId);
	public Collection<Gift> listByOfferId(Long offerId);
	public Collection<Long> listOfferIdsForUser(Long userId);
}
