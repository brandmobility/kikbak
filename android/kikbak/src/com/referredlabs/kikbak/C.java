
package com.referredlabs.kikbak;

import android.location.Location;

public class C {
  public static final String REST_URI = "https://m.kikbak.me/m/kikbak";
  public static final String UPLOAD_URI = "http://m.kikbak.me/s/upload.php";

  public static boolean USE_FIXED_LOCATION = false;
  public static final double LATITUDE = 37.7602;
  public static final double LONGITUDE = -122.4351;

  public static boolean BYPASS_STORE_CHECK = false;
  public static final float IN_STORE_DISTANCE = 100; // 100 meters

  public static final float REFETCH_DISTANCE = 2000; // 2 km

  public static final float RECALCULATE_DISTANCE = 200; // 200 meters

  public static final float CLOSE_TO_STORE_DISTANCE = 804; // 804 meters = 0.5 mile

  public static Location FIXED_LOCATION;
  static {
    FIXED_LOCATION = new Location("fake");
    FIXED_LOCATION.setLatitude(LATITUDE);
    FIXED_LOCATION.setLongitude(LONGITUDE);
  }
}
