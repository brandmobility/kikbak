
package com.referredlabs.kikbak.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.ui.RedeemGiftFragment.RedeemGiftCallback;

public class RedeemGiftActivity extends KikbakActivity implements RedeemGiftCallback {

  public static final String EXTRA_GIFT = "gift";
  public static final String EXTRA_SHARE_IDX = "idx";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_redeem_gift);
  }

  @Override
  public void onRedeemGiftSuccess(String barcode) {
    onRedeemGiftSuccess(barcode, null);
  }

  @Override
  public void onRedeemGiftSuccess(String barcode, Bitmap barcodeBitmap) {
    Intent intent = new Intent(this, SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE_BITMAP, barcodeBitmap);
    intent.putExtra(SuccessActivity.ARG_BARCODE, barcode);
    intent.putExtra(SuccessActivity.ARG_GIFT, getIntent().getStringExtra(EXTRA_GIFT));
    startActivity(intent);
    finish();
  }
}
