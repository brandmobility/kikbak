
package com.referredlabs.kikbak.ui;

import android.support.v4.app.Fragment;

import com.referredlabs.kikbak.tasks.Task;

public class KikbakFragment extends Fragment {

  protected Task mTask;

  @Override
  public void onDestroy() {
    super.onDestroy();
    resetTask();
  }

  protected void resetTask() {
    if (mTask != null) {
      mTask.cancel(true);
      mTask = null;
    }
  }
}
