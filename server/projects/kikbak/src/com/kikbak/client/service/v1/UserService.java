package com.kikbak.client.service.v1;

import java.util.Collection;

import com.kikbak.jaxb.v1.devicetoken.DeviceTokenType;
import com.kikbak.jaxb.v1.register.UserIdType;
import com.kikbak.jaxb.v1.register.UserType;

public interface UserService {

    public UserIdType registerUser(UserType userType);
    public void updateFriendsList(final long userId, final Collection<Long> friends);
    public void persistDeviceToken(final Long userId, final DeviceTokenType deviceToken);
    public String getUserToken(long userId);
    public boolean verifyUserToken(long userId, String token);
    public Long registerWebUser(String name, String email, String phone);
}
