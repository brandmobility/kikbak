
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.referredlabs.kikbak.R;

public class RedeemGiftActivity extends FragmentActivity {

  public static final String EXTRA_GIFT = "gift";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_redeem_gift);
  }

}
