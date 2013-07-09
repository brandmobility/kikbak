package com.kikbak.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;
import com.kikbak.jaxb.offer.UserLocationType;
import com.kikbak.location.Coordinate;
import com.kikbak.location.GeoBoundaries;
import com.kikbak.location.GeoFence;

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
		Date now = new Date();
		offer.setBeginDate(now);
		offer.setEndDate(now);
		offer.setMerchantId(1);
		offer.setGiftName("gift");
		offer.setGiftNotificationText("notification");
		offer.setKikbakName("kname");
		offer.setKikbakNotificationText("notification");
		offer.setName("test");
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
		offer.setGiftName("gn");
		offer.setGiftValue(12.21);
		offer.setGiftNotificationText("notification");
		offer.setKikbakName("kn");
		offer.setKikbakValue(32.32);
		offer.setKikbakNotificationText("notification");
		offer.setName("name");
		rwDao.makePersistent(offer);
		
		Collection<Offer> offers = roDao.listValidOffers();
		assertTrue(offers.size() == 2);
		
	}
	
	@Test
	public void testListValidOffersWithGeoFence(){
		UserLocationType ult = new UserLocationType();
		ult.setLatitude(37.4207480);
		ult.setLongitude(-122.1303430);
		Coordinate origin = new Coordinate(ult.getLatitude(), ult.getLongitude());
		GeoFence fence = GeoBoundaries.getGeoFence(origin, 10d);
	
		Collection<Offer> offers = roDao.listValidOffersInGeoFence(fence);
		assertTrue(offers.size() == 1);
	}
}
