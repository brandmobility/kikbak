
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.referredlabs.kikbak.R;

public class RedeemChooserDialog extends DialogFragment implements OnClickListener {

  public static final String ARG_GIFT = "gift";
  public static final String ARG_CREDIT = "credit";

  public static RedeemChooserDialog newInstance(String gift, String credit) {
    RedeemChooserDialog dialog = new RedeemChooserDialog();
    Bundle args = new Bundle();
    args.putString(ARG_GIFT, gift);
    args.putString(ARG_CREDIT, credit);
    dialog.setArguments(args);
    return dialog;
  }

  public interface OnRedeemOptionSelectedListener {
    void onRedeemGiftSelected();

    void onRedeemCreditSelected();
  }

  OnRedeemOptionSelectedListener mListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, 0);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Fragment target = getTargetFragment();
    if (target instanceof OnRedeemOptionSelectedListener) {
      mListener = (OnRedeemOptionSelectedListener) target;
    } else {
      mListener = (OnRedeemOptionSelectedListener) activity;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.redeem_chooser, container, false);
    Button gift = (Button) root.findViewById(R.id.gift);
    gift.setOnClickListener(this);
    gift.setText(getArguments().getString(ARG_GIFT));
    Button credit = (Button) root.findViewById(R.id.credit);
    credit.setOnClickListener(this);
    credit.setText(getArguments().getString(ARG_CREDIT));
    return root;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.gift:
        mListener.onRedeemGiftSelected();
        break;
      case R.id.credit:
        mListener.onRedeemCreditSelected();
        break;
    }
    dismiss();
  }
}
