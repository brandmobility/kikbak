
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.tasks.Task;

public class SharingDialog extends DialogFragment {

  protected Task mTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, 0);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.sharing_progress, container, false);
    return v;
  }

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

  protected Long longOrNull(long l) {
    return l == -1 ? null : l;
  }

}
