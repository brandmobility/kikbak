package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.jaxb.RegisterUserRequest;
import com.kikbak.jaxb.RegisterUserResponse;

@Controller
@RequestMapping("/register")
public class RegisterUserController {

	@RequestMapping(value = "/fb/{fbUserId}",  method = RequestMethod.POST)
	public RegisterUserResponse registerFacebookUser(@PathVariable String fbUserId,
			@RequestBody RegisterUserRequest request, final HttpServletResponse response){
		
		return new RegisterUserResponse();
	}
}
