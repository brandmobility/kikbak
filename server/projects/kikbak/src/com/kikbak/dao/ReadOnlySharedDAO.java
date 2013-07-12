package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Shared;


public interface ReadOnlySharedDAO {

	public Shared findById(Long id);
	public Collection<Shared> listByUserId(Long userId);
	public Collection<Shared> listByLocationId(Long locationId);
	public Collection<Shared> listByUserIdAndOfferId(Long userId, Long offerId);
	public Collection<Shared> listAvailableForGifting(Long userId);
    public Collection<Shared> listAvailableForGiftingByReferralCode(String referralCode);
}
