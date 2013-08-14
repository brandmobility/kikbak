
package com.referredlabs.kikbak.utils;

import android.location.Location;

import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.service.LocationFinder;

public class Nearest {
  private MerchantLocationType[] mLocations;
  private MerchantLocationType mNearestLocation;
  private float mDistanceToNearest;

  public Nearest(MerchantLocationType[] locations) {
    mLocations = locations;

    Location l = LocationFinder.getLastLocation();
    determineNearestLocation(l.getLatitude(), l.getLongitude());
  }

  public Nearest(MerchantLocationType[] locations, double latitude, double longitude) {
    mLocations = locations;
    determineNearestLocation(latitude, longitude);
  }

  private void determineNearestLocation(double latitude, double longitude) {
    float[] results = new float[1];
    float distance = Float.MAX_VALUE;
    MerchantLocationType nearest = null;

    for (MerchantLocationType loc : mLocations) {
      Location.distanceBetween(latitude, longitude, loc.latitude, loc.longitude, results);
      if (results[0] < distance) {
        distance = results[0];
        nearest = loc;
      }
    }

    mNearestLocation = nearest;
    mDistanceToNearest = distance;
  }

  public float getDistance() {
    return mDistanceToNearest;
  }

  public MerchantLocationType get() {
    return mNearestLocation;
  }

}
