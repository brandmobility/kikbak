package com.kikbak.dao;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Multimap;
import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Allocatedgift;

public class AllocatedGiftDAOTest extends KikbakBaseTest {

	@Autowired
	ReadOnlyAllocatedGiftDAO roDao;
	
	@Autowired
	ReadWriteAllocatedGiftDAO rwDao;

	
	@Test
	public void testListByUserId(){
		Collection<Allocatedgift> gifts = roDao.listValidByUserId(12L);
		assertEquals(1, gifts.size());
	}
	
	@Test
	public void testListByMerchantId(){
		Collection<Allocatedgift> gifts = roDao.listByMerchantId(3L);
		assertEquals(4, gifts.size());
	}
	
	@Test
	public void testListByOfferId(){
		Collection<Allocatedgift> gifts = roDao.listByOfferId(13L);
		assertEquals(1, gifts.size());
	}
	
	@Test
	public void testListByFriendUserId(){
		Collection<Allocatedgift> gifts = roDao.listByFriendUserId(13L);
		assertEquals(2, gifts.size());
	}
	@Test
	public void testWriteGift(){
		Allocatedgift gift = new Allocatedgift();
		gift.setExpirationDate(new Date());
		gift.setFriendUserId(12);
		gift.setMerchantId(13);
		gift.setOfferId(144);
		gift.setUserId(6363);
		gift.setValue(43.54);
		rwDao.makePersistent(gift);
		
		Collection<Allocatedgift> gifts = roDao.listValidByUserId(6363L);
		assertEquals(1, gifts.size());
		Allocatedgift g = (Allocatedgift) gifts.toArray()[0];
		assertEquals(144, g.getOfferId());
	}
}
