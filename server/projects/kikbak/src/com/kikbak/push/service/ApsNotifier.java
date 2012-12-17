package com.kikbak.push.service;

import java.io.IOException;

import com.kikbak.dto.Devicetoken;

public interface ApsNotifier {

	public void sendNotification(Devicetoken deviceToken, String alertText) throws IOException, Exception;
}
