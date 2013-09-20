
package com.referredlabs.kikbak;

import android.location.Location;

// Debug settings

public class D {
  public static boolean USE_FIXED_LOCATION = false;
  public static boolean BYPASS_STORE_CHECK = false;

  public static Location FIXED_LOCATION;
  public static double LATITUDE = 37.7602;
  public static double LONGITUDE = -122.4351;
  static {
    FIXED_LOCATION = new Location("fake");
    FIXED_LOCATION.setLatitude(LATITUDE);
    FIXED_LOCATION.setLongitude(LONGITUDE);
  }
}
