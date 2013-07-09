
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.ui.RefreshOfferTask.RefreshOfferListener;
import com.referredlabs.kikbak.utils.Nearest;

import java.util.HashMap;

public class OfferListFragment extends Fragment implements OnItemClickListener,
    RefreshOfferListener {

  public interface OnOfferClickedListener {
    void onOfferClicked(ClientOfferType offer);
  }

  private ViewFlipper mFlipper;
  private ListView mListView;
  private OfferAdapter mAdapter;
  private OnOfferClickedListener mListener;
  private Location mLastLocation;
  private boolean mLocationDisabledShown;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnOfferClickedListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_offer_list, container, false);
    mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setOnItemClickListener(this);
    mAdapter = new OfferAdapter(getActivity(), new IconBarActionHandler(getActivity()));
    mListView.setAdapter(mAdapter);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    Location location = LocationFinder.getLastLocation();
    if (location == null) {
      if (!LocationFinder.isLocationEnabled()) {
        showLocationDisabledNotification();
        showEmpty();
      }
    } else {
      setUserLocation(location);
    }
  }

  private void showLocationDisabledNotification() {
    if(!mLocationDisabledShown) {
      mLocationDisabledShown = true;
      Toast.makeText(getActivity(), R.string.location_disabled, Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    ClientOfferType offer = (ClientOfferType) mListView.getItemAtPosition(position);
    mListener.onOfferClicked(offer);
  }

  public void setUserLocation(Location location) {
    assert (location != null);
    // if location is significantly better then fetch new list from server
    if (mLastLocation == null || location.distanceTo(mLastLocation) > 1000) {
      mLastLocation = location;
      new RefreshOfferTask(this, location).execute();
    } else {
      // just recalculate distance map
    }
  }

  public void showList() {
    mFlipper.setDisplayedChild(0);
  }

  public void showEmpty() {
    mFlipper.setDisplayedChild(1);
  }

  @Override
  public void onNewOffer(ClientOfferType[] offers, HashMap<ClientOfferType, Nearest> locationMap) {
    if (offers != null && offers.length > 0) {
      mAdapter.swap(offers, locationMap);
      showList();
    } else {
      showEmpty();
    }
  }
}
