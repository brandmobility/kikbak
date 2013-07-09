
package com.referredlabs.kikbak.ui;

import android.location.Location;
import android.os.AsyncTask;

import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GetUserOffersRequest;
import com.referredlabs.kikbak.data.GetUserOffersResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Nearest;
import com.referredlabs.kikbak.utils.OffersLocationComparator;
import com.referredlabs.kikbak.utils.Register;

import java.util.Arrays;
import java.util.HashMap;

public class RefreshOfferTask extends AsyncTask<Void, Void, Void> {

  public interface RefreshOfferListener {
    void onNewOffer(ClientOfferType[] offers, HashMap<ClientOfferType, Nearest> locationMap);
  }

  private long mUserId;
  private ClientOfferType[] mOffers;
  private HashMap<ClientOfferType, Nearest> mLocationMap;
  private Location mLocation;
  private RefreshOfferListener mListener;

  RefreshOfferTask(RefreshOfferListener listener, Location location) {
    mUserId = Register.getInstance().getUserId();
    mListener = listener;
    mLocation = location;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      double latitude = mLocation.getLatitude();
      double longitude = mLocation.getLongitude();
      String uri = Http.getUri(GetUserOffersRequest.PATH + mUserId);

      GetUserOffersRequest req = GetUserOffersRequest.create(latitude, longitude);
      GetUserOffersResponse resp = Http.execute(uri, req, GetUserOffersResponse.class);

      mOffers = resp.offers;
      mLocationMap = new HashMap<ClientOfferType, Nearest>();
      for (ClientOfferType offer : mOffers) {
        Nearest n = new Nearest(offer.locations);
        n.determineNearestLocation(latitude, longitude);
        mLocationMap.put(offer, n);
        offer.mCurrentDistance = n.getDistance();
      }

      Arrays.sort(mOffers, new OffersLocationComparator(mLocationMap));
    } catch (Exception e) {
      android.util.Log.d("MMM", "Exception while fetching a offer list:" + e);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    mListener.onNewOffer(mOffers, mLocationMap);
  }

}
