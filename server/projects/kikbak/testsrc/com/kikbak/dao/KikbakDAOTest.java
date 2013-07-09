package com.kikbak.dao;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Credit;

public class KikbakDAOTest extends KikbakBaseTest {

	@Autowired
	ReadOnlyCreditDAO roDao;
	
	@Autowired
	ReadWriteCreditDAO rwDao;
	
	@Test
	public void testFindById(){
		Credit credit = roDao.findById(1L);
		assertEquals(3, credit.getMerchantId());
	}
	
	@Test
	public void testListByUserId(){
		Collection<Credit> credits = roDao.listCreditsWithBalance(2L);
		assertEquals(1, credits.size());
	}
	
	@Test
	public void testListByMerchantId(){
		Collection<Credit> credits = roDao.listByMerchantId(3L);
		assertEquals(2, credits.size());
	}
	
	@Test
	public void testListByOfferId(){
		Collection<Credit> credits = roDao.listByOfferId(4L);
		assertEquals(2, credits.size());
	}
	
	@Test
	public void testWriteKikbak(){
		Credit credit = new Credit();
		credit.setBeginDate(new Date());
		credit.setEndDate(new Date());
		credit.setLocationId(1234);
		credit.setMerchantId(1234);
		credit.setOfferId(1234);
		
		rwDao.makePersistent(credit);
		
		Collection<Credit> credits = roDao.listByMerchantId(1234L);
		assertEquals(1, credits.size());
		Credit k2 = (Credit) credits.toArray()[0];
		assertEquals(1234, k2.getLocationId());
	}
}
