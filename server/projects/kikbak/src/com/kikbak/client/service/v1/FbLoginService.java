package com.kikbak.client.service.v1;

import java.util.Collection;

import com.kikbak.jaxb.v1.register.UserType;

public interface FbLoginService {

    UserType getUserInfo(String accessToken) throws FbLoginException;

    Collection<Long> getFriends(String accessToken) throws FbLoginException;

}
