
package com.referredlabs.kikbak.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.referredlabs.kikbak.R;

public class NoteDialog extends DialogFragment {

  private static final String ARG_TITLE = "title";
  private static final String ARG_MSG = "msg";

  public static NoteDialog newInstance(String title, String message) {
    NoteDialog dialog = new NoteDialog();
    Bundle args = new Bundle();
    args.putString(ARG_TITLE, title);
    args.putString(ARG_MSG, message);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    String title = getArguments().getString(ARG_TITLE);
    String msg = getArguments().getString(ARG_MSG);
    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    b.setTitle(title);
    b.setMessage(msg);
    b.setPositiveButton(R.string.done, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    return b.create();
  }

}
