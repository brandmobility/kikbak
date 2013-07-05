
package com.referredlabs.kikbak.utils;

import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;

public class DataUtils {

  public static final String GIFT_TYPE_AMOUNT = "amonut";
  public static final String GIFT_TYPE_PERCENTAGE = "percentage";
  public static final String GIFT_TYPE_PURCHASE = "purchase";

  public static String getRibbonGiveString(String giftType, double value) {
    if (GIFT_TYPE_PERCENTAGE.equals(giftType)) {
      return Kikbak.getInstance().getString(R.string.ribbon_give_percentage_fmt, value);
    } else if (GIFT_TYPE_AMOUNT.equals(giftType)) {
      return Kikbak.getInstance().getString(R.string.ribbon_give_amount_fmt, value);
    } else {
      return "???";
    }
  }

  public static String getRibbonGetString(double value) {
    return Kikbak.getInstance().getString(R.string.ribbon_get_fmt, value);
  }

}
