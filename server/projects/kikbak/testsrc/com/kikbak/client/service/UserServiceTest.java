package com.kikbak.client.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.jaxb.UserType;

public class UserServiceTest extends KikbakBaseTest{

	@Autowired
	UserService service;
	
	@Test
	public void testRegisterUser(){
		UserType ut = generateUserType();		
		service.registerUser(ut);
	}
	
	@Test
	public void testUpdateFriends(){
		
	}
	
	@Test
	public void testPersistDeviceToken(){
		
	}
	
	@Test
	public void testGetOffers(){
		
	}
	
	protected UserType generateUserType(){
		UserType ut = new UserType();
		ut.setEmail("foo@foo.com");
		ut.setFirstName("first");
		ut.setGender("male");
		ut.setLastName("last");
		ut.setName("first last");
		ut.setUsername("username");
		ut.setVerified(1);
		
		return ut;
	}
}
