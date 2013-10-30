
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.tasks.Task;

public class ShareViaBase extends DialogFragment {
  protected static final String ARG_OFFER = "offer";
  protected static final String ARG_COMMENT = "comment";
  protected static final String ARG_PHOTO_PATH = "photo_path";
  protected static final String ARG_EMPLYOYEE = "emplyee";
  protected static final String ARG_LOCATION_ID = "location_id";

  protected Task mTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, 0);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setCanceledOnTouchOutside(false);
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
}
