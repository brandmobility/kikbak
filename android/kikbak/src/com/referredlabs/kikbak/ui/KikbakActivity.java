
package com.referredlabs.kikbak.ui;

import android.support.v7.app.ActionBarActivity;

import com.flurry.android.FlurryAgent;
import com.referredlabs.kikbak.tasks.Task;

public class KikbakActivity extends ActionBarActivity {

  private static String KEY = "ZK8JD63MJ6CCJ62Q68GX";

  protected Task mTask;

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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    cancellTask();
  }

  protected void cancellTask() {
    if (mTask != null) {
      mTask.cancel(true);
      mTask = null;
    }
  }
}
