package com.kikbak.dao;

import java.util.Collection;

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
		Collection<Shared> shareds = roDao.listByUserId(3L);
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
		
		rwDao.makePersistent(shared);
		Shared s = roDao.findById(shared.getId());
		assertEquals(1234, s.getLocationId());
	}
}

