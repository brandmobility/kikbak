package com.kikbak.client.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakBaseTest;
import com.kikbak.jaxb.register.UserType;

public class FbLoginServiceTest extends KikbakBaseTest{
	
	@Autowired
	FbLoginService service;
	
	@Test
	public void testFbLogin() throws Exception {
		String token = "CAAHAusHEzpUBAJ1jho2EhLPaYIUESZBaReOBjtWckE9J9naQmQZAwjDfrhdvwWBsB8qZAyBwWBX7Sci1cyWSPBqN0IgbZA402XYRtj2PUqslzQM8eYVLPVpvRGjS3LC1HbZAORsdL3L7zugExNtNC2KZCSZCRpV0WUZD";
		UserType user = service.getUserInfo(token);
		assertNotNull(user);
	}
}
