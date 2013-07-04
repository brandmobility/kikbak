
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.referredlabs.kikbak.R;

public class RedeemActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_redeem);

    //Fragment f = new RedeemCreditFragment();
    Fragment f = new RedeemGiftFragment();
    
    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    t.add(R.id.root, f);
    t.commit();
  }

}
