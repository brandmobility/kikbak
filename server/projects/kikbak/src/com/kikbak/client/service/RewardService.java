package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.claim.ClaimType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionResponseType;
import com.kikbak.jaxb.redeemcredit.CreditRedemptionType;
import com.kikbak.jaxb.redeemgift.GiftRedemptionType;
import com.kikbak.jaxb.rewards.AvailableCreditType;
import com.kikbak.jaxb.rewards.GiftType;

public interface RewardService {

	public Collection<GiftType> getGifts(final Long userId);
	public Collection<AvailableCreditType> getCredits(final Long userId);
	public String registerGiftRedemption(final Long userId, final GiftRedemptionType giftType) throws Exception;
	public CreditRedemptionResponseType redeemCredit(final Long userId, final CreditRedemptionType creditType) throws Exception;
	public void claimCredit(final Long userId, final ClaimType claim) throws Exception;
	public String getBarcode(final Long userId, final Long allocatedGiftId)throws Exception;
}
