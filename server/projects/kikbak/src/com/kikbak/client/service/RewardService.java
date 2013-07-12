package com.kikbak.client.service;

import java.util.Collection;
import java.util.List;

import com.kikbak.jaxb.redeemcredit.CreditRedemptionResponseType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionType;
import com.kikbak.jaxb.redeemgift.GiftRedemptionType;
import com.kikbak.jaxb.rewards.AvailableCreditType;
import com.kikbak.jaxb.rewards.ClaimStatusType;
import com.kikbak.jaxb.rewards.GiftType;

public interface RewardService {

	Collection<GiftType> getGifts(final Long userId) throws RewardException;
	Collection<AvailableCreditType> getCredits(final Long userId) throws RewardException;
	String registerGiftRedemption(final Long userId, final GiftRedemptionType giftType) throws RedemptionException, RateLimitException;
	CreditRedemptionResponseType redeemCredit(final Long userId, final CreditRedemptionType creditType) throws RedemptionException;
    ClaimStatusType claimGift(Long userId, String referralCode, List<GiftType> gifts) throws RewardException;
}
