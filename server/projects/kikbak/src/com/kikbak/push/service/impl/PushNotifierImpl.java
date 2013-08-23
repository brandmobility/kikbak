package com.kikbak.push.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikbak.client.service.impl.types.PlatformType;
import com.kikbak.config.ContextUtil;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;
import com.kikbak.jaxb.applepushnotification.AppleNotificationPayload;
import com.kikbak.jaxb.applepushnotification.ApsType;
import com.kikbak.push.apple.ApsConnection;
import com.kikbak.push.apple.ApsToken;
import com.kikbak.push.apple.NotificationPayload;
import com.kikbak.push.google.Message;
import com.kikbak.push.google.Sender;
import com.kikbak.push.service.PushNotifier;

@Service
public class PushNotifierImpl implements PushNotifier {

    private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration",
            PropertiesConfiguration.class);

    private static Logger log = Logger.getLogger(PushNotifierImpl.class);

    private ApsConnection connection = null;

    @Autowired
    ReadOnlyDeviceTokenDAO roDeviceToken;

    @Override
    public void sendKikbakNotification(Long toUserId, Kikbak kikbak) {
        Devicetoken deviceToken = roDeviceToken.findByUserId(toUserId);
        if (deviceToken == null)
            return;

        if (deviceToken.getPlatformType() == PlatformType.iOS.ordinal()) {
            sendKikbakNotificationApple(deviceToken, kikbak.getNotificationText());
            return;
        } else if (deviceToken.getPlatformType() == PlatformType.Android.ordinal()) {
            sendKikbakNotificationGoogle(deviceToken, kikbak);
            return;
        }
    }

    private void sendKikbakNotificationApple(Devicetoken deviceToken, String notification) {
        try {
            if (config.getInt("aps.enabled", 0) == 0) {
                return;
            }
            if (connection == null || connection.isClosed()) {
                connection = new ApsConnection();
                connection.connect();
            }

            AppleNotificationPayload apsNotification = new AppleNotificationPayload();
            ApsType values = new ApsType();
            values.setAlert(notification);
            apsNotification.setAps(values);
            ApsToken token = new ApsToken(deviceToken.getToken());
            NotificationPayload payload = new NotificationPayload(apsNotification, token);
            connection.sendPush(payload);
        } catch (Exception e) {
            log.error("Failed to send gift notification via apple:" + e, e);
        }
    }

    private void sendKikbakNotificationGoogle(Devicetoken deviceToken, Kikbak kikbak) {
        try {
            String key = config.getString("gcm.key");
            Sender sender = new Sender(key);
            Message msg = new Message.Builder() //
                    .delayWhileIdle(true) //
                    .addData("type", "kikbak") //
                    .addData("for", Long.toString(deviceToken.getUserId())) //
                    .addData("msg", kikbak.getNotificationText()) //
                    .addData("offerId", Long.toString(kikbak.getOfferId())).build();
            sender.sendNoRetry(msg, deviceToken.getToken());
        } catch (Exception e) {
            log.error("Failed to send kikbak notification via google:" + e, e);
        }
    }

    @Override
    public void sendGiftNotification(Long fromUserId, Gift gift) {
        Collection<Devicetoken> tokens = roDeviceToken.listFriendsDeviceTokens(fromUserId);
        ArrayList<String> ios = new ArrayList<String>(tokens.size());
        ArrayList<String> android = new ArrayList<String>(tokens.size());

        for (Devicetoken token : tokens) {
            if (token.getPlatformType() == PlatformType.iOS.ordinal()) {
                ios.add(token.getToken());
                continue;
            }
            if (token.getPlatformType() == PlatformType.Android.ordinal()) {
                android.add(token.getToken());
                continue;
            }
        }
        sendGiftNotificationApple(android, gift);
        sendGiftNotificationGoogle(android, gift);
    }

    private void sendGiftNotificationApple(List<String> tokens, Gift gift) {
        // TODO: implement me
    }

    private void sendGiftNotificationGoogle(List<String> tokens, Gift gift) {
        if (tokens.isEmpty())
            return;

        try {
            String key = config.getString("gcm.key");
            Sender sender = new Sender(key);
            Message msg = new Message.Builder() //
                    .delayWhileIdle(true) //
                    .addData("type", "gift") //
                    .addData("msg", gift.getNotificationText()) //
                    .addData("offerId", Long.toString(gift.getOfferId())) //
                    .build();
            sender.sendNoRetry(msg, tokens);
        } catch (Exception e) {
            log.error("Failed to send gift notification via google:" + e, e);
        }
    }

}
