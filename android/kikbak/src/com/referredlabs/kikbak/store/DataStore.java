
package com.referredlabs.kikbak.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.location.Location;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GetUserOffersRequest;
import com.referredlabs.kikbak.data.GetUserOffersResponse;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.RewardsRequest;
import com.referredlabs.kikbak.data.RewardsResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.tasks.Task;
import com.referredlabs.kikbak.utils.Register;

public class DataStore {

  private static final long REFRESH_TIME_MS = 5 * 60 * 1000;

  private static DataStore sInstance;

  private GetOffersTask mOffersTask;
  private ArrayList<ClientOfferType> mOffers = new ArrayList<ClientOfferType>();

  private GetRewardsTask mRewardsTask;
  private ArrayList<GiftType> mGifts = new ArrayList<GiftType>();
  private ArrayList<AvailableCreditType> mCredits = new ArrayList<AvailableCreditType>();

  private DataSetObservable mOffersObservable = new DataSetObservable();
  private DataSetObservable mRewardsObservable = new DataSetObservable();

  public static synchronized DataStore getInstance() {
    if (sInstance == null) {
      sInstance = new DataStore();
    }
    return sInstance;
  }

  private DataStore() {
  }

  public void registerOffersObserver(DataSetObserver observer) {
    mOffersObservable.registerObserver(observer);
  }

  public void unregisterOffersObserver(DataSetObserver observer) {
    mOffersObservable.unregisterObserver(observer);
  }

  public void registerRewardsObserver(DataSetObserver observer) {
    mRewardsObservable.registerObserver(observer);
  }

  public void unregisterRewardsObserver(DataSetObserver observer) {
    mRewardsObservable.unregisterObserver(observer);
  }

  public void refreshOffers() {
    executeFetchOffers();
  }

  private synchronized void executeFetchOffers() {
    mOffersTask = new GetOffersTask();
    mOffersTask.execute();
  }

  public List<ClientOfferType> getOffers() throws Exception {
    GetOffersTask task = fetchOffersIfNeeded();
    task.get(); // wait until done, will throw if task throws
    return getOffersLocal();
  }

  private synchronized GetOffersTask fetchOffersIfNeeded() {
    if (shouldRefreshOffers()) {
      executeFetchOffers();
    }
    return mOffersTask;
  }

  private boolean shouldRefreshOffers() {
    if (mOffersTask == null)
      return true;
    if (mOffersTask.isCancelled())
      return true;

    long now = System.currentTimeMillis();
    if (mOffersTask.isDone() && mOffersTask.getCompletionTimeMillis() + REFRESH_TIME_MS < now)
      return true;

    return false;
  }

  void swapOffers(List<ClientOfferType> offers) {
    synchronized (this) {
      mOffers.clear();
      mOffers.addAll(offers);
    }
    mOffersObservable.notifyChanged();
  }

  public synchronized List<ClientOfferType> getOffersLocal() {
    return new ArrayList<ClientOfferType>(mOffers);
  }

  public List<GiftType> getGifts() throws Exception {
    getRewards();
    return getGiftsLocal();
  }

  public List<AvailableCreditType> getCredits() throws Exception {
    getRewards();
    return getCreditsLocal();
  }

  public void refreshRewards() {
    executeFetchRewards();
  }

  private synchronized void executeFetchRewards() {
    mRewardsTask = new GetRewardsTask();
    mRewardsTask.execute();
  }

  private void getRewards() throws Exception {
    GetRewardsTask task = fetchRewardsIfNeeded();
    task.get(); // wait until done, will throw if task throws
  }

  private synchronized GetRewardsTask fetchRewardsIfNeeded() {
    if (shouldRefreshRewards()) {
      executeFetchRewards();
    }
    return mRewardsTask;
  }

  private boolean shouldRefreshRewards() {
    if (mRewardsTask == null)
      return true;
    if (mRewardsTask.isCancelled())
      return true;

    long now = System.currentTimeMillis();
    if (mRewardsTask.isDone() && mRewardsTask.getCompletionTimeMillis() + REFRESH_TIME_MS < now)
      return true;

    return false;
  }

  void swapRewards(List<GiftType> gifts, List<AvailableCreditType> credits) {
    synchronized (this) {
      mGifts.clear();
      mGifts.addAll(gifts);

      mCredits.clear();
      mCredits.addAll(credits);
    }
    mRewardsObservable.notifyChanged();
  }

  public synchronized List<GiftType> getGiftsLocal() {
    return new ArrayList<GiftType>(mGifts);
  }

  public synchronized List<AvailableCreditType> getCreditsLocal() {
    return new ArrayList<AvailableCreditType>(mCredits);
  }

  public synchronized boolean isEmpty() {
    return mOffers.isEmpty() && mGifts.isEmpty() && mCredits.isEmpty();
  }

  public void creditUsed(long id, double creditUsed) {
    // This could be unsafe
    // - may have already a updated credit value from server (slim chance)
    synchronized (this) {
      Iterator<AvailableCreditType> it = mCredits.iterator();
      while (it.hasNext()) {
        AvailableCreditType credit = it.next();
        if (credit.id == id) {
          credit.value = Math.max(0, credit.value - creditUsed);
          if (credit.value == 0) {
            it.remove();
          }
          break;
        }
      }
    }
    mRewardsObservable.notifyChanged();
  }

  public synchronized ClientOfferType getOffer(long id) {
    for (ClientOfferType offer : mOffers) {
      if (offer.id == id)
        return offer;
    }
    return null;
  }

  public void giftUsed(long offerId) {
    synchronized (this) {
      Iterator<GiftType> it = mGifts.iterator();
      while (it.hasNext()) {
        GiftType gift = it.next();
        if (gift.offerId == offerId) {
          it.remove();
        }
        break;
      }
    }
    mRewardsObservable.notifyChanged();
  }

  private class GetOffersTask extends Task<Void> {
    @Override
    protected Void doInBackground() throws IOException {
      long userId = Register.getInstance().getUserId();
      Location loc = LocationFinder.getLastLocation();
      String uri = Http.getUri(GetUserOffersRequest.PATH + userId);
      GetUserOffersRequest req =
          GetUserOffersRequest.create(loc.getLatitude(), loc.getLongitude());
      GetUserOffersResponse result = Http.execute(uri, req, GetUserOffersResponse.class);
      swapOffers(Arrays.asList(result.offers));
      return null;
    }
  }

  private class GetRewardsTask extends Task<Void> {
    @Override
    protected Void doInBackground() throws Exception {
      long userId = Register.getInstance().getUserId();
      String uri = Http.getUri(RewardsRequest.PATH + userId);
      RewardsRequest req = new RewardsRequest();
      RewardsResponse response = Http.execute(uri, req, RewardsResponse.class);
      swapRewards(Arrays.asList(response.gifts), Arrays.asList(response.credits));
      return null;
    }
  }

}
