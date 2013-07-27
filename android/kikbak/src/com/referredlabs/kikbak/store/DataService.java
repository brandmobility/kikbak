
package com.referredlabs.kikbak.store;

import android.location.Location;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.data.GetUserOffersRequest;
import com.referredlabs.kikbak.data.GetUserOffersResponse;
import com.referredlabs.kikbak.data.RewardsRequest;
import com.referredlabs.kikbak.data.RewardsResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.http.SafeResponse;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.utils.Register;

public class DataService {

  private static DataService sInstance;

  private static final long RELOAD_TIME_MS = 5 * 60 * 1000;

  private DataStore mStore;

  private SafeResponse<GetUserOffersRequest, GetUserOffersResponse> mLastRefreshOffersResponse;
  private int mRefreshOffersInProgress;

  private SafeResponse<RewardsRequest, RewardsResponse> mLastRefreshRewardsResponse;
  private int mRefreshRewardsInProgress;

  public static DataService getInstance() {
    if (sInstance == null) {
      sInstance = new DataService();
    }
    return sInstance;
  }

  private DataService() {
    mStore = DataStore.getInstance();
  }

  // offers

  public synchronized boolean refreshOffers(boolean force) {
    if (force || shouldRefreshOffers())
      executeRefreshOffers();
    return mRefreshOffersInProgress > 0;
  }

  private synchronized boolean shouldRefreshOffers() {
    if (mRefreshOffersInProgress > 0)
      return false;

    if (mLastRefreshOffersResponse == null)
      return true;

    long now = System.currentTimeMillis();
    if (mLastRefreshOffersResponse.startTime + RELOAD_TIME_MS < now)
      return true;

    return false;
  }

  private synchronized void executeRefreshOffers() {
    ++mRefreshOffersInProgress;
    Kikbak.PARALLEL_EXECUTOR.execute(
        new Runnable() {
          @Override
          public void run() {
            long userId = Register.getInstance().getUserId();
            Location loc = LocationFinder.getLastLocation();
            String uri = Http.getUri(GetUserOffersRequest.PATH + userId);
            GetUserOffersRequest req =
                GetUserOffersRequest.create(loc.getLatitude(), loc.getLongitude());
            SafeResponse<GetUserOffersRequest, GetUserOffersResponse> result =
                Http.executeSafe(uri, req, GetUserOffersResponse.class);
            commitOffers(result);
          }
        });
  }

  private synchronized void commitOffers(
      SafeResponse<GetUserOffersRequest, GetUserOffersResponse> result) {
    if (result.isSuccessful()) {
      mStore.swap(result.response.offers);
    }
    mLastRefreshOffersResponse = result;
    --mRefreshOffersInProgress;
  }

  // rewards

  public synchronized boolean refreshRewards(boolean force) {
    if (force || shouldRefreshRewards())
      executeRefreshRewards();
    return mRefreshRewardsInProgress > 0;
  }

  private synchronized boolean shouldRefreshRewards() {
    if (mRefreshRewardsInProgress > 0)
      return false;

    if (mLastRefreshRewardsResponse == null)
      return true;

    long now = System.currentTimeMillis();
    if (mLastRefreshRewardsResponse.startTime + RELOAD_TIME_MS < now)
      return true;

    return false;
  }

  private synchronized void executeRefreshRewards() {
    ++mRefreshRewardsInProgress;
    Kikbak.PARALLEL_EXECUTOR.execute(
        new Runnable() {
          @Override
          public void run() {
            long userId = Register.getInstance().getUserId();
            String uri = Http.getUri(RewardsRequest.PATH + userId);
            RewardsRequest req = new RewardsRequest();
            SafeResponse<RewardsRequest, RewardsResponse> result =
                Http.executeSafe(uri, req, RewardsResponse.class);
            commitRewards(result);
          }
        });
  }

  private synchronized void commitRewards(SafeResponse<RewardsRequest, RewardsResponse> result) {
    if (result.isSuccessful()) {
      mStore.swap(result.response.gifts, result.response.credits);
    }
    mLastRefreshRewardsResponse = result;
    --mRefreshRewardsInProgress;
  }

}
