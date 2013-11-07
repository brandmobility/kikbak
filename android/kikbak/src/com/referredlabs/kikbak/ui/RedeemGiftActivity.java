
package com.referredlabs.kikbak.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.service.LocationFinder.LocationFinderListener;
import com.referredlabs.kikbak.ui.RedeemGiftFragment.RedeemGiftCallback;

public class RedeemGiftActivity extends KikbakActivity implements RedeemGiftCallback,
    LocationFinderListener {

  public static final String EXTRA_GIFT = "gift";
  public static final String EXTRA_SHARE_IDX = "idx";

  private LocationFinder mLocationFinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_redeem_gift);

    mLocationFinder = new LocationFinder(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mLocationFinder.startLocating();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mLocationFinder.stopLocating();
  }

  @Override
  public void onLocationUpdated(Location location) {
  }

  @Override
  public void onRedeemGiftSuccess(String barcode) {
    Intent intent = new Intent(this, SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE, barcode);
    intent.putExtra(SuccessActivity.ARG_GIFT, getIntent().getStringExtra(EXTRA_GIFT));
    startActivity(intent);
    finish();
  }

}
