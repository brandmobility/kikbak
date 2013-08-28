
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.referredlabs.kikbak.R;

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
    b.setTitle(R.string.share_thank_you);
    b.setMessage(R.string.share_success_note);
    b.setPositiveButton(R.string.ok, null);
    return b.show();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mListener.onShareSuccessDismissed();
  }
}
