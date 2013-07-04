
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.referredlabs.kikbak.R;

public class RedeemSuccessFragment extends Fragment implements OnClickListener {

  private TextView mName;
  private View mSuccessFrame;
  private TextView mNote;
  private TextView mTypeLabel;
  private TextView mValue;
  private TextView mDesc;
  private TextView mCode;
  private ImageView mQrCode;
  private Button mGive;

  public static RedeemSuccessFragment newInstance() {
    RedeemSuccessFragment fragment = new RedeemSuccessFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_success, container, false);
    mSuccessFrame = root.findViewById(R.id.success_frame);
    mName = (TextView) root.findViewById(R.id.name);
    mNote = (TextView) root.findViewById(R.id.redeem_note);
    mTypeLabel = (TextView) root.findViewById(R.id.redeem_type_label);
    mValue = (TextView) root.findViewById(R.id.redeem_value);
    mDesc = (TextView) root.findViewById(R.id.redeem_desc);
    mCode = (TextView) root.findViewById(R.id.code);
    mQrCode = (ImageView) root.findViewById(R.id.qr_code);
    mGive = (Button) root.findViewById(R.id.give);
    mGive.setOnClickListener(this);
    setupViews();
    return root;
  }

  private void setupViews() {
    setupForCredit();
  }

  private void setupForGift() {
    mSuccessFrame.setBackgroundColor(getResources().getColor(R.color.success_gift_background));
    mNote.setText(R.string.redeem_success_gift_note);
    mTypeLabel.setText(R.string.redeem_success_offer);
  }

  private void setupForCredit() {
    mSuccessFrame.setBackgroundColor(getResources().getColor(R.color.success_credit_background));
    mNote.setText(R.string.redeem_success_credit_note);
    mTypeLabel.setText(R.string.redeem_success_credit);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.give:
        onGiveClicked();
        break;
    }
  }

  private void onGiveClicked() {
    Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
  }
}
