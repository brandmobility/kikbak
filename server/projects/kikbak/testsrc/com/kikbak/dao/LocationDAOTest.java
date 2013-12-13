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
		location.setStatus("participating");
		
		rwDao.makePersistent(location);
		
		Location l2 = roDao.findById(location.getId());
		
		assertEquals("SJ", l2.getCity());
		
	}
	
	
	@Test
	public void testLocationArea() {
        final double latitude = 20;
        final double longitude = 20;
        final float geofence = 50;
	    
	    Location location = new Location();
        location.setAddress1("1234 ave");
        location.setCity("SJ");
        location.setMerchantId(3);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setGeofence(geofence);
        location.setState("CA");
        location.setVerificationCode("3241");
        location.setZipcode(12343);
        location.setStatus("participating");
        
        rwDao.makePersistent(location);

        
        assertTrue(roDao.hasLocationInArea(3L, latitude, longitude));
        assertTrue(roDao.hasLocationInArea(3L, latitude + 0.1, longitude + 0.1));
        assertFalse(roDao.hasLocationInArea(3L, latitude + 1, longitude + 1));        
	}
	
}
