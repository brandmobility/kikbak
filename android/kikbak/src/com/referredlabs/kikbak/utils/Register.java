
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
  private static final String KEY_FIRST_NAME = "first_name";
  private static final String KEY_FULL_NAME = "full_name";
  private static Register sInstance = new Register();

  private Context mContext;
  private SharedPreferences mPref;

  public static Register getInstance() {
    return sInstance;
  }

  private Register() {
    mContext = Kikbak.getInstance();
    mPref = mContext.getSharedPreferences(REG, Context.MODE_PRIVATE);
  }

  public boolean isRegistered() {
    return mPref.getBoolean(KEY_IS_REGISTERED, false);
  }

  public long getUserId() {
    return mPref.getLong(KEY_USER_ID, -1);
  }

  public String getFirstName() {
    return mPref.getString(KEY_FIRST_NAME, "");
  }

  public String getFullName() {
    return mPref.getString(KEY_FULL_NAME, "");
  }

  public void registerUser(long userId, String firstName, String fullName) {
    Editor editor = mPref.edit();
    editor.putBoolean(KEY_IS_REGISTERED, true);
    editor.putLong(KEY_USER_ID, userId);
    editor.putString(KEY_FIRST_NAME, firstName);
    editor.putString(KEY_FULL_NAME, fullName);
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
