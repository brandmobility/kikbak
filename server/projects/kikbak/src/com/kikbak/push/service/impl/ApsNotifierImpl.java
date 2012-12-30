package com.kikbak.push.service.impl;

import java.io.IOException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Service;

import com.kikbak.config.ContextUtil;
import com.kikbak.dto.Devicetoken;
import com.kikbak.jaxb.AppleNotificationPayload;
import com.kikbak.jaxb.ApsType;
import com.kikbak.push.service.ApsNotifier;

@Service
public class ApsNotifierImpl implements ApsNotifier {

	private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration", PropertiesConfiguration.class);
	
	private ApsConnection connection = null;
	
	
	public void sendNotification(Devicetoken deviceToken, String alertText) throws IOException, Exception{
		if( config.getInt("aps.enabled", 0) == 0 ){
			return;
		}
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
