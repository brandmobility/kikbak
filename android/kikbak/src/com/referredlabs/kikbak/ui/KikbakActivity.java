
package com.referredlabs.kikbak.ui;

import com.flurry.android.FlurryAgent;

import android.support.v7.app.ActionBarActivity;

public class KikbakActivity extends ActionBarActivity {

  private static String KEY = "ZK8JD63MJ6CCJ62Q68GX";

  @Override
  protected void onStart() {
    super.onStart();
    FlurryAgent.onStartSession(this, KEY);
  }

  @Override
  protected void onStop() {
    super.onStop();
    FlurryAgent.onEndSession(this);
  }
}
