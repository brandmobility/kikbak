
package com.referredlabs.kikbak.ui;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientLocationType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.KikbakType;

public class Reward {

  public static final String TYPE_AMOUNT = "amonut";
  public static final String TYPE_PERCENTAGE = "percentage";
  public static final String TYPE_PURCHASE = "purchase";

  public GiftType gift;
  public KikbakType kikbak;
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
    if (TYPE_PERCENTAGE.equals(gift.type)) {
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
    if (kikbak == null)
      return null;
    double value = kikbak.value;
    String result = Kikbak.getInstance().getString(R.string.reward_credit_type_amount_fmt, value);
    return result;
  }

}
