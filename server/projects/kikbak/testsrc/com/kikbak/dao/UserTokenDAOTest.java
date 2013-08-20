package com.kikbak.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.UserToken;

public class UserTokenDAOTest extends KikbakBaseTest{
	
	private final long userId = 1;
	
	@Autowired
	ReadOnlyUserTokenDAO roDao;
	
	@Autowired
	ReadWriteUserTokenDAO rwDAO;
	
	@Test
	public void testTokenDAO(){
		UserToken token = roDao.findByUserId(1L);
		assertNull(token);
		token = new UserToken();
		token.setUserId(userId);
		token.setToken("token");

		rwDAO.makePersistent(token);
		token = roDao.findByUserId(1L);
		assertEquals(userId, token.getUserId());
		assertEquals("token", token.getToken());
	}
	
}
