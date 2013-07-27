
package com.referredlabs.kikbak.store;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GiftType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataStore {

  private static DataStore sInstance;

  public interface UpdateListener {
    void onOffersUpdated();

    void onRewardsUpdated();
  }

  private ArrayList<GiftType> mGifts = new ArrayList<GiftType>();
  private ArrayList<AvailableCreditType> mCredits = new ArrayList<AvailableCreditType>();
  private ArrayList<ClientOfferType> mOffers = new ArrayList<ClientOfferType>();

  private DataSetObservable mOffersObservable = new DataSetObservable();
  private DataSetObservable mRewardsObservable = new DataSetObservable();

  public static DataStore getInstance() {
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

  void swap(ClientOfferType[] offers) {
    synchronized (this) {
      mOffers.clear();
      mOffers.addAll(Arrays.asList(offers));
    }
    mOffersObservable.notifyChanged();
  }

  void swap(GiftType[] gifts, AvailableCreditType[] credits) {
    synchronized (this) {
      mGifts.clear();
      mGifts.addAll(Arrays.asList(gifts));

      mCredits.clear();
      mCredits.addAll(Arrays.asList(credits));
    }
    mRewardsObservable.notifyChanged();
  }

  public synchronized List<ClientOfferType> getOffers() {
    return new ArrayList<ClientOfferType>(mOffers);
  }

  public synchronized List<GiftType> getGifts() {
    return new ArrayList<GiftType>(mGifts);
  }

  public synchronized List<AvailableCreditType> getCredits() {
    return new ArrayList<AvailableCreditType>(mCredits);
  }

}
