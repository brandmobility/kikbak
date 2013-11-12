
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.OfferType;

public class ShareSuccessDialog extends DialogFragment {

  private static final String ARG_TYPE = "type";

  public interface OnShareSuccessListener {
    void onShareSuccessDismissed();
  }

  public static ShareSuccessDialog newInstance(OfferType offerType) {
    Bundle args = new Bundle();
    args.putInt(ARG_TYPE, offerType.ordinal());

    ShareSuccessDialog dialog = new ShareSuccessDialog();
    dialog.setArguments(args);
    return dialog;
  }

  private OnShareSuccessListener mListener;
  private OfferType mOfferType;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOfferType = OfferType.values()[getArguments().getInt(ARG_TYPE)];
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnShareSuccessListener) activity;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    if (mOfferType == OfferType.give_only) {
      b.setTitle(R.string.share_thank_you_give_only);
      b.setMessage(R.string.share_success_note_give_only);
    } else {
      b.setTitle(R.string.share_thank_you);
      b.setMessage(R.string.share_success_note);
    }
    b.setPositiveButton(R.string.ok, null);
    return b.create();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mListener.onShareSuccessDismissed();
  }
}
