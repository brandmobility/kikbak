package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Kikbak;

public class KikbakDAOTest extends KikbakBaseTest {

	@Autowired
	ReadOnlyKikbakDAO roDao;
	
	@Autowired
	ReadWriteKikbakDAO rwDao;
	
	@Test
	public void testFindById(){
		Kikbak kikbak = roDao.findById(1L);
		assertEquals(3, kikbak.getMerchantId());
	}
	
	@Test
	public void testListByUserId(){
		Collection<Kikbak> kikbaks = roDao.listKikbaksWithValue(2L);
		assertEquals(1, kikbaks.size());
	}
	
	@Test
	public void testListByMerchantId(){
		Collection<Kikbak> kikbaks = roDao.listByMerchantId(3L);
		assertEquals(2, kikbaks.size());
	}
	
	@Test
	public void testListByOfferId(){
		Collection<Kikbak> kikbaks = roDao.listByOfferId(4L);
		assertEquals(2, kikbaks.size());
	}
	
	@Test
	public void testWriteKikbak(){
		Kikbak kikbak = new Kikbak();
		kikbak.setBeginDate(new Date());
		kikbak.setEndDate(new Date());
		kikbak.setLocationId(1234);
		kikbak.setMerchantId(1234);
		kikbak.setOfferId(1234);
		
		rwDao.makePersistent(kikbak);
		
		Collection<Kikbak> kikbaks = roDao.listByMerchantId(1234L);
		assertEquals(1, kikbaks.size());
		Kikbak k2 = (Kikbak) kikbaks.toArray()[0];
		assertEquals(1234, k2.getLocationId());
	}
}
