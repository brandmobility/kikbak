
package com.referredlabs.kikbak;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class Kikbak extends Application {

  private static Kikbak mInstance;

  public static Kikbak getInstance() {
    return mInstance;
  }

  private static void setInstance(Kikbak instance) {
    mInstance = instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    setInstance(this);
  }

  public int getAppVersion() {
    try {
      PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

}
