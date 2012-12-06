package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;

public class OfferDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyOfferDAO roDao;
	
	@Autowired
	ReadWriteOfferDAO rwDao;
	
	@Test
	public void testReadOffer(){
		Offer offer = roDao.findById(1L);
		assertEquals("kikit", offer.getName());
	}
	
	@Test
	public void testListOffersByMerchant(){
		Merchant merchant = new Merchant();
		merchant.setId(1L);
		
		Collection<Offer> offers = roDao.listOffers(merchant);
		assertEquals(1, offers.size());
	}
	
	@Test
	public void testRWOffer(){
		Offer offer = new Offer();
		offer.setDescription("testing");
		Date now = new Date();
		offer.setBeginDate(now);
		offer.setEndDate(now);
		offer.setMerchantId(1);
		offer.setGiftDescription("desc");
		offer.setGiftName("gift");
		offer.setKikbakDescription("kik");
		offer.setKikbakName("kname");
		offer.setName("test");
		offer.setDefaultText("3243242");
		offer.setGiftValue(3.25);
		offer.setKikbakValue(2.25);
		
		rwDao.makePersistent(offer);
		
		Offer o2 = roDao.findById(offer.getId());
		
		assertEquals("test", o2.getName());
	}
	
	@Test 
	public void testListValidOffers(){
		Offer offer = new Offer();
		Date now = new Date();
		offer.setBeginDate(new Date(now.getTime() - 999999999));
		offer.setEndDate(new Date(now.getTime() + 999999999));
		offer.setDefaultText("default");
		offer.setDescription("desc");
		offer.setGiftDescription("gift");
		offer.setGiftName("gn");
		offer.setGiftValue(12.21);
		offer.setKikbakDescription("kd");
		offer.setKikbakName("kn");
		offer.setKikbakValue(32.32);
		offer.setName("name");
		rwDao.makePersistent(offer);
		
		Collection<Offer> offers = roDao.listValidOffers();
		assertTrue(offers.size() == 1);
		
	}
}
