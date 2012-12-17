package com.kikbak.push.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.kikbak.dto.Devicetoken;
import com.kikbak.jaxb.AppleNotificationPayload;
import com.kikbak.jaxb.ApsType;

@Service
public class ApsNotifierImpl {

	private ApsConnection connection = null;
	
	
	public void sendNotification(Devicetoken deviceToken, String alertText) throws IOException, Exception{
		if( connection == null || connection.isClosed() ){
			connection = new ApsConnection();
			connection.connect();
		}
		
		AppleNotificationPayload apsNotification = new AppleNotificationPayload();
		ApsType values = new ApsType();
		values.setAlert(alertText);
		apsNotification.setAps(values);
		ApsToken token = new ApsToken(deviceToken.getToken());
		NotificationPayload payload = new NotificationPayload(apsNotification, token);
		connection.sendPush(payload);
	}
}
