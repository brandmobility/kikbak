
package com.referredlabs.kikbak.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.Kikbak;

import java.util.List;

public class LocationFinder implements LocationListener {

  private static final int MIN_TIME_MS = 3 * 1000;
  private static final int MIN_DISTANCE = 100;
  private static final int TWO_MINUTES = 1000 * 60 * 2;

  private LocationManager mLocMgr;
  private Location mBestLocation;
  private LocationFinderListener mListener;

  public interface LocationFinderListener {
    void onLocationUpdated(Location location);
  }

  public LocationFinder(LocationFinderListener listener) {
    mLocMgr = (LocationManager) Kikbak.getInstance().getSystemService(
        Context.LOCATION_SERVICE);
    mListener = listener;
  }

  public void startLocating() {
    mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_MS, MIN_DISTANCE,
        this);
    mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_MS, MIN_DISTANCE, this);
  }

  public void stopLocating() {
    mLocMgr.removeUpdates(this);
  }

  public static boolean isLocationEnabled() {
    LocationManager manager = (LocationManager) Kikbak.getInstance().getSystemService(
        Context.LOCATION_SERVICE);
    return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  public static Location getLastLocation() {
    Location bestLocation = null;
    LocationManager manager = (LocationManager) Kikbak.getInstance().getSystemService(
        Context.LOCATION_SERVICE);

    List<String> providers = manager.getAllProviders();
    for (String provider : providers) {
      Location location = manager.getLastKnownLocation(provider);
      if (location == null) {
        continue;
      }
      if (isBetterLocation(location, bestLocation)) {
        bestLocation = location;
      }
    }

    if (C.USE_FIXED_LOCATION)
      return C.FIXED_LOCATION;

    return bestLocation;
  }

  /**
   * Determines whether one Location reading is better than the current Location fix
   * 
   * @param location The new Location that you want to evaluate
   * @param currentBestLocation The current Location fix, to which you want to compare the new one
   */
  protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
        currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  /** Checks whether two providers are the same */
  protected static boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }

  @Override
  public void onLocationChanged(Location location) {
    if (C.USE_FIXED_LOCATION)
      location = C.FIXED_LOCATION;

    android.util.Log.d("MMM", "onLocationChanged:" + location);
    if (isBetterLocation(location, mBestLocation)) {
      mBestLocation = location;
      mListener.onLocationUpdated(location);
    }
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub
    android.util.Log.d("MMM", "onStatusChanged:" + status);

  }

  @Override
  public void onProviderEnabled(String provider) {
    // TODO Auto-generated method stub
    android.util.Log.d("MMM", "onProviderEnabled:" + provider);
  }

  @Override
  public void onProviderDisabled(String provider) {
    // TODO Auto-generated method stub
    android.util.Log.d("MMM", "onProviderDisabled:" + provider);
  }
}
