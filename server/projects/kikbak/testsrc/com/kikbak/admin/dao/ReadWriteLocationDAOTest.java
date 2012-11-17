package com.kikbak.admin.dao;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Location;

public class ReadWriteLocationDAOTest extends KikbakBaseTest{

	@Autowired
	ReadWriteLocationDAO rwDao;
	
	@Autowired
	ReadOnlyLocationDAO roDao;
	
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
