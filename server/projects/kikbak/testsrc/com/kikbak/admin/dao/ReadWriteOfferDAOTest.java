package com.kikbak.admin.dao;

import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Offer;

public class ReadWriteOfferDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyOfferDAO roDao;
	
	@Autowired
	ReadWriteOfferDAO rwDao;
	
	@Test
	public void testRWOffer(){
		Offer offer = new Offer();
		offer.setDescription("testing");
		Date now = new Date();
		offer.setBeginDate(now);
		offer.setEndDate(now);
		offer.setMerchantId(1);
		offer.setName("test");
		offer.setShareeValue(3.25);
		offer.setSharerValue(2.25);
		
		rwDao.makePersistent(offer);
		
		Offer o2 = roDao.findById(offer.getId());
		
		assertEquals("test", o2.getName());
		
	}
}
