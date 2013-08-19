
package com.referredlabs.kikbak.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.Session;
import com.referredlabs.kikbak.Kikbak;

public class Register {

  private static final String REG = "registration";
  private static final String KEY_IS_REGISTERED = "is_registered";
  private static final String KEY_USER_ID = "user_id";
  private static final String KEY_USER_NAME = "user_name";
  private static Register sInstance;

  private Context mContext;
  private SharedPreferences mPref;

  public static synchronized Register getInstance() {
    if (sInstance == null) {
      sInstance = new Register();
    }
    return sInstance;
  }

  Register() {
    mContext = Kikbak.getInstance();
    mPref = mContext.getSharedPreferences(REG, Context.MODE_PRIVATE);
  }

  public boolean isRegistered() {
    return mPref.getBoolean(KEY_IS_REGISTERED, false);
  }

  public long getUserId() {
    return mPref.getLong(KEY_USER_ID, -1);
  }

  public String getUserName() {
    return mPref.getString(KEY_USER_NAME, "");
  }

  public void registerUser(long userId, String userName) {
    Editor editor = mPref.edit();
    editor.putBoolean(KEY_IS_REGISTERED, true);
    editor.putLong(KEY_USER_ID, userId);
    editor.putString(KEY_USER_NAME, userName);
    editor.commit();
  }

  public void clear() {
    Editor editor = mPref.edit();
    editor.clear();
    editor.commit();
    clearFbSession();
  }

  private void clearFbSession() {
    Session s = Session.openActiveSessionFromCache(Kikbak.getInstance());
    if (s != null) {
      s.closeAndClearTokenInformation();
    }
  }
}
