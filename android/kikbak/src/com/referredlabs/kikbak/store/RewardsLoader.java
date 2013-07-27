
package com.referredlabs.kikbak.store;

import android.content.Context;
import android.database.DataSetObserver;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.service.LocationFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

    DataService.getInstance().refreshRewards(false);
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
        entry = new TheReward(id, gift.merchant);
        entry.calculateDistance(latitude, longitude);
        map.put(id, entry);
      }
      entry.addGift(gift);
    }

    for (AvailableCreditType credit : credits) {
      long id = credit.offerId;
      TheReward entry = map.get(id);
      if (entry == null) {
        entry = new TheReward(id, credit.merchant);
        entry.calculateDistance(latitude, longitude);
        map.put(id, entry);
      }
      entry.addCredit(credit);
    }

    ArrayList<TheReward> result = new ArrayList<TheReward>(map.values());
    sort(result);
    return result;
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
    mStore.unregisterRewardsObserver(mObserver);
  }

  private class Observer extends DataSetObserver {
    public void onChanged() {
      onContentChanged();
    };
  };

}
