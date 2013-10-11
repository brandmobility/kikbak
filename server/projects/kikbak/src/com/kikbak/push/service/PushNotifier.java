package com.kikbak.push.service;

import com.kikbak.dto.Gift;
import com.kikbak.dto.Kikbak;

public interface PushNotifier {

    public void sendKikbakNotification(Long fromUserId, Long toUserId, Kikbak kikbak, Long creditId);

    public void sendGiftNotification(Long fromUserId, Gift gift);
}
