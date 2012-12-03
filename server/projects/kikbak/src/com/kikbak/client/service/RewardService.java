package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.GiftRedemptionType;
import com.kikbak.jaxb.GiftType;
import com.kikbak.jaxb.KikbakRedemptionType;
import com.kikbak.jaxb.KikbakType;

public interface RewardService {

	public Collection<GiftType> getGifts(final Long userId);
	public Collection<KikbakType> getKikbaks(final Long userId);
	public String registerGiftRedemption(final Long userId, final GiftRedemptionType giftType) throws Exception;
	public String registerKikbakRedemption(final Long userId, final KikbakRedemptionType kikbakType) throws Exception;
}
