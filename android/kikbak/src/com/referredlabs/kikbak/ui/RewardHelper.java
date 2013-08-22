
package com.referredlabs.kikbak.ui;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.RewardType;
import com.referredlabs.kikbak.store.TheReward;
import com.referredlabs.kikbak.ui.RewardAdapter.ItemAreaListener;

public class RewardHelper extends IconBarHelper {

  public ImageView mImage;
  public ImageView mFriendImage;
  private TextView mMerchantName;
  private TextView mGiftLabel;
  private TextView mGiftValue;
  private TextView mCreditLabel;
  private TextView mCreditValue;
  private View mSeparator;
  private TheReward mCurrentReward;
  ItemAreaListener mAreaListener;

  public RewardHelper(View root, IconBarListener listener, ItemAreaListener areaListener) {
    super(root, listener);
    mAreaListener = areaListener;

    mImage = (ImageView) root.findViewById(R.id.reward_image);
    mFriendImage = (ImageView) root.findViewById(R.id.friend_photo);
    mMerchantName = (TextView) root.findViewById(R.id.name);
    mGiftLabel = (TextView) root.findViewById(R.id.gift_label);
    mGiftValue = (TextView) root.findViewById(R.id.gift_value);
    mCreditLabel = (TextView) root.findViewById(R.id.credit_label);
    mCreditValue = (TextView) root.findViewById(R.id.credit_value);
    mSeparator = root.findViewById(R.id.separator);

    root.findViewById(R.id.gift_area).setOnClickListener(this);
    root.findViewById(R.id.credit_area).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    super.onClick(v);
    switch (v.getId()) {
      case R.id.gift_area:
        mAreaListener.onGiftAreaClicked(mCurrentReward);
        break;
      case R.id.credit_area:
        mAreaListener.onCreditAreaClicked(mCurrentReward);
        break;
    }
  }

  public void setCurrentReward(TheReward reward) {
    mCurrentReward = reward;
  }

  public void setMerchantName(String name) {
    mMerchantName.setText(name);
  }

  public void setGiftValue(String value) {
    mGiftValue.setText(value);
    setGiftVisibility(value != null);
    mFriendImage.setVisibility(value != null ? View.VISIBLE : View.INVISIBLE);
    updateSeparatorVisibility();
  }

  private void setGiftVisibility(boolean visible) {
    mGiftLabel.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    mGiftValue.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
  }

  private void setCreditVisibility(boolean visible) {
    mCreditLabel.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    mCreditValue.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
  }

  private void updateSeparatorVisibility() {
    if (mGiftLabel.getVisibility() == View.VISIBLE
        && mCreditLabel.getVisibility() == View.VISIBLE) {
      mSeparator.setVisibility(View.VISIBLE);
    } else {
      mSeparator.setVisibility(View.INVISIBLE);
    }
  }

  public void setCreditPart(Resources res, AvailableCreditType credit) {
    if (credit == null) {
      setCreditVisibility(false);
    } else {
      setCreditVisibility(true);
      if (RewardType.PURCHASE.equals(credit.rewardType)) {
        mCreditLabel.setText(R.string.reward_credit_purchase_label);
        String value = res.getString(R.string.reward_credit_purchase_amount_fmt, credit.value);
        mCreditValue.setText(value);
      } else {
        mCreditLabel.setText(R.string.reward_credit_claim_label);
        String value = res.getString(R.string.reward_credit_claim_amount_fmt, credit.value);
        mCreditValue.setText(value);
      }
    }
    updateSeparatorVisibility();
  }
}
