
package com.referredlabs.kikbak.ui;

import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.referredlabs.kikbak.ui.IconBarHelper.IconBarListener;
import com.referredlabs.kikbak.utils.Nearest;

public class IconBarActionHandler implements IconBarListener {

  Activity mActivity;

  IconBarActionHandler(Activity activity) {
    mActivity = activity;
  }

  @Override
  public void onMapIconClicked(Nearest nearest) {
    String uri = String.format(Locale.US, "geo:0,0?q=%1$f,%2$f", nearest.get().latitude,
        nearest.get().longitude);
    // String uri = String.format(Locale.US, "https://maps.google.com/maps?q=%1$f,%2$f",
    // loc.getLatitude(), loc.getLongitude());
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    start(intent);
  }

  @Override
  public void onPhoneIconClicked(String phone) {
    String uri = "tel:" + phone;
    // ACTION_CALL and android.permission.CALL_PHONE to call directly
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    start(intent);
  }

  @Override
  public void onWebIconClicked(String url) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    start(intent);
  }

  private void start(Intent intent) {
    try {
      mActivity.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      android.util.Log.d("MMM", "Can not start activity for:" + intent);
    }
  }

}
