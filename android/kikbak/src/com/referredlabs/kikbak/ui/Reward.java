
package com.referredlabs.kikbak.ui;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientLocationType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;

public class Reward {

  public static final String TYPE_AMOUNT = "amonut";
  public static final String TYPE_PERCENTAGE = "percentage";
  public static final String TYPE_PURCHASE = "purchase";

  public GiftType gift;
  public AvailableCreditType credit;
  public final ClientMerchantType mMerchant;
  public ClientLocationType mNearestLocation;
  public float mDistance;

  Reward(ClientMerchantType merchant) {
    mMerchant = merchant;
    mNearestLocation = mMerchant.locations[0]; // TODO
    mDistance = 1.1f; // TODO
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
    return getGiftValueString(gift);
  }

  String getCreditValueString() {
    if (credit == null)
      return null;
    double value = credit.value;
    String result = Kikbak.getInstance().getString(R.string.reward_credit_type_amount_fmt, value);
    return result;
  }
  
  public boolean hasFacebookImage() {
    return gift != null && gift.fbImageId != 0;
  }
  
  public String getImageUrl() {
    if(gift != null) {
      return gift.imageUrl;
    } else if(credit != null) {
      return credit.imageUrl;
    }
    return null;
  }

}
