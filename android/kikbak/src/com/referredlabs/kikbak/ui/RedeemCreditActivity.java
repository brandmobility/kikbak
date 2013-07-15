
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.referredlabs.kikbak.R;

public class RedeemCreditActivity extends FragmentActivity {

  public static final String EXTRA_CREDIT = "credit";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_redeem_credit);
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
    boolean isGiftCard = true;
    Fragment fragment = isGiftCard ? new RedeemGiftCardFragment() : new RedeemCreditFragment();
    fragment.setArguments(args);
    return fragment;
  }
}
