package com.kikbak.rest.admin;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.jaxb.AuthenticationRequest;
import com.kikbak.jaxb.AuthenticationResponse;

@Controller
public class AuthenticationController {

	@RequestMapping(value = "/Authenticate", method = RequestMethod.POST)
	public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request, final HttpServletResponse response){
		
		return new AuthenticationResponse();
	}
	
}
