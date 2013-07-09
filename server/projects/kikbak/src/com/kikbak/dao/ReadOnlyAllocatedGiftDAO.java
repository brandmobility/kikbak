package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Allocatedgift;

public interface ReadOnlyAllocatedGiftDAO {
	
	public Allocatedgift findById(Long id);
	public Collection<Allocatedgift> listValidByUserId(Long userId);
	public Collection<Allocatedgift> listByMerchantId(Long merchantId);
	public Collection<Allocatedgift> listByOfferId(Long offerId);
	public Collection<Long> listOfferIdsForUser(Long userId);
	public Collection<Allocatedgift> listByFriendUserId(Long friendId);
}
