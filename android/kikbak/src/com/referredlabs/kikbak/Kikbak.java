
package com.referredlabs.kikbak;

import android.app.Application;

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

}
