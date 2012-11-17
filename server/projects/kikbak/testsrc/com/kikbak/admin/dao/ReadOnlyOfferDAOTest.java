package com.kikbak.admin.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Offer;

public class ReadOnlyOfferDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyOfferDAO dao;
	
	@Test
	public void testReadOffer(){
		Offer offer = dao.findById(1L);
		assertEquals("kikit", offer.getName());
	}
}
