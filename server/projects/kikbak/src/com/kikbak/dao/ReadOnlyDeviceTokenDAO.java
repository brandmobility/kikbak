package com.kikbak.dao;

import java.util.Collection;

import com.kikbak.dto.Devicetoken;

public interface ReadOnlyDeviceTokenDAO {

    Devicetoken findByUserId(Long userId);

    Collection<Devicetoken> listFriendsDeviceTokens(Long userId);

}
