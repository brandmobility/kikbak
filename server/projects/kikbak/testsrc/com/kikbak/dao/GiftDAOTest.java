package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Gift;

public class GiftDAOTest extends KikbakBaseTest {

	@Autowired
	ReadOnlyGiftDAO roDao;
	
	@Autowired
	ReadWriteGiftDAO rwDao;

	
	@Test
	public void testListByUserId(){
		Collection<Gift> gifts = roDao.listByUserId(12L);
		assertEquals(1, gifts.size());
	}
	
	@Test
	public void testListByMerchantId(){
		Collection<Gift> gifts = roDao.listByMerchantId(3L);
		assertEquals(2, gifts.size());
	}
	
	@Test
	public void testListByOfferId(){
		Collection<Gift> gifts = roDao.listByOfferId(13L);
		assertEquals(1, gifts.size());
	}
	
	@Test 
	public void testListOfferIdsForUser(){
		Collection<Long> offerIds = roDao.listOfferIdsForUser(12L);	
		assertEquals(1, offerIds.size());
	}
	
	@Test
	public void testListByFriendUserId(){
		Collection<Gift> gifts = roDao.listByFriendUserId(13L);
		assertEquals(2, gifts.size());
	}
	@Test
	public void testWriteGift(){
		Gift gift = new Gift();
		gift.setExperirationDate(new Date());
		gift.setFriendUserId(12);
		gift.setMerchantId(13);
		gift.setOfferId(144);
		gift.setUserId(6363);
		gift.setValue(43.54);
		gift.setRedemptionDate(new Date());
		rwDao.makePersistent(gift);
		
		Collection<Gift> gifts = roDao.listByUserId(6363L);
		assertEquals(1, gifts.size());
		Gift g = (Gift) gifts.toArray()[0];
		assertEquals(144, g.getOfferId());
	}
}
