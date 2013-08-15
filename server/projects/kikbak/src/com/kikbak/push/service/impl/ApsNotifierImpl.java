package com.kikbak.push.service.impl;

import java.io.IOException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Service;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.kikbak.client.service.impl.types.PlatformType;
import com.kikbak.config.ContextUtil;
import com.kikbak.dto.Devicetoken;
import com.kikbak.jaxb.applepushnotification.AppleNotificationPayload;
import com.kikbak.jaxb.applepushnotification.ApsType;
import com.kikbak.push.service.ApsNotifier;

@Service
public class ApsNotifierImpl implements ApsNotifier {

	private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration", PropertiesConfiguration.class);
	
	private ApsConnection connection = null;
	
	
	public void sendNotification(Devicetoken deviceToken, String alertText) throws IOException, Exception{
		
        if (deviceToken.getPlatformType() == PlatformType.Android.ordinal()) {
            sendViaGoogleCloudMessaging(deviceToken, alertText);
            return;
        }
		
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
	
    private void sendViaGoogleCloudMessaging(Devicetoken deviceToken, String alertText) throws IOException {
        String key = config.getString("gcm.key");
        Sender sender = new Sender(key);
        Message msg = new Message.Builder()
            .delayWhileIdle(true)
            .addData("for", Long.toString(deviceToken.getUserId()))
            .addData("type", "redeem")
            .addData("msg", alertText)
            .build();
        sender.sendNoRetry(msg, deviceToken.getToken());
    }
}
