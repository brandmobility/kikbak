package com.kikbak.client.dao;

import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.client.dao.ReadOnlyUserDAO;
import com.kikbak.client.dao.ReadWriteUserDAO;
import com.kikbak.dto.User;

public class ReadWriteUserDAOTest extends KikbakBaseTest{
	
	@Autowired
	ReadOnlyUserDAO readOnlyUserDAO;
	
	@Autowired
	ReadWriteUserDAO readWriteUserDAO;
	
	
	@Test
	public void testWriteUser(){
		User user = new User();
		user.setEmail("test@test.com");
		user.setFirstName("t");
		byte gender = 0;
		user.setGender(gender);
		user.setFacebookId(123424L);
		user.setLastName("t");
		user.setCreateDate(new Date());
		user.setUsername("tt");
		
		readWriteUserDAO.makePersistent(user);
		
		User u2 = readOnlyUserDAO.findByFacebookId(123424L);
		
		assertEquals(u2.getEmail(), user.getEmail());
	}

}
