package com.kikbak.dao;

import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.dto.Devicetoken;

public class DeviceTokenDAOTest extends KikbakBaseTest{

	@Autowired
	ReadOnlyDeviceTokenDAO roDAO;
	
	@Autowired
	ReadWriteDeviceTokenDAO rwDAO;
	
	@Test
	public void testReadDeviceToken(){
		Devicetoken token = roDAO.findByUserId(1L);
		assertEquals("testtoken", token.getToken());
	}
	
	@Test
	public void testWriteDeviceToken(){
		Devicetoken token = new Devicetoken();
		token.setUserId(3);
		token.setPlatformType((short) 0);
		token.setLastUpdateTime(new Date());
		token.setToken("write");
		rwDAO.makePersistent(token);
		
		Devicetoken t = roDAO.findByUserId(3L);
		assertEquals(token.getToken(), t.getToken());
	}
}
