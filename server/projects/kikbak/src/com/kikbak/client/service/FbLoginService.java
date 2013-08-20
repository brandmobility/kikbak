package com.kikbak.client.service;

import com.kikbak.jaxb.register.UserType;

public interface FbLoginService {

	UserType getUserInfo(String accessToken) throws FbLoginException;
}
