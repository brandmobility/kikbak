
package com.referredlabs.kikbak;

import android.location.Location;

public class C {
  public static boolean USE_FIXED_LOCATION = false;

  public static final double LATITUDE = 37.44;
  public static final double LONGITUDE = -122.17;
  public static final String SERVER = "54.244.124.116";

  // public static final double LATITUDE = 50.01250;
  // public static final double LONGITUDE = 20.98833;
  // public static final String SERVER = "192.168.0.101";

  public static final short PORT = 8080;

  public static final boolean BYPASS_STORE_CHECK = true;
  public static final float IN_STORE_DISTANCE = 100; // 100 meters

  public static final float REFETCH_DISTANCE = 2000; // 2 km

  public static final float RECALCULATE_DISTANCE = 200; // 200 meters

  public static Location FIXED_LOCATION;
  static {
    FIXED_LOCATION = new Location("fake");
    FIXED_LOCATION.setLatitude(LATITUDE);
    FIXED_LOCATION.setLongitude(LONGITUDE);
  }

}
