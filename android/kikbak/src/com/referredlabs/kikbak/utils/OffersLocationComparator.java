
package com.referredlabs.kikbak.utils;

import android.location.Location;

import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.OfferLocationType;

import java.util.Comparator;
import java.util.HashMap;

public class OffersLocationComparator implements Comparator<ClientOfferType> {

  public double mLatitude;
  public double mLongitude;
  public float mResult[] = new float[1];
  public HashMap<ClientOfferType, Float> mNearestMap = new HashMap<ClientOfferType, Float>();

  public OffersLocationComparator(double latitude, double longitude) {
    mLatitude = latitude;
    mLongitude = longitude;
  }

  @Override
  public int compare(ClientOfferType lhs, ClientOfferType rhs) {

    float l = getDistanceToNearestLocation(lhs);
    float r = getDistanceToNearestLocation(rhs);
    return Float.compare(l, r);
  }

  float getDistanceToNearestLocation(ClientOfferType offer) {
    if (mNearestMap.containsKey(offer)) {
      return mNearestMap.get(offer);
    }
    float[] results = mResult;
    float nearest = Float.MAX_VALUE;
    for (OfferLocationType location : offer.locations) {
      Location.distanceBetween(mLatitude, mLongitude, location.latitude, location.longitude,
          results);
      nearest = Math.min(nearest, results[0]);
    }
    mNearestMap.put(offer, nearest);

    offer.mCurrentDistance = nearest;

    return nearest;
  }
}
