
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RewardAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mInflater;
  private List<Reward> mRewards;

  public RewardAdapter(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
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
      helper = new RewardHelper(view, null);
      view.setTag(helper);
    } else {
      helper = (RewardHelper) view.getTag();
    }

    Reward reward = getItem(position);

    helper.setLink(reward.mMerchant.url);
    helper.setPhone(Long.toString(reward.mNearestLocation.phoneNumber));
    helper.setDistance(reward.mDistance, null); // TODO:

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
