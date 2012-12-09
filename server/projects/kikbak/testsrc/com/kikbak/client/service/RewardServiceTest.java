package com.kikbak.client.service;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadWriteGiftDAO;
import com.kikbak.dao.ReadWriteLocationDAO;
import com.kikbak.dao.ReadWriteMerchantDAO;
import com.kikbak.dao.ReadWriteOfferDAO;
import com.kikbak.dao.ReadWriteSharedDAO;
import com.kikbak.dao.ReadWriteUser2FriendDAO;
import com.kikbak.dto.Gift;
import com.kikbak.jaxb.ClientLocationType;
import com.kikbak.jaxb.GiftRedemptionType;
import com.kikbak.jaxb.GiftType;
import com.kikbak.jaxb.KikbakRedemptionType;
import com.kikbak.jaxb.KikbakType;
import com.kikbak.service.helper.DataPopulator;

public class RewardServiceTest extends KikbakBaseTest{
	
	@Autowired
	RewardService service;
	
	@Autowired
	ReadWriteOfferDAO rwOfferDao;
	
	@Autowired
	ReadWriteMerchantDAO rwMerchantDao;
	
	@Autowired
	ReadWriteUser2FriendDAO rwU2FDao;
	
	@Autowired
	ReadWriteSharedDAO rwSharedDao;
	
	@Autowired
	ReadWriteGiftDAO rwGiftDao;
	
	@Autowired
	ReadWriteLocationDAO rwLocationDao;
	
	@Autowired
	ReadOnlyGiftDAO roGiftDao;
	
	@Test
	public void testGetGifts(){
		DataPopulator populator = new DataPopulator(rwOfferDao, rwMerchantDao, rwU2FDao, rwSharedDao, rwLocationDao);
		populator.pupulateDataForGiftTest(2, 32);
		
		Collection<GiftType> gifts = service.getGifts(32L);
		assertEquals(2, gifts.size());
	}
	
	@Test
	public void testGiftsWithExistingGift(){
		DataPopulator populator = new DataPopulator(rwOfferDao, rwMerchantDao, rwU2FDao, rwSharedDao, rwLocationDao);
		populator.pupulateDataForGiftTest(2, 32);
		
		Gift gift = new Gift();
		Date now = new Date();
		gift.setExperirationDate(new Date(now.getTime() + 999999999));
		gift.setFriendUserId(16);
		gift.setMerchantId(1);
		gift.setOfferId(1);
		gift.setUserId(32);
		gift.setValue(12.21);
		rwGiftDao.makePersistent(gift);
		
		Collection<GiftType> gifts = service.getGifts(32L);
		assertEquals(3, gifts.size());
	}
	
	@Test
	public void testGetKikbaks(){
		DataPopulator populator = new DataPopulator(rwOfferDao, rwMerchantDao, rwU2FDao, rwSharedDao, rwLocationDao);
		populator.pupulateDataForGiftTest(2, 32);
		
		Collection<GiftType> gifts = service.getGifts(32L);
		GiftType gift = (GiftType) gifts.toArray()[0];
		ClientLocationType clt = (ClientLocationType) gift.getMerchant().getLocations().toArray()[0];
		
		GiftRedemptionType grt = new GiftRedemptionType();
		grt.setId(gift.getId());
		grt.setLocationId(clt.getId());
		grt.setVerificationCode("12345");
		
		try {
			service.registerGiftRedemption(32L, grt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		Gift giftDto = roGiftDao.findById(gift.getId());
		
		Collection<KikbakType> krt = service.getKikbaks(giftDto.getFriendUserId());
		assertEquals(1, krt.size());
	}
	
	@Test
	public void testRegisterGiftRedemption(){
		GiftRedemptionType grt = new GiftRedemptionType();
		grt.setId(1);
		grt.setLocationId(1);
		grt.setVerificationCode("4343");
		
		try {
			service.registerGiftRedemption(1L, grt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRegisterKikbakRedemption(){
		KikbakRedemptionType krt = new KikbakRedemptionType();
		krt.setLocationId(1);
		krt.setVerificationCode("4343");
		krt.setAmount(5);
		krt.setId(1);
		
		try {
			service.registerKikbakRedemption(1L, krt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
