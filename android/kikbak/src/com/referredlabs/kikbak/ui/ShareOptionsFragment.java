
package com.referredlabs.kikbak.ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.utils.Nearest;

public class ShareOptionsFragment extends DialogFragment implements OnClickListener {

  public static final String ARG_OFFER = "offer";

  private ClientOfferType mOffer;
  private Spinner mStoreSpinner;

  public interface OnShareMethodSelectedListener {
    public void onShareVia(String method, Class<? extends DialogFragment> clazz, Bundle args);
  }

  private OnShareMethodSelectedListener mListener;

  public static ShareOptionsFragment newInstance(ClientOfferType offer) {
    ShareOptionsFragment dialog = new ShareOptionsFragment();
    Bundle args = new Bundle();
    String data = new Gson().toJson(offer);
    args.putString(ARG_OFFER, data);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, 0);
    String data = getArguments().getString(ARG_OFFER);
    mOffer = new Gson().fromJson(data, ClientOfferType.class);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnShareMethodSelectedListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_share_options, container, false);
    v.findViewById(R.id.via_email).setOnClickListener(this);
    v.findViewById(R.id.via_sms).setOnClickListener(this);
    v.findViewById(R.id.via_facebook).setOnClickListener(this);
    v.findViewById(R.id.via_twitter).setOnClickListener(this);
    mStoreSpinner = (Spinner) v.findViewById(R.id.store_selection);
    boolean hasManyLocations = mOffer.locations.length > 1;
    if (hasManyLocations) {
      ((TextView) v.findViewById(R.id.title)).setText(R.string.share_options_title_multistore);
      enableStoreSelection(inflater);
    }

    return v;
  }

  void enableStoreSelection(LayoutInflater inflater) {
    mStoreSpinner.setVisibility(View.VISIBLE);
    mStoreSpinner.setAdapter(new Adapter(inflater, mOffer.locations.clone()));

    float minDistance = new Nearest(mOffer.locations).getDistance();
    if (minDistance < C.CLOSE_TO_STORE_DISTANCE) {
      mStoreSpinner.setSelection(1); // preselect closest one
    }

    mStoreSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
        if (id == -1) {
          view.postDelayed(new Runnable() {
            @Override
            public void run() {
              view.requestFocus();
            }
          }, 500);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  @Override
  public void onClick(View v) {
    String employee = null;

    MerchantLocationType location = mOffer.locations[0];
    if (mStoreSpinner.getVisibility() == View.VISIBLE) {
      location = (MerchantLocationType) mStoreSpinner.getSelectedItem();
    }

    Bundle extraArgs = new Bundle();
    if(location != null)
      extraArgs.putLong(ShareViaBase.ARG_LOCATION_ID, location.locationId);
    extraArgs.putString(ShareViaBase.ARG_EMPLYOYEE, employee);

    switch (v.getId()) {
      case R.id.via_email:
        mListener.onShareVia("email", ShareViaEmailFragment.class, extraArgs);
        break;
      case R.id.via_facebook:
        mListener.onShareVia("facebook", ShareViaFacebookFragment.class, extraArgs);
        break;
      case R.id.via_sms:
        mListener.onShareVia("sms", ShareViaSmsFragment.class, extraArgs);
        break;
      case R.id.via_twitter:
        mListener.onShareVia("twitter", ShareViaTwitterFragment.class, extraArgs);
        break;
    }

    dismiss();
  }

  private class Adapter extends BaseAdapter {

    LayoutInflater mInflater;
    ArrayList<MerchantLocationType> mLocations;

    public Adapter(LayoutInflater inflater, MerchantLocationType[] locations) {
      mInflater = inflater;
      mLocations = new ArrayList<MerchantLocationType>(Arrays.asList(locations));
      Location l = LocationFinder.getLastLocation();
      Nearest.sortByDistance(mLocations, l.getLatitude(), l.getLongitude());
    }

    @Override
    public int getCount() {
      return mLocations.size() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      MerchantLocationType location = getItem(position);
      TextView tv = (TextView) mInflater.inflate(R.layout.fragment_share_options_store_view,
          parent, false);
      if (location == null) {
        int color = tv.getHintTextColors().getDefaultColor();
        tv.setTextColor(color);
        tv.setText(R.string.share_option_pick_location);
      } else {
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setText(location.address1);
      }
      return tv;
    }

    @Override
    public MerchantLocationType getItem(int position) {
      --position;
      if (position < 0) {
        return null; // "pick location"
      }
      return mLocations.get(position);
    }

    @Override
    public long getItemId(int position) {
      --position;
      if (position < 0) {
        return -1; // "pick location"
      }
      return mLocations.get(position).locationId;
    }

  }

}
