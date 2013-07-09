package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.redeemgift.GiftRedemptionType;
import com.kikbak.jaxb.redeemkikbak.KikbakRedemptionResponseType;
import com.kikbak.jaxb.redeemkikbak.KikbakRedemptionType;
import com.kikbak.jaxb.rewards.GiftType;
import com.kikbak.jaxb.rewards.KikbakType;

public interface RewardService {

	public Collection<GiftType> getGifts(final Long userId);
	public Collection<KikbakType> getKikbaks(final Long userId);
	public String registerGiftRedemption(final Long userId, final GiftRedemptionType giftType) throws Exception;
	public KikbakRedemptionResponseType registerKikbakRedemption(final Long userId, final KikbakRedemptionType kikbakType) throws Exception;
}
