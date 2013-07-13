
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
  private static Register sInstance;

  private Context mContext;
  private SharedPreferences mPref;
  boolean mIsRegistered;
  long mUserId;

  public static synchronized Register getInstance() {
    if (sInstance == null) {
      sInstance = new Register();
    }
    return sInstance;
  }

  Register() {
    mContext = Kikbak.getInstance();
    mPref = mContext.getSharedPreferences(REG, Context.MODE_PRIVATE);
    mIsRegistered = mPref.getBoolean(KEY_IS_REGISTERED, false);
    mUserId = mPref.getLong(KEY_USER_ID, -1);
  }

  public boolean isRegistered() {
    return mIsRegistered;
  }

  public long getUserId() {
    return mUserId;
  }

  public void registerUser(long userId) {
    Editor editor = mPref.edit();
    editor.putBoolean(KEY_IS_REGISTERED, true);
    editor.putLong(KEY_USER_ID, userId);
    editor.apply();
    mUserId = userId;
    mIsRegistered = true;
  }

  public void clear() {
    Editor editor = mPref.edit();
    editor.remove(KEY_IS_REGISTERED);
    editor.remove(KEY_USER_ID);
    editor.apply();
    mUserId = 0;
    mIsRegistered = false;
    clearFbSession();
  }

  private void clearFbSession() {
    Session s = Session.openActiveSessionFromCache(Kikbak.getInstance());
    if (s != null) {
      s.closeAndClearTokenInformation();
    }
  }
}
