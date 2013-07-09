package com.kikbak.admin.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.jaxb.admin.TokenType;

public class AuthenticationServiceTest extends KikbakBaseTest{

	@Autowired
	AuthenticationService authService;
	
	@Test
	public void testAuth(){
		
		try {
			TokenType token = authService.authorize("test@kikbak.com", "1234");
			authService.isValidToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}
}
