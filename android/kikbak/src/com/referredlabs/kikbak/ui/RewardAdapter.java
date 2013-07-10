
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.ui.IconBarHelper.IconBarListener;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RewardAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mInflater;
  private List<Reward> mRewards;
  IconBarListener mIconBarListener;

  public RewardAdapter(Context context, IconBarListener iconBarListener) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
    mIconBarListener = iconBarListener;
  }

  @Override
  public int getCount() {
    return mRewards == null ? 0 : mRewards.size();
  }

  @Override
  public Reward getItem(int position) {
    return mRewards.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position; // no fixed ids
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    RewardHelper helper;
    if (view == null) {
      view = mInflater.inflate(R.layout.reward, parent, false);
      helper = new RewardHelper(view, mIconBarListener);
      view.setTag(helper);
    } else {
      helper = (RewardHelper) view.getTag();
    }

    Reward reward = getItem(position);

    Location location = LocationFinder.getLastLocation();
    Nearest nearest = new Nearest(reward.mMerchant.locations);
    nearest.determineNearestLocation(location.getLatitude(), location.getLongitude());
    helper.setLocation(nearest);
    helper.setPhone(Long.toString(nearest.getPhoneNumber()));
    helper.setLink(reward.mMerchant.url);

    helper.setMerchantName(reward.mMerchant.name);

    Uri url = Uri.parse(reward.mMerchant.imageUrl);
    Picasso.with(mContext).load(url).into(helper.mImage);

    helper.setGiftValue(reward.getGiftValueString());
    helper.setCreditValue(reward.getCreditValueString());

    return view;
  }

  public void swap(List<Reward> rewards) {
    mRewards = rewards;
    notifyDataSetChanged();
  }

}
