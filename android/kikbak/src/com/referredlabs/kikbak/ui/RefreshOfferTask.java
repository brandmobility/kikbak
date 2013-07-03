
package com.referredlabs.kikbak.ui;

import android.os.AsyncTask;

import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GetUserOffersRequest;
import com.referredlabs.kikbak.data.GetUserOffersResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.OffersLocationComparator;

import java.util.Arrays;

public class RefreshOfferTask extends AsyncTask<Void, Void, Void> {

  private long mUserId;
  private OfferAdapter mAdapter;
  private ClientOfferType[] mOffers;

  RefreshOfferTask(long userId, OfferAdapter adapter) {
    mUserId = userId;
    mAdapter = adapter;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      String uri = Http.getUri(GetUserOffersRequest.PATH + mUserId);
      GetUserOffersRequest req = GetUserOffersRequest.create(C.LATITUDE, C.LONGITUDE);
      GetUserOffersResponse resp = Http.execute(uri, req, GetUserOffersResponse.class);

      mOffers = resp.offers;
      Arrays.sort(mOffers, new OffersLocationComparator(C.LATITUDE, C.LONGITUDE));
    } catch (Exception e) {
      android.util.Log.d("MMM", "Exception while fetching a offer list:" + e);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    mAdapter.swap(mOffers);
  }

}
