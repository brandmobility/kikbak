package com.kikbak.client.service;

import java.util.Collection;

import com.kikbak.jaxb.register.UserType;

public interface FbLoginService {

    UserType getUserInfo(String accessToken) throws FbLoginException;

    Collection<Long> getFriends(String accessToken) throws FbLoginException;

}
