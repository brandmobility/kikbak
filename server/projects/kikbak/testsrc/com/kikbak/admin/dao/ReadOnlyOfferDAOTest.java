package com.kikbak.admin.dao;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Offer;

public class ReadOnlyOfferDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyOfferDAO dao;
	
	@Test
	public void testReadOffer(){
		Offer offer = dao.findById(1L);
		assertEquals("kikit", offer.getName());
	}
	
	@Test
	public void testListOffersByMerchant(){
		Merchant merchant = new Merchant();
		merchant.setId(1L);
		
		Collection<Offer> offers = dao.listOffers(merchant);
		assertEquals(1, offers.size());
	}
}
