package com.kikbak.rest.admin;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.admin.service.AuthenticationService;
import com.kikbak.jaxb.AuthenticationRequest;
import com.kikbak.jaxb.AuthenticationResponse;
import com.kikbak.jaxb.StatusType;
import com.kikbak.jaxb.TokenType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping(value = "/auth")
public class AuthenticationController {

	private static final Logger logger = Logger.getLogger(AuthenticationController.class);
	
	@Autowired
	AuthenticationService authenticationService;
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request, final HttpServletResponse response){
		
		AuthenticationResponse authResponse = new AuthenticationResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		authResponse.setStatus(status);
		try {
			TokenType token = authenticationService.authorize(request.getAccountInfo().getEmail(), request.getAccountInfo().getPassword());
			if( token != null){
				authResponse.setToken(token);
			}
			else{
				status.setCode(StatusCode.AUTH_ERROR.ordinal());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return authResponse;
		
		/*
		AuthenticationRequest request = new AuthenticationRequest();
		AccountType at = new AccountType();
		at.setEmail("admin@kikbak.com");
		at.setPassword("kikit");
		request.setAccountInfo(at);
		return request;
		*/
	}
	
}
