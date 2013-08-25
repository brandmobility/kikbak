
package com.referredlabs.kikbak.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.RewardType;
import com.referredlabs.kikbak.ui.ClaimInputFragment.ClaimCompletedCallback;
import com.referredlabs.kikbak.ui.RedeemCreditFragment.RedeemCreditCallback;

public class RedeemCreditActivity extends KikbakActivity implements RedeemCreditCallback,
    ClaimCompletedCallback {

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
    String data = args.getString(RedeemCreditActivity.EXTRA_CREDIT);
    AvailableCreditType credit = new Gson().fromJson(data, AvailableCreditType.class);
    boolean isGiftCard = RewardType.GIFT_CARD.equals(credit.rewardType);
    Fragment fragment = isGiftCard ? new RedeemGiftCardFragment() : new RedeemCreditFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onRedeemCreditSuccess(double creditUsed, String code) {
    onRedeemCreditSuccess(creditUsed, code, null);
  }

  @Override
  public void onRedeemCreditSuccess(double creditUsed, String barcode, Bitmap barcodeBitmap) {
    Intent intent = new Intent(this, SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE_BITMAP, barcodeBitmap);
    intent.putExtra(SuccessActivity.ARG_BARCODE, barcode);
    intent.putExtra(SuccessActivity.ARG_CREDIT, getIntent().getStringExtra(EXTRA_CREDIT));
    intent.putExtra(SuccessActivity.ARG_CREDIT_USED, creditUsed);
    startActivity(intent);
    finish();
  }

  @Override
  public void onClaimCompleted() {
    finish();
  }
}
