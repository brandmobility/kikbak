package com.kikbak.admin.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import com.kikbak.KikbakBaseTest;
import com.kikbak.jaxb.TokenType;

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
