
package com.referredlabs.kikbak.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.ui.ChangeAmountDialog.OnCreditChangedListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;

public class RedeemCreditFragment extends Fragment implements OnClickListener,
    OnCreditChangedListener, ConfirmationListener {

  private TextView mRedeemCount;
  private TextView mCreditValue;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_credit, container, false);

    root.findViewById(R.id.scan).setOnClickListener(this);
    root.findViewById(R.id.change).setOnClickListener(this);

    mCreditValue = (TextView) root.findViewById(R.id.credit_value);
    mRedeemCount = (TextView) root.findViewById(R.id.redeem_count);
    return root;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.scan:
        onScanClicked();
        break;
      case R.id.change:
        onChangeAmountClicked();
        break;
    }
  }

  private void onScanClicked() {
    String msg = getString(R.string.redeem_credit_confirmation);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), null);
  }

  private void onChangeAmountClicked() {
    ChangeAmountDialog dialog = ChangeAmountDialog.newInstance(45.00);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), "");
  }

  @Override
  public void onCreditChanged(double value) {
    setCreditAmount(value);
  }

  private void setCreditAmount(double value) {
    String txt = getString(R.string.credit_amount_fmt, value);
    mCreditValue.setText(txt);
  }

  @Override
  public void onYesClick() {
    Toast.makeText(getActivity(), "scan not implemented", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onNoClick() {
    // do nothing
  }
}
