package com.kikbak.admin.service;

import com.kikbak.jaxb.TokenType;

public interface AuthenticationService {

	public TokenType authorize(final String email, final String password) throws Exception;
	
	public void isValidToken(final TokenType token) throws Exception;
}
