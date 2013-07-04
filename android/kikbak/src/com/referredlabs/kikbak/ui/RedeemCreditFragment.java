
package com.referredlabs.kikbak.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.KikbakRedemptionType;
import com.referredlabs.kikbak.data.RedeemKikbakRequest;
import com.referredlabs.kikbak.data.RedeemKikbakResponse;
import com.referredlabs.kikbak.data.StatusType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.ui.BarcodeScannerFragment.OnBarcodeScanningListener;
import com.referredlabs.kikbak.ui.ChangeAmountDialog.OnCreditChangedListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;

import java.io.IOException;

public class RedeemCreditFragment extends Fragment implements OnClickListener,
    OnCreditChangedListener, ConfirmationListener, OnBarcodeScanningListener {

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
    BarcodeScannerFragment scanner = new BarcodeScannerFragment();
    scanner.setTargetFragment(this, 0);
    scanner.show(getFragmentManager(), "");
  }

  @Override
  public void onNoClick() {
    // do nothing
  }

  public void onRegstrationSuccess(String code) {
    Intent intent = new Intent(getActivity(), SuccessActivity.class);
    startActivity(intent);
  }

  private class RequestTask extends AsyncTask<RedeemKikbakRequest, Void, RedeemKikbakResponse> {

    @Override
    protected RedeemKikbakResponse doInBackground(RedeemKikbakRequest... params) {
      try {
        RedeemKikbakRequest req = params[0];
        String uri = Http.getUri(RedeemKikbakRequest.PATH + "1");
        RedeemKikbakResponse resp = Http.execute(uri, req, RedeemKikbakResponse.class);
        return resp;
      } catch (IOException e) {
        android.util.Log.d("MMM", "exception:" + e);
      }
      return null;
    }

    @Override
    protected void onPostExecute(RedeemKikbakResponse result) {
      if (result == null || result.status.code != StatusType.OK) {
        Toast.makeText(getActivity(), "failed to report to kikbak", Toast.LENGTH_SHORT).show();
        return;
      }

      // TODO: update balance!
      onRegstrationSuccess(result.response.authorizationCode);
    }

  }

  @Override
  public void onBarcodeScanned(String code) {
    RedeemKikbakRequest req = new RedeemKikbakRequest();
    req.kikbak = new KikbakRedemptionType();
    req.kikbak.id = 0;
    req.kikbak.locationId = 0;
    req.kikbak.amount = 20.0;
    req.kikbak.verificationCode = code;
    RequestTask task = new RequestTask();
    task.execute(req);
  }

  @Override
  public void onScanningCancelled() {
    // TODO Auto-generated method stub

  }
}
