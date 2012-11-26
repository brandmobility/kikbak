package com.kikbak.dao;

import com.kikbak.dto.Devicetoken;

public interface ReadOnlyDeviceTokenDAO {
	
	Devicetoken findByUserId(Long userId);

}
