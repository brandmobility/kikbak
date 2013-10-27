
package com.referredlabs.kikbak;

// Config options

public class C {

  public static final String REST_URI;
  public static final String UPLOAD_URI;

  static {
    if (BuildConfig.DEBUG) {
      REST_URI = "http://test.kikbak.me/m/kikbak";
      UPLOAD_URI = "http://test.kikbak.me/s/upload.php";
    } else {
      REST_URI = "https://m.kikbak.me/m/kikbak";
      UPLOAD_URI = "https://m.kikbak.me/s/upload.php";
    }
  }

  public static final float IN_STORE_DISTANCE = 100; // 100 meters

  public static final float REFETCH_DISTANCE = 2000; // 2 km

  public static final float RECALCULATE_DISTANCE = 200; // 200 meters

  public static final float CLOSE_TO_STORE_DISTANCE = 804; // 804 meters = 0.5 mile
}
