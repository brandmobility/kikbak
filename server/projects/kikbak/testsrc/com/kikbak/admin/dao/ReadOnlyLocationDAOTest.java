package com.kikbak.admin.dao;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.dto.Location;

public class ReadOnlyLocationDAOTest extends ReadOnlyAccountDAOTest {
	
	@Autowired
	ReadOnlyLocationDAO dao;
	
	@Test
	public void testReadLocation(){
		
		Location location = dao.findById(1L);
		
		assertEquals("4343", location.getVerificationCode());
	}

}
