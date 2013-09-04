
package com.referredlabs.kikbak.ui;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.store.DataStore;
import com.referredlabs.kikbak.store.OffersLoader;
import com.referredlabs.kikbak.store.TheOffer;

public class OfferListFragment extends Fragment implements OnItemClickListener,
    LoaderCallbacks<List<TheOffer>>, OnClickListener {

  public interface OnOfferClickedListener {
    void onOfferClicked(TheOffer theOffer);

    void onSuggestClicked();
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

    view.findViewById(R.id.suggest).setOnClickListener(this);
    return view;
  }

  @Override
  public void onClick(View v) {
    mListener.onSuggestClicked();
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
    if (!mLocationDisabledShown) {
      mLocationDisabledShown = true;
      Toast.makeText(getActivity(), R.string.location_disabled, Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    TheOffer offer = (TheOffer) mListView.getItemAtPosition(position);
    mListener.onOfferClicked(offer);
  }

  public void setUserLocation(Location location) {
    assert (location != null);
    if (mLastLocation == null) {
      getLoaderManager().initLoader(0, null, this);
    } else {
      // if location is significantly better then fetch new list from server
      if (C.REFETCH_DISTANCE < mLastLocation.distanceTo(location)) {
        DataStore.getInstance().refreshOffers();
      }
      if (C.RECALCULATE_DISTANCE < mLastLocation.distanceTo(location)) {
        Loader<Object> l = getLoaderManager().getLoader(0);
        l.onContentChanged();
      }
    }
    mLastLocation = location;
  }

  public void showList() {
    mFlipper.setDisplayedChild(0);
  }

  public void showEmpty() {
    mFlipper.setDisplayedChild(1);
  }

  @Override
  public Loader<List<TheOffer>> onCreateLoader(int id, Bundle args) {
    OffersLoader loader = new OffersLoader(getActivity());
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<List<TheOffer>> loader, List<TheOffer> result) {
    if (result == null || result.isEmpty()) {
      showEmpty();
    } else {
      mAdapter.swap(result);
      showList();
    }
  }

  @Override
  public void onLoaderReset(Loader<List<TheOffer>> loader) {
    // ???
  }
}
