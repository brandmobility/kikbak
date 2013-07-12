
package com.referredlabs.kikbak.utils;

import android.location.Location;

import com.referredlabs.kikbak.data.OfferLocationType;

public class Nearest {
  private static float[] sResult = new float[1];
  private OfferLocationType[] mLocations;
  private OfferLocationType mNearestLocation;
  private float mDistanceToNearest;

  public Nearest(OfferLocationType[] locations) {
    mLocations = locations;
  }

  public void determineNearestLocation(double latitude, double longitude) {
    float[] results = sResult;
    float distance = Float.MAX_VALUE;
    OfferLocationType nearest = null;

    for (OfferLocationType loc : mLocations) {
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
