
package com.referredlabs.kikbak.ui;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.utils.Nearest;

import java.util.ArrayList;

public class Reward2 {

  public static final String TYPE_AMOUNT = "amonut";
  public static final String TYPE_PERCENTAGE = "percentage";
  public static final String TYPE_PURCHASE = "purchase";

  private long mOfferId;
  private ArrayList<GiftType> mGifts = new ArrayList<GiftType>();
  private AvailableCreditType mCredit;
  private final ClientMerchantType mMerchant;
  private MerchantLocationType mNearestLocation;
  private float mDistance;

  Reward2(long offerId, ClientMerchantType merchant) {
    mMerchant = merchant;
    mNearestLocation = mMerchant.locations[0]; // TODO
    mDistance = 1.1f; // TODO
  }

  public void addGift(GiftType gift) {
    mGifts.add(gift);
  }

  public void addCredit(AvailableCreditType credit) {
    if (mCredit != null)
      throw new IllegalArgumentException("credit was already set, two credits for one offer ?");
    mCredit = credit;
  }
  

  public void calculateDistance(long latitude,long longitude) {
    
  }

  public static String getGiftValueString(GiftType gift) {
    if (gift == null)
      return null;
    double value = gift.value;
    String result;
    if (TYPE_PERCENTAGE.equals(gift.discountType)) {
      result = Kikbak.getInstance().getString(R.string.reward_gift_type_percentage_fmt, value);
    } else {
      result = Kikbak.getInstance().getString(R.string.reward_gift_type_amount_fmt, value);
    }
    return result;
  }

  String getGiftValueString() {
    return "";//getGiftValueString(gift);
  }

  String getCreditValueString() {
    if (mCredit == null)
      return null;
    double value = mCredit.value;
    String result = Kikbak.getInstance().getString(R.string.reward_credit_type_amount_fmt, value);
    return result;
  }

  public boolean hasFacebookImage() {
    return false;
  }

  public String getImageUrl() {
    if (mGifts.size() > 0) {
      return mGifts.get(0).imageUrl;
    } else if (mCredit != null) {
      return mCredit.imageUrl;
    }
    return null;
  }

}
