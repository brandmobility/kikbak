package com.kikbak.dao;


import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dto.User;

public class ReadOnlyUserDAOTest extends KikbakBaseTest{
	
	private final Long userId = 1L;
	
	@Autowired
	ReadOnlyUserDAO dao;
	
	@Test
	public void testFindById(){
		User user = dao.findById(1L);
		
		assertEquals(userId, user.getId());
	}

	@Test
	public void testFindByFacebookId(){
		User user = dao.findByFacebookId(1234L);
		
		assertEquals(userId, user.getId());
	}
	
}
