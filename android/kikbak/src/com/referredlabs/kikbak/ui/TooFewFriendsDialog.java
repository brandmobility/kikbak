
package com.referredlabs.kikbak.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.referredlabs.kikbak.R;

public class TooFewFriendsDialog extends DialogFragment {

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    b.setMessage(R.string.too_few_friends);
    b.setPositiveButton(R.string.ok, null);
    return b.show();
  }
}
