
package com.referredlabs.kikbak.ui;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.store.TheReward;
import com.referredlabs.kikbak.ui.IconBarHelper.IconBarListener;
import com.referredlabs.kikbak.utils.LocaleUtils;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class RewardAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mInflater;
  private List<TheReward> mRewards;
  IconBarListener mIconBarListener;
  ItemAreaListener mAreaListener;

  public interface ItemAreaListener {
    void onGiftAreaClicked(TheReward reward);

    void onCreditAreaClicked(TheReward reward);
  }

  public RewardAdapter(Context context, IconBarListener iconBarListener,
      ItemAreaListener areaListener) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
    mIconBarListener = iconBarListener;
    mAreaListener = areaListener;
  }

  public void swap(List<TheReward> result) {
    mRewards = result;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mRewards == null ? 0 : mRewards.size();
  }

  @Override
  public TheReward getItem(int position) {
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
      helper = new RewardHelper(view, mIconBarListener, mAreaListener);
      view.setTag(helper);
    } else {
      helper = (RewardHelper) view.getTag();
    }

    TheReward reward = getItem(position);
    helper.setCurrentReward(reward);

    Nearest nearest = reward.getNearest();
    ClientMerchantType merchant = reward.getMerchant();

    helper.setLocation(nearest);
    helper.setPhone(Long.toString(nearest.get().phoneNumber));
    helper.setLink(merchant.url);
    helper.setMerchantName(merchant.name);

    Uri url = Uri.parse(reward.getImageUrl());
    helper.mImage.setImageResource(R.color.no_image);
    Picasso.with(mContext).load(url).into(helper.mImage);
    if (reward.hasGifts()) {
      Uri friendUrl = Fb.getFriendPhotoUri(reward.getGift().shareInfo[0].fbFriendId);
      Picasso.with(mContext).load(friendUrl).into((Target) helper.mFriendImage);
    }
    if (reward.hasMultipleGifts()) {
      helper.mFriendImage.setBackgroundResource(R.drawable.bg_multiple_friends);
    } else {
      helper.mFriendImage.setBackgroundDrawable(null);
    }

    helper.setGiftValue(LocaleUtils.getGiftValueString(mContext, reward));
    helper.setCreditPart(mContext.getResources(), reward.getCredit());

    return view;
  }
}
