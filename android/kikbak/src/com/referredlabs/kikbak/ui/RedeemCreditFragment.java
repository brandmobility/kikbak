
package com.referredlabs.kikbak.ui;

import android.app.Activity;
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

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.KikbakRedemptionType;
import com.referredlabs.kikbak.data.KikbakType;
import com.referredlabs.kikbak.data.RedeemKikbakRequest;
import com.referredlabs.kikbak.data.RedeemKikbakResponse;
import com.referredlabs.kikbak.data.StatusType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.ui.BarcodeScannerFragment.OnBarcodeScanningListener;
import com.referredlabs.kikbak.ui.ChangeAmountDialog.OnCreditChangedListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;
import com.referredlabs.kikbak.utils.Register;

import java.io.IOException;

public class RedeemCreditFragment extends Fragment implements OnClickListener,
    OnCreditChangedListener, ConfirmationListener, OnBarcodeScanningListener {

  private KikbakType mKikbak;
  private double mCreditToUse;
  private TextView mName;
  private TextView mRedeemCount;
  private TextView mCreditValue;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = activity.getIntent().getStringExtra(RedeemCreditActivity.EXTRA_KIKBAK);
    mKikbak = new Gson().fromJson(data, KikbakType.class);
    mCreditToUse = mKikbak.value;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_credit, container, false);

    root.findViewById(R.id.scan).setOnClickListener(this);
    root.findViewById(R.id.change).setOnClickListener(this);

    mName = (TextView) root.findViewById(R.id.name);
    mCreditValue = (TextView) root.findViewById(R.id.credit_value);
    mRedeemCount = (TextView) root.findViewById(R.id.redeem_count);

    setupViews();
    return root;
  }

  private void setupViews() {
    mName.setText(mKikbak.merchant.name);
    setCreditAmount(mKikbak.value);
    setRedeemCount(mKikbak.redeeemedGiftsCount);
  }

  private void setCreditAmount(double value) {
    String txt = getString(R.string.credit_amount_fmt, value);
    mCreditValue.setText(txt);
  }

  private void setRedeemCount(int count) {
    String txt = getResources().getQuantityString(R.plurals.credit_redeem_count, count, count);
    mRedeemCount.setText(txt);
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
    ChangeAmountDialog dialog = ChangeAmountDialog.newInstance(mKikbak.value);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), "");
  }

  @Override
  public void onCreditChanged(double value) {
    setCreditAmount(value);
    mCreditToUse = value;
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

  @Override
  public void onBarcodeScanned(String code) {
    RedeemKikbakRequest req = new RedeemKikbakRequest();
    req.kikbak = new KikbakRedemptionType();
    req.kikbak.id = mKikbak.id;
    req.kikbak.locationId = mKikbak.merchant.locations[0].locationId; //TODO: location
    req.kikbak.amount = mCreditToUse;
    req.kikbak.verificationCode = code;
    req.kikbak.verificationCode = code.substring(0, Math.min(8, code.length()));
    long userId = Register.getInstance().getUserId();
    RequestTask task = new RequestTask(userId);
    task.execute(req);
  }

  @Override
  public void onScanningCancelled() {
    // TODO Auto-generated method stub

  }

  private class RequestTask extends AsyncTask<RedeemKikbakRequest, Void, RedeemKikbakResponse> {

    private long mUserId;

    public RequestTask(long userId) {
      mUserId = userId;
    }

    @Override
    protected RedeemKikbakResponse doInBackground(RedeemKikbakRequest... params) {
      try {
        RedeemKikbakRequest req = params[0];
        String uri = Http.getUri(RedeemKikbakRequest.PATH + mUserId);
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
}
