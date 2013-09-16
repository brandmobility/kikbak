
package com.referredlabs.kikbak.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;

import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.service.LocationFinder;

public class OffersLoader extends AsyncTaskLoader<List<TheOffer>> {

  Observer mObserver;
  DataStore mStore;

  public OffersLoader(Context context) {
    super(context);
    mStore = DataStore.getInstance();
  }

  @Override
  protected void onStartLoading() {
    super.onStartLoading();
    forceLoad();

    if (mObserver == null) {
      mObserver = new Observer();
      mStore.registerOffersObserver(mObserver);
    }
  }

  @Override
  public ArrayList<TheOffer> loadInBackground() {

    try {
      List<ClientOfferType> offers = mStore.getOffers();

      Location current = LocationFinder.getLastLocation();
      double latitude = current.getLatitude();
      double longitude = current.getLongitude();

      ArrayList<TheOffer> result = new ArrayList<TheOffer>();
      for (ClientOfferType offer : offers) {
        TheOffer theOffer = new TheOffer(offer.id, offer, latitude, longitude);
        result.add(theOffer);
      }

      sort(result);
      return result;
    } catch (Exception e) {
      // ignore for now
    }
    return null;
  }

  private void sort(ArrayList<TheOffer> result) {
    Collections.sort(result, new Comparator<TheOffer>() {
      @Override
      public int compare(TheOffer lhs, TheOffer rhs) {
        float l = lhs.getDistance();
        float r = rhs.getDistance();
        return Float.compare(l, r);
      }
    });
  }

  @Override
  protected void onReset() {
    super.onReset();
    if (mObserver != null) {
      mStore.unregisterOffersObserver(mObserver);
      mObserver = null;
    }
  }

  private class Observer extends DataSetObserver {
    public void onChanged() {
      onContentChanged();
    };
  };

}
