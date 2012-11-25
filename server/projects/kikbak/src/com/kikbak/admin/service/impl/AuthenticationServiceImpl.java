package com.kikbak.admin.service.impl;

import java.security.MessageDigest;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.admin.service.AuthenticationException;
import com.kikbak.admin.service.AuthenticationService;
import com.kikbak.admin.util.AuthTokenManager;
import com.kikbak.dao.ReadOnlyAccountDAO;
import com.kikbak.dto.Account;
import com.kikbak.jaxb.TokenType;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	ReadOnlyAccountDAO roAccountDAO;
	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public TokenType authorize(String email, String password) throws Exception {
		Account account = roAccountDAO.findByEmailAndPassword(email, password);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		if( account != null){
			md.update(account.getEmail().getBytes("UTF-8"));
			md.update(account.getPassword().getBytes("UTF-8"));
			md.update((new Date()).toString().getBytes("UTF-8"));
		}
		else{
			throw new AuthenticationException("Invalid username or password.");
		}
		
		byte [] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for(byte b : digest){
			sb.append(Integer.toHexString(b & 0xFF));
		}
		
		//generate token
		TokenType token = new TokenType();
		token.setToken(sb.toString());
		
		//add token to cache
		AuthTokenManager.getInstance().addToken(token);
		
		return token;
	}

	@Override
	public void isValidToken(TokenType token) throws Exception {
		if( !AuthTokenManager.getInstance().validToken(token) ){
			throw new AuthenticationException("Invalid Authentication Token");
		}
	}

}
