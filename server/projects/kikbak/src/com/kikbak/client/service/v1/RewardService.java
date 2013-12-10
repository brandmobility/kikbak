package com.kikbak.client.service.v1;

import java.util.Collection;
import java.util.List;

import com.kikbak.jaxb.v1.barcode.BarcodeResponse;
import com.kikbak.jaxb.v1.claim.ClaimType;
import com.kikbak.jaxb.v1.redeemcredit.CreditRedemptionResponseType;
import com.kikbak.jaxb.v1.redeemcredit.CreditRedemptionType;
import com.kikbak.jaxb.v1.redeemgift.GiftRedemptionType;
import com.kikbak.jaxb.v1.rewards.AvailableCreditType;
import com.kikbak.jaxb.v1.rewards.ClaimStatusType;
import com.kikbak.jaxb.v1.rewards.GiftType;

public interface RewardService {

    Collection<GiftType> getGifts(final Long userId) throws RewardException;
    Collection<AvailableCreditType> getCredits(final Long userId) throws RewardException;
    String registerGiftRedemption(final Long userId, final GiftRedemptionType giftType) throws RedemptionException, RateLimitException;
    CreditRedemptionResponseType redeemCredit(final Long userId, final CreditRedemptionType creditType) throws RedemptionException;
    ClaimStatusType claimGift(Long userId, String referralCode, List<GiftType> gifts, List<Long> agIds, String refererHost) throws RewardException;
    void claimCredit(final Long userId, final ClaimType claim) throws Exception;
    void getBarcode(final Long userId, final Long allocatedGiftId, BarcodeResponse barcodeResponse)throws Exception;
    GiftType getGiftByReferredCode(final String code) throws RewardException;
    GiftType getLastGiftByCreditId(final long creditId) throws RewardException;
    AvailableCreditType getCreditByCode(final String code) throws RewardException;
    String getBarcode(String referralCode, BarcodeResponse response) throws OfferExpiredException, OfferExhaustedException;
}
