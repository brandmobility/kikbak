package com.kikbak.dao;

import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dto.Location;

public class ReadOnlyLocationDAOTest extends ReadOnlyAccountDAOTest {
	
	@Autowired
	ReadOnlyLocationDAO dao;
	
	@Test
	public void testReadLocation(){
		
		Location location = dao.findById(1L);
		
		assertEquals("4343", location.getVerificationCode());
	}

	@Test
	public void testListLocationsByMerchantId(){
		Collection<Location> locations = dao.listByMerchant(1L);
		
		assertEquals(1, locations.size());
		
	}
}
