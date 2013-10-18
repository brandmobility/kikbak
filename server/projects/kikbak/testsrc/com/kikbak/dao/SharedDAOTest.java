package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.KikbakBaseTest;
import com.kikbak.client.service.v1.ReferralCodeUniqueException;
import com.kikbak.dto.Shared;

public class SharedDAOTest extends KikbakBaseTest{

	@Autowired
    @Qualifier("ReadOnlySharedDAO")
	ReadOnlySharedDAO roDao;
	
	@Autowired
    @Qualifier("ReadWriteSharedDAO")
	ReadWriteSharedDAO rwDao;
	
	@Test
	public void testFindById(){
		Shared shared = roDao.findById(1L);
		assertEquals(13, shared.getLocationId().longValue());
	}
	
	@Test
	public void testListByUserId(){
		Collection<Shared> shareds = roDao.listByUserId(6L);
		assertEquals(1, shareds.size());
		
	}
	
	@Test
	public void testListByLocationId(){
		Collection<Shared> shareds = roDao.listByLocationId(13L);
		assertEquals(2, shareds.size());
	}
	
	@Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void testWriteShared() throws Exception {
		Shared shared = new Shared();
		shared.setMerchantId(12);
		shared.setOfferId(13);
		shared.setLocationId(1234L);
		shared.setUserId(12342);
		shared.setSharedDate(new Date());
		shared.setType("email");
		shared.setReferralCode("code4");
		
		try {
		    rwDao.saveShared(shared);
		} catch (ReferralCodeUniqueException e) {
	        shared.setReferralCode("code3");
            rwDao.saveShared(shared);
		}
		
		Shared s = roDao.findById(shared.getId());
		assertEquals(1234, s.getLocationId().longValue());
        assertEquals("code4", s.getReferralCode());
	}
	
	@Test
	public void testListUserAndOfferId() throws Exception {
		Collection<Shared> shareds = roDao.listByUserIdAndOfferId(6L, 3L);
		assertEquals(2, shareds.size());
		
		Shared shared = rwDao.findLastShareByUserAndOffer(6L, 3L);
		assertEquals(3, shared.getId());
	
		shared.setSharedDate(new Date(0));
		rwDao.saveShared(shared);
		
		shared = rwDao.findLastShareByUserAndOffer(6L, 3L);
		assertEquals(2, shared.getId());
		
		
	}
}

