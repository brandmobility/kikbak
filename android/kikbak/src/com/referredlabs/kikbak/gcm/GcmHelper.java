
package com.referredlabs.kikbak.gcm;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.DeviceTokenType;
import com.referredlabs.kikbak.data.DeviceTokenUpdateRequest;
import com.referredlabs.kikbak.data.DeviceTokenUpdateResponse;
import com.referredlabs.kikbak.data.UserIdType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.tasks.Task;
import com.referredlabs.kikbak.utils.Register;

public class GcmHelper {

  private static final String TAG = "GCM";
  private static final String REG = "gcm";
  private static final String KEY_REGISTRATION_ID = "registration_id";
  private static final String KEY_APP_VERSION = "appVersion";
  private static final String KEY_EXPIRATION_TIME = "expirationTime";
  private static final long EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7; // 7 days

  private static GcmHelper sInstance;

  public static synchronized GcmHelper getInstance() {
    if (sInstance == null) {
      sInstance = new GcmHelper();
    }
    return sInstance;
  }

  private GcmHelper() {
  }

  public void registerIfNeeded() {
    String regId = getRegistrationId();
    Log.i("Kikbak", "GCM registration id:" + regId);
    if (regId == null) {
      register();
    }
  }

  public String getRegistrationId() {
    SharedPreferences prefs = Kikbak.getInstance().getSharedPreferences(REG, Context.MODE_PRIVATE);
    String registrationId = prefs.getString(KEY_REGISTRATION_ID, null);
    if (registrationId == null) {
      Log.v(TAG, "Not registered.");
      return null;
    }

    int registeredVersion = prefs.getInt(KEY_APP_VERSION, Integer.MIN_VALUE);
    int currentVersion = Kikbak.getInstance().getAppVersion();
    if (registeredVersion != currentVersion) {
      Log.v(TAG, "App version changed.");
      return null;
    }

    long expirationTime = prefs.getLong(KEY_EXPIRATION_TIME, -1);
    boolean expired = System.currentTimeMillis() > expirationTime;
    if (expired) {
      Log.v(TAG, "Registration expired.");
      return null;
    }

    return registrationId;
  }

  public void clear() {
    SharedPreferences prefs = Kikbak.getInstance().getSharedPreferences(REG, Context.MODE_PRIVATE);
    prefs.edit().clear().commit();
  }

  private void register() {
    new RegisterTask().execute();
  }

  private class RegisterTask extends Task {

    @Override
    protected void doInBackground() throws IOException {
      Context ctx = Kikbak.getInstance();
      GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx);
      String senderId = ctx.getString(R.string.gcm_sender_id);
      String regId = gcm.register(senderId);
      reportToServer(regId);
      save(regId);
    }

    private void reportToServer(String regId) throws IOException {
      long userId = Register.getInstance().getUserId();
      DeviceTokenUpdateRequest req = new DeviceTokenUpdateRequest();
      req.user = new UserIdType();
      req.user.userId = userId;
      req.token = new DeviceTokenType();
      req.token.token = regId;

      String uri = Http.getUri(DeviceTokenUpdateRequest.PATH + userId);
      Http.execute(uri, req, DeviceTokenUpdateResponse.class);
    }

    private void save(String regId) {
      Kikbak kikbak = Kikbak.getInstance();
      int appVersion = kikbak.getAppVersion();
      long expirationTime = System.currentTimeMillis() + EXPIRY_TIME_MS;

      SharedPreferences prefs = kikbak.getSharedPreferences(REG, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(KEY_REGISTRATION_ID, regId);
      editor.putInt(KEY_APP_VERSION, appVersion);
      editor.putLong(KEY_EXPIRATION_TIME, expirationTime);
      editor.commit();
    }

    @Override
    protected void done() {
      if (!isSuccessful()) {
        Exception e = getException();
        Log.e(TAG, "Registration failed:" + e.getMessage(), e);
      }
    }
  }
}
