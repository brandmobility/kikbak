
package com.referredlabs.kikbak.utils;

import android.content.Context;

import java.util.Locale;

public class Distance {

  public static String getLocalizedDistance(Context context, float distance) {
    // TODO: depending on locale display either km or convert to miles
    distance = distance / 1000; // to kilometers
    distance = distance * 0.621371192f; // to miles
    String result = String.format(Locale.getDefault(), "%.2f mi", distance);
    return result;
  }
}
