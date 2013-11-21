package com.kikbak.client.service.v1;

import com.kikbak.jaxb.v1.devicetoken.DeviceTokenType;

public interface UserService {

    public void updateFriendsList(final long userId, String accessToken) throws FbLoginException;
    public void persistDeviceToken(final Long userId, final DeviceTokenType deviceToken);
    public String getUserToken(long userId);
    public boolean verifyUserToken(long userId, String token);
    public long registerWebUser(String name, String email, String phone);
    public long registerFbUser(String accessToken, String phone) throws FbUserLimitException, FbLoginException;
}
