package com.kikbak.dao;

import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dto.Location;

public class LocationDAOTest extends AccountDAOTest {
	
	@Autowired
	ReadOnlyLocationDAO roDao;
	
	@Autowired
	ReadWriteLocationDAO rwDao;
	
	@Test
	public void testReadLocation(){
		
		Location location = roDao.findById(1L);
		
		assertEquals("4343", location.getVerificationCode());
	}

	@Test
	public void testListLocationsByMerchantId(){
		Collection<Location> locations = roDao.listByMerchant(1L);
		
		assertEquals(1, locations.size());
	}
	
	@Test
	public void testWriteLocation(){
		Location location = new Location();
		location.setAddress1("1234 ave");
		location.setCity("SJ");
		location.setLatitude(123.23);
		location.setLongitude(321.32);
		location.setState("CA");
		location.setVerificationCode("3241");
		location.setZipcode(12343);
		
		rwDao.makePersistent(location);
		
		Location l2 = roDao.findById(location.getId());
		
		assertEquals("SJ", l2.getCity());
		
	}
}
