
package com.referredlabs.kikbak.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GiftType;

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

  public void creditUsed(long id, double creditUsed) {
    // This is unsafe
    // - may have already a updated credit value from server (slim chance)
    // - threading, double assignment is not atomic (stay on main thread)
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

}
