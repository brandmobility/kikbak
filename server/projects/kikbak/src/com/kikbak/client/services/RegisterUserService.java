package com.kikbak.client.services;

import com.kikbak.jaxb.RegisterUserRequest;
import com.kikbak.jaxb.RegistrationResponse;

public interface RegisterUserService {
		
	public RegistrationResponse registerUser(RegisterUserRequest request);

}
