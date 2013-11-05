
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.referredlabs.kikbak.R;

public class SuggestSuccessDialog extends DialogFragment {

  public interface SuggestSuccessListener {
    void onSuggestSuccessDismissed();
  }

  private SuggestSuccessListener mListener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (SuggestSuccessListener) activity;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    b.setTitle(R.string.suggest_success_title);
    b.setMessage(R.string.suggest_success_note);
    b.setPositiveButton(R.string.ok, null);
    return b.create();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mListener.onSuggestSuccessDismissed();
  }
}
