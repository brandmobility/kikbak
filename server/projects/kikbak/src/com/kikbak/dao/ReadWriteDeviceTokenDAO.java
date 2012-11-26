package com.kikbak.dao;

import com.kikbak.dto.Devicetoken;

public interface ReadWriteDeviceTokenDAO {

	public void makePersistent(Devicetoken token);
}
