
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class ConfirmationDialog extends DialogFragment implements OnClickListener {

  public static final String ARG_MSG = "msg";

  public interface ConfirmationListener {
    public void onYesClick();

    public void onNoClick();
  }

  private ConfirmationListener mListener;

  public static ConfirmationDialog newInstance(String msg) {
    ConfirmationDialog dialog = new ConfirmationDialog();
    Bundle args = new Bundle();
    args.putString(ARG_MSG, msg);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Fragment f = getTargetFragment();
    if (f instanceof ConfirmationListener) {
      mListener = (ConfirmationListener) f;
    } else {
      mListener = (ConfirmationListener) activity;
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    setStyle(STYLE_NO_TITLE, 0);
    String msg = getMessage();
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(msg);
    builder.setPositiveButton(android.R.string.yes, this);
    builder.setNegativeButton(android.R.string.no, this);
    return builder.create();
  }

  protected String getMessage() {
    return getArguments().getString(ARG_MSG);
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    switch (which) {
      case DialogInterface.BUTTON_POSITIVE:
        mListener.onYesClick();
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        mListener.onNoClick();
    }
  }
}
