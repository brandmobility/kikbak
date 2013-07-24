
package com.referredlabs.kikbak.utils;

import android.location.Location;

import com.referredlabs.kikbak.data.MerchantLocationType;

public class Nearest {
  private static float[] sResult = new float[1];
  private MerchantLocationType[] mLocations;
  private MerchantLocationType mNearestLocation;
  private float mDistanceToNearest;

  public Nearest(MerchantLocationType[] locations) {
    mLocations = locations;
  }

  public void determineNearestLocation(double latitude, double longitude) {
    float[] results = sResult;
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

  public double getLatitude() {
    return mNearestLocation.latitude;
  }

  public double getLongitude() {
    return mNearestLocation.longitude;
  }

  public long getPhoneNumber() {
    return mNearestLocation.phoneNumber;
  }

  // public OfferLocationType getLocation() {
  // return mNearestLocation;
  // }

}
