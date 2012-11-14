package com.kikbak.client.services.dao;


import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseUTest;
import com.kikbak.dto.User;

public class ReadOnlyUserDAOUTest extends KikbakBaseUTest{
	
	@Autowired
	ReadOnlyUserDAO dao;
	
	@Test
	public void testFindById(){
		User user = dao.findById(1L);
		
		assertEquals(new Long(1), user.getId());
	}

	@Test
	public void testFindByFacebookId(){
		User user = dao.findByFacebookId(1234L);
		
		System.out.println(user.getFirstName());
	}
	
}
