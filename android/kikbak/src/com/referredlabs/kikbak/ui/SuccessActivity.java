
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.referredlabs.kikbak.R;

public class SuccessActivity extends KikbakActivity {

  public static final String ARG_GIFT = "gift";
  public static final String ARG_BARCODE = "barcode";
  public static final String ARG_CREDIT = "credit";
  public static final String ARG_CREDIT_USED = "credit_used";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_success);
    FragmentManager fm = getSupportFragmentManager();
    if (fm.findFragmentById(R.id.main) == null) {
      FragmentTransaction t = fm.beginTransaction();
      Fragment frag = getFragment();
      t.add(R.id.main, frag);
      t.commit();
    }
  }

  Fragment getFragment() {
    Bundle args = getIntent().getExtras();
    boolean isGift = args.containsKey(ARG_GIFT);
    Fragment fragment = isGift ? new RedeemGiftSuccessFragment()
        : new RedeemCreditSuccessFragment();
    fragment.setArguments(args);
    return fragment;
  }

}
