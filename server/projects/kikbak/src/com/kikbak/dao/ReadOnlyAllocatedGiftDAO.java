package com.kikbak.dao;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.kikbak.dto.Allocatedgift;

public interface ReadOnlyAllocatedGiftDAO {
	
	public Allocatedgift findById(Long id);
	public Collection<Allocatedgift> listValidByUserId(Long userId);
	public Collection<Allocatedgift> listByMerchantId(Long merchantId);
	public Collection<Allocatedgift> listByOfferId(Long offerId);
	public Multimap<Long,Long> listOfferIdsByFriendsForUser(Long userId);
	public Collection<Long> listSharedIdsForUser(Long userId);
	public Collection<Allocatedgift> listByFriendUserId(Long friendId);
	public Collection<Allocatedgift> listValidByUserIdAndSharedId(Long userId, long sharedId);
}
