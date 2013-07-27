
package com.referredlabs.kikbak.utils;

import android.content.Context;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.DiscountType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.store.TheReward;

import java.util.Locale;

public class LocaleUtils {

  public static String getGiftValueString(Context ctx, GiftType gift) {
    if (gift == null)
      return null;

    if (DiscountType.PERCENTAGE.equals(gift.discountType)) {
      return ctx.getString(R.string.reward_gift_type_percentage_fmt, gift.value);
    } else if (DiscountType.AMOUNT.equals(gift.discountType)) {
      return ctx.getString(R.string.reward_gift_type_amount_fmt, gift.value);
    }
    throw new RuntimeException("new discount type?"); // TODO: remove before release
  }

  public static String getGiftValueString(Context ctx, TheReward reward) {
    if (reward == null || reward.getGifts().size() == 0)
      return null;
    GiftType gift = reward.getGifts().get(0); // all gifts are same
    return getGiftValueString(ctx, gift);
  }

  public static String getCreditValueString(Context ctx, AvailableCreditType credit) {
    if (credit == null)
      return null;
    return ctx.getString(R.string.reward_credit_type_amount_fmt, credit.value);
  }

  public static String getCreditValueString(Context ctx, TheReward reward) {
    if (reward == null || reward.getCredit() == null)
      return null;
    return getCreditValueString(ctx, reward.getCredit());
  }

  public static String getLocalizedDistance(Context context, float distance) {
    // TODO: depending on locale display either km or convert to miles
    distance = distance / 1000; // to kilometers
    distance = distance * 0.621371192f; // to miles
    String result = String.format(Locale.getDefault(), "%.2f mi", distance);
    return result;
  }

  public static String getRibbonGiveString(Context ctx, ClientOfferType offer) {
    if (DiscountType.PERCENTAGE.equals(offer.giftDiscountType)) {
      return ctx.getString(R.string.ribbon_give_percentage_fmt, offer.giftValue);
    } else if (DiscountType.AMOUNT.equals(offer.giftDiscountType)) {
      return Kikbak.getInstance().getString(R.string.ribbon_give_amount_fmt, offer.giftValue);
    }
    throw new RuntimeException("new discount type?"); // TODO: remove before release
  }

  public static String getRibbonGetString(Context mContext, ClientOfferType offer) {
    return Kikbak.getInstance().getString(R.string.ribbon_get_fmt, offer.kikbakValue);
  }

}
