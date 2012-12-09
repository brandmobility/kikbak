package com.kikbak.dao;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dto.User;

public class UserDAOTest extends KikbakBaseTest{
	
	private final long userId = 1;
	
	@Autowired
	ReadOnlyUserDAO roDao;
	
	@Autowired
	ReadWriteUserDAO rwDAO;
	
	@Test
	public void testFindById(){
		User user = roDao.findById(1L);
		
		assertEquals(userId, user.getId());
	}

	@Test
	public void testFindByFacebookId(){
		User user = roDao.findByFacebookId(1234L);
		
		assertEquals(userId, user.getId());
	}
	
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
		
		rwDAO.makePersistent(user);
		
		User u2 = roDao.findByFacebookId(123424L);
		
		assertEquals(u2.getEmail(), user.getEmail());
	}
}
