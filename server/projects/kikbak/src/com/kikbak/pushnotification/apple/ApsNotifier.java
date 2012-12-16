package com.kikbak.pushnotification.apple;

import java.io.IOException;

import com.kikbak.dto.Devicetoken;
import com.kikbak.jaxb.AppleNotificationPayload;
import com.kikbak.jaxb.ApsType;

public class ApsNotifier {

	private ApsConnection connection = null;
	
	
	public void sendNotification(Devicetoken deviceToken) throws IOException, Exception{
		if( connection == null || connection.isClosed() ){
			connection = new ApsConnection();
			connection.connect();
		}
		
		AppleNotificationPayload apsNotification = new AppleNotificationPayload();
		ApsType values = new ApsType();
		values.setAlert("Test Alert");
		apsNotification.setAps(values);
		ApsToken token = new ApsToken(deviceToken.getToken());
		NotificationPayload payload = new NotificationPayload(apsNotification, token);
		connection.sendPush(payload);
	}
}
