
package com.referredlabs.kikbak.utils;

import java.util.Locale;

import android.content.Context;

import com.referredlabs.kikbak.BuildConfig;
import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.DiscountType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.RewardType;
import com.referredlabs.kikbak.store.TheReward;

public class LocaleUtils {

  public static String getGiftValueString(Context ctx, GiftType gift) {
    if (gift == null)
      return null;

    if (gift.discountType == DiscountType.percentage) {
      return ctx.getString(R.string.reward_gift_type_percentage_fmt, gift.value);
    } else if (gift.discountType == DiscountType.amount) {
      return ctx.getString(R.string.reward_gift_type_amount_fmt, gift.value);
    }
    return null;
  }

  public static String getGiftValueString(Context ctx, TheReward reward) {
    if (reward == null)
      return null;
    return getGiftValueString(ctx, reward.getGift());
  }

  public static String getCreditValueString(Context ctx, AvailableCreditType credit) {
    if (credit == null)
      return null;
    if (credit.rewardType == RewardType.purchase) {
      return ctx.getString(R.string.reward_credit_purchase_amount_fmt, credit.value);
    } else {
      return ctx.getString(R.string.reward_credit_claim_amount_fmt, credit.value);
    }
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
    String format = distance > 9.95f ? "%.0f mi" : "%.1f mi";
    String result = String.format(Locale.getDefault(), format, distance);
    return result;
  }

  public static String getRibbonGiveString(Context ctx, ClientOfferType offer) {
    if (offer.giftDiscountType == DiscountType.percentage) {
      return ctx.getString(R.string.ribbon_give_percentage_fmt, offer.giftValue);
    } else if (offer.giftDiscountType == DiscountType.amount) {
      return Kikbak.getInstance().getString(R.string.ribbon_give_amount_fmt, offer.giftValue);
    }
    if (BuildConfig.DEBUG) {
      throw new RuntimeException("new discount type?");
    }
    return "?";
  }

  public static String getRibbonGetString(Context mContext, ClientOfferType offer) {
    return Kikbak.getInstance().getString(R.string.ribbon_get_fmt, offer.kikbakValue);
  }

}
