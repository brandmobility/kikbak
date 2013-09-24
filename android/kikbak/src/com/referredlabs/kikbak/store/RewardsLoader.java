
package com.referredlabs.kikbak.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.service.LocationFinder;

public class RewardsLoader extends AsyncTaskLoader<List<TheReward>> {

  Observer mObserver;
  DataStore mStore;

  public RewardsLoader(Context context) {
    super(context);
    mStore = DataStore.getInstance();
  }

  @Override
  protected void onStartLoading() {
    super.onStartLoading();
    forceLoad();

    if (mObserver == null) {
      mObserver = new Observer();
      mStore.registerRewardsObserver(mObserver);
    }
  }

  @Override
  public List<TheReward> loadInBackground() {

    try {
      List<GiftType> gifts = mStore.getGifts();
      List<AvailableCreditType> credits = mStore.getCredits();

      Location current = LocationFinder.getLastLocation();
      double latitude = current.getLatitude();
      double longitude = current.getLongitude();

      HashMap<Long, TheReward> map = new HashMap<Long, TheReward>();

      for (GiftType gift : gifts) {
        long id = gift.offerId;
        TheReward entry = map.get(id);
        if (entry == null) {
          entry = new TheReward(id, gift.merchant, latitude, longitude);
          map.put(id, entry);
        }
        entry.addGift(gift);
      }

      for (AvailableCreditType credit : credits) {
        long id = credit.offerId;
        TheReward entry = map.get(id);
        if (entry == null) {
          entry = new TheReward(id, credit.merchant, latitude, longitude);
          map.put(id, entry);
        }
        entry.addCredit(credit);
      }

      ArrayList<TheReward> result = new ArrayList<TheReward>(map.values());
      sort(result);
      return result;
    } catch (Exception e) {
      // ignore
    }
    return null;
  }

  private void sort(ArrayList<TheReward> result) {
    Collections.sort(result, new Comparator<TheReward>() {
      @Override
      public int compare(TheReward lhs, TheReward rhs) {
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
      mStore.unregisterRewardsObserver(mObserver);
      mObserver = null;
    }
  }

  private class Observer extends DataSetObserver {
    public void onChanged() {
      onContentChanged();
    };
  }
}
