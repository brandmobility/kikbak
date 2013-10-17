package com.kikbak.push.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikbak.client.service.impl.types.PlatformType;
import com.kikbak.client.util.CryptoUtils;
import com.kikbak.config.ContextUtil;
import com.kikbak.dao.ReadOnlyDeviceTokenDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyOfferDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dto.Devicetoken;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.User;
import com.kikbak.jaxb.v1.applepushnotification.AppleNotificationPayload;
import com.kikbak.jaxb.v1.applepushnotification.ApsType;
import com.kikbak.mail.EmailSender;
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

    @Autowired
    ReadOnlyUserDAO roUserDAO;

    @Autowired
    ReadOnlyMerchantDAO roMerchantDAO;

    @Autowired
    ReadOnlyOfferDAO roOfferDAO;

    @Override
    public void sendKikbakNotification(Long fromUserId, Long toUserId, Kikbak kikbak, Long creditId) {
        if (creditId == null) {
            return;
        }

        sendKikbakNotificationEmail(fromUserId, toUserId, kikbak, creditId);

        Devicetoken deviceToken = roDeviceToken.findByUserId(toUserId);
        if (deviceToken == null) {
            return;
        }

        if (deviceToken.getPlatformType() == PlatformType.iOS.ordinal()) {
            sendKikbakNotificationApple(fromUserId, deviceToken, kikbak);
            return;
        } else if (deviceToken.getPlatformType() == PlatformType.Android.ordinal()) {
            sendKikbakNotificationGoogle(fromUserId, deviceToken, kikbak);
            return;
        }
    }

    private void sendKikbakNotificationApple(Long fromUserId, Devicetoken deviceToken, Kikbak kikbak) {
        User user = roUserDAO.findById(fromUserId);
        String who = user.getFirstName() + " " + user.getLastName();

        long oid = kikbak.getOfferId();
        long mid = roOfferDAO.findById(oid).getMerchantId();
        Merchant merchant = roMerchantDAO.findById(mid);

        String tmpl = config.getString("notification.ios.kikbak");
        tmpl = tmpl.replace("%USER%", who);
        tmpl = tmpl.replace("%RETAILER%", merchant.getName());
        tmpl = tmpl.replace("%REWARD%", kikbak.getDescription());

        sendNotificationApple(deviceToken.getToken(), tmpl);
    }

    private void sendNotificationApple(String encodedToken, String notification) {
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
            ApsToken token = new ApsToken(encodedToken);
            NotificationPayload payload = new NotificationPayload(apsNotification, token);
            connection.sendPush(payload);
        } catch (Exception e) {
            log.error("Failed to send gift notification via apple:" + e, e);
        }
    }

    private void sendKikbakNotificationGoogle(Long fromUserId, Devicetoken deviceToken, Kikbak kikbak) {
        try {

            User user = roUserDAO.findById(fromUserId);
            String who = user.getFirstName() + " " + user.getLastName();

            long oid = kikbak.getOfferId();
            long mid = roOfferDAO.findById(oid).getMerchantId();
            Merchant merchant = roMerchantDAO.findById(mid);

            String tmpl = config.getString("notification.android.kikbak");
            tmpl = tmpl.replace("%USER%", who);
            tmpl = tmpl.replace("%RETAILER%", merchant.getName());
            tmpl = tmpl.replace("%REWARD%", kikbak.getDescription());

            String key = config.getString("gcm.key");
            Sender sender = new Sender(key);
            Message msg = new Message.Builder() //
                    .delayWhileIdle(true) //
                    .addData("type", "kikbak") //
                    .addData("for", Long.toString(deviceToken.getUserId())) //
                    .addData("title", "You’ve earned a Kikbak reward!") //
                    .addData("msg", tmpl) //
                    .addData("offerId", Long.toString(kikbak.getOfferId())).build();
            sender.sendNoRetry(msg, deviceToken.getToken());
        } catch (Exception e) {
            log.error("Failed to send kikbak notification via google:" + e, e);
        }
    }

    private void sendKikbakNotificationEmail(Long fromUserId, Long toUserId, Kikbak kikbak, long creditId) {
        try {
            long oid = kikbak.getOfferId();
            long mid = roOfferDAO.findById(oid).getMerchantId();
            Merchant merchant = roMerchantDAO.findById(mid);

            String subjectTmpl = config.getString("notification.email.kikbak.subject");
            subjectTmpl = subjectTmpl.replace("%RETAILER%", merchant.getName());
            subjectTmpl = subjectTmpl.replace("%REWARD%", kikbak.getDescription());

            String bodyTmpl = config.getString("notification.email.kikbak.body");
            bodyTmpl = bodyTmpl.replace("%RETAILER%", merchant.getName());
            bodyTmpl = bodyTmpl.replace("%CODE%", CryptoUtils.symetricEncrypt(creditId));

            String email = getDecoratedEmailForUser(toUserId);
            EmailSender.send(email, subjectTmpl, bodyTmpl);
        } catch (Exception e) {
            log.error("Failed to send out notification email", e);
        }
    }

    private String getDecoratedEmailForUser(Long userId) {
        User user = roUserDAO.findById(userId);
        return "\"" + user.getFirstName() + " " + user.getLastName() + "\"<" + user.getEmail() + ">";
    }

    @Override
    public void sendGiftNotification(Long fromUserId, Gift gift) {
        Collection<Long> usersToNotify = roUserDAO.listEligibleForOfferFromUser(gift.getOfferId(), fromUserId);
        if (usersToNotify.size() == 0) {
            return;
        }

        Collection<Devicetoken> tokens = roDeviceToken.listDeviceTokens(usersToNotify);
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
        sendGiftNotificationApple(ios, fromUserId, gift);
        sendGiftNotificationGoogle(android, fromUserId, gift);
    }

    private void sendGiftNotificationApple(List<String> tokens, Long fromUserId, Gift gift) {
        if (tokens.isEmpty())
            return;

        User user = roUserDAO.findById(fromUserId);
        String who = user.getFirstName() + " " + user.getLastName();

        long oid = gift.getOfferId();
        long mid = roOfferDAO.findById(oid).getMerchantId();
        Merchant merchant = roMerchantDAO.findById(mid);

        String tmpl = config.getString("notification.ios.gift");
        tmpl = tmpl.replace("%USER%", who);
        tmpl = tmpl.replace("%RETAILER%", merchant.getName());
        tmpl = tmpl.replace("%GIFT%", gift.getDescription());

        for (String token : tokens) {
            sendNotificationApple(token, tmpl);
        }
    }

    private void sendGiftNotificationGoogle(List<String> tokens, Long fromUserId, Gift gift) {
        if (tokens.isEmpty())
            return;

        User user = roUserDAO.findById(fromUserId);
        String who = user.getFirstName() + " " + user.getLastName();

        long oid = gift.getOfferId();
        long mid = roOfferDAO.findById(oid).getMerchantId();
        Merchant merchant = roMerchantDAO.findById(mid);

        String tmpl = config.getString("notification.android.gift");
        tmpl = tmpl.replace("%RETAILER%", merchant.getName());
        tmpl = tmpl.replace("%GIFT%", gift.getDescription());

        try {
            String key = config.getString("gcm.key");
            Sender sender = new Sender(key);
            Message msg = new Message.Builder() //
                    .delayWhileIdle(true) //
                    .addData("type", "gift") //
                    .addData("title", who) //
                    .addData("msg", tmpl) //
                    .addData("offerId", Long.toString(gift.getOfferId())) //
                    .build();
            sender.sendNoRetry(msg, tokens);
        } catch (Exception e) {
            log.error("Failed to send gift notification via google:" + e, e);
        }
    }
}
