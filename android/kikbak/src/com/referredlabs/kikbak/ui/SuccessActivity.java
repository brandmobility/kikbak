
package com.referredlabs.kikbak.ui;

import android.os.Bundle;

import com.referredlabs.kikbak.R;

public class SuccessActivity extends KikbakActivity {

  public static final String ARG_GIFT = "gift";
  public static final String ARG_BARCODE = "barcode";
  public static final String ARG_BARCODE_BITMAP = "barcode_bitmap";
  public static final String ARG_CREDIT = "credit";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_success);
  }
}
