package com.kikbak.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.jaxb.admin.LocationType;
import com.kikbak.jaxb.admin.MerchantType;
import com.kikbak.jaxb.admin.OfferType;

public class MerchantServiceTest extends KikbakBaseTest{
	
	@Autowired
	MerchantService service;
	
	
	@Test
	public void getMerchants(){
		Collection<MerchantType> merchants = service.getMerchants();
		
		assertEquals(1, merchants.size());
	}

	@Test
	public void testAddMerchant(){
		MerchantType mt = new MerchantType();
		mt.setDescription("test");
		mt.setImageUrl("url");
		mt.setName("test");
		mt.setUrl("url");
		LocationType lt = fillLocationType();
		mt.getLocations().add(lt);
		
		try {
			service.addOrUpdateMerchant(mt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testAddLocation(){
		LocationType lt = fillLocationType();
		lt.setMerchantId(1L);
		try {
			service.addOrUpdateLocation(lt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Test
	public void testAddOffer(){
		OfferType ot = new OfferType();
		ot.setBeginDate(new Date().getTime());
		ot.setEndDate(new Date().getTime());
		ot.setGiftName("gift name");
		ot.setGiftValue(234.32);
		ot.setGiftNotificationText("notification");
		ot.setKikbakName("name");
		ot.setKikbakValue(12.12);
		ot.setKikbakNotificationText("notification");
		ot.setMerchantId(1L);
		ot.setName("test offer");
		
		service.addOrUpdateOffer(ot);
		assertTrue(ot.getId() >= 1);
	}
	
	protected LocationType fillLocationType(){
		LocationType lt = new LocationType();
		lt.setAddress1("add");
		lt.setAddress2("ass");
		lt.setCity("test");
		lt.setState("test");
		lt.setVerificationCode("test");
		lt.setZipCode(12345);
		lt.setLatitude(12.12);
		lt.setLongitude(12.31);

		return lt;
	}
}
