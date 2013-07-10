package com.kikbak.dao;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Shared;

public class SharedDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlySharedDAO roDao;
	
	@Autowired
	ReadWriteSharedDAO rwDao;
	
	@Test
	public void testFindById(){
		Shared shared = roDao.findById(1L);
		assertEquals(13, shared.getLocationId());
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
	public void testWriteShared(){
		Shared shared = new Shared();
		shared.setMerchantId(12);
		shared.setOfferId(13);
		shared.setLocationId(1234);
		shared.setUserId(12342);
		shared.setSharedDate(new Date());
		shared.setType("email");
		
		rwDao.makePersistent(shared);
		Shared s = roDao.findById(shared.getId());
		assertEquals(1234, s.getLocationId());
	}
	
	@Test
	public void testListAvailableFOrGifting(){
		
		Collection<Shared> s = roDao.listAvailableForGifting(1L);
		assertEquals(1, s.size());
	}
	
	@Test
	public void testListUserAndOfferId(){
		Collection<Shared> shared = roDao.listByUserIdAndOfferId(6L, 3L);
		assertEquals(1, shared.size());
	}
}

