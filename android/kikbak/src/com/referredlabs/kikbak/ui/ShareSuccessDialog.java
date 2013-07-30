
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ShareSuccessDialog extends DialogFragment {

  public interface OnShareSuccessListener {
    void onShareSuccessDismissed();
  }

  private OnShareSuccessListener mListener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnShareSuccessListener) activity;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    b.setTitle("Thank you!");
    b.setMessage("We will notify you when a friend uses your gift and you earn a reward");
    b.setPositiveButton("OK", null);
    return b.show();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mListener.onShareSuccessDismissed();
  }
}
