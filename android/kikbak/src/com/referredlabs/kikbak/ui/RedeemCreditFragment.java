
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.gson.Gson;
import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.CreditRedemptionType;
import com.referredlabs.kikbak.data.RedeemCreditRequest;
import com.referredlabs.kikbak.data.RedeemCreditResponse;
import com.referredlabs.kikbak.data.StatusType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.tasks.FetchBarcodeTask;
import com.referredlabs.kikbak.tasks.FetchBarcodeTask.OnBarcodeFetched;
import com.referredlabs.kikbak.ui.BarcodeScannerFragment.OnBarcodeScanningListener;
import com.referredlabs.kikbak.ui.ChangeAmountDialog.OnCreditChangedListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;
import com.referredlabs.kikbak.utils.Nearest;
import com.referredlabs.kikbak.utils.Register;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class RedeemCreditFragment extends Fragment implements OnClickListener,
    OnCreditChangedListener, ConfirmationListener, OnBarcodeScanningListener, OnBarcodeFetched {

  private static final int REQUEST_CONFIRM_CREDIT = 1;
  private static final int REQUEST_NOT_IN_STORE = 2;
  private static final int REQUEST_SCAN_CONFIRMATION = 3;

  private AvailableCreditType mCredit;
  private double mCreditToUse;
  private TextView mName;
  private ImageView mImage;
  private TextView mRedeemCount;
  private TextView mCreditValue;
  private Button mRedeemInStore;
  private boolean mCreditConfirmed;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = activity.getIntent().getStringExtra(RedeemCreditActivity.EXTRA_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);
    mCreditToUse = mCredit.value;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_credit, container, false);

    root.findViewById(R.id.change).setOnClickListener(this);

    mName = (TextView) root.findViewById(R.id.name);
    mImage = (ImageView) root.findViewById(R.id.image);
    mCreditValue = (TextView) root.findViewById(R.id.credit_value);
    mRedeemCount = (TextView) root.findViewById(R.id.redeem_count);
    mRedeemInStore = (Button) root.findViewById(R.id.redeem_store);
    mRedeemInStore.setOnClickListener(this);

    setupViews();
    return root;
  }

  private void setupViews() {
    mName.setText(mCredit.merchant.name);
    Uri uri = Uri.parse(mCredit.imageUrl);
    Picasso.with(getActivity()).load(uri).into(mImage);
    setCreditAmount(mCredit.value);
    setRedeemCount(mCredit.redeeemedGiftsCount);
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
      case R.id.redeem_store:
        onRedeemInStoreClicked();
        break;
      case R.id.change:
        onChangeAmountClicked();
        break;
    }
  }

  private void onRedeemInStoreClicked() {
    if (!mCreditConfirmed) {
      showCreditConfirmation();
      return;
    }

    if (!isInStore()) {
      showNotInStore();
      return;
    }

    if (ValidationType.QR.equals(mCredit.validationType)) {
      // scanning QR code
      showScanConfirmation();
    } else if (ValidationType.POS.equals(mCredit.validationType)) {
      // barcode generation
      mRedeemInStore.setEnabled(false);
      long userId = Register.getInstance().getUserId();
      new FetchBarcodeTask(this, userId, mCredit.id).execute();
    }
  }

  private boolean isInStore() {
    Location loc = LocationFinder.getLastLocation();
    Nearest nearest = new Nearest(mCredit.merchant.locations);
    nearest.determineNearestLocation(loc.getLatitude(), loc.getLongitude());
    return C.BYPASS_STORE_CHECK || nearest.getDistance() < C.IN_STORE_DISTANCE;
  }

  private void showNotInStore() {
    String msg = getString(R.string.redeem_not_in_store);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_NOT_IN_STORE);
    dialog.show(getFragmentManager(), null);
  }

  private void showCreditConfirmation() {
    String msg = getString(R.string.redeem_credit_confirmation);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_CONFIRM_CREDIT);
    dialog.show(getFragmentManager(), null);
  }

  private void showScanConfirmation() {
    String msg = getString(R.string.redeem_gift_confirmation);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_SCAN_CONFIRMATION);
    dialog.show(getFragmentManager(), null);
  }

  private void onChangeAmountClicked() {
    ChangeAmountDialog dialog = ChangeAmountDialog.newInstance(mCredit.value);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), "");
  }

  @Override
  public void onCreditChanged(double value) {
    setCreditAmount(value);
    mCreditToUse = value;
    mCreditConfirmed = false;
  }

  @Override
  public void onYesClick(int requestCode) {
    switch (requestCode) {
      case REQUEST_NOT_IN_STORE:
        onRedeemInStoreClicked();
        break;
      case REQUEST_CONFIRM_CREDIT:
        mCreditConfirmed = true;
        onRedeemInStoreClicked();
        break;
      case REQUEST_SCAN_CONFIRMATION:
        showScanner();
        break;
    }
  }

  @Override
  public void onNoClick(int requestCode) {
    // do nothing
  }

  private void showScanner() {
    BarcodeScannerFragment scanner = new BarcodeScannerFragment();
    scanner.setTargetFragment(this, 0);
    scanner.show(getFragmentManager(), null);
  }

  @Override
  public void onBarcodeFetched(Bitmap bitmap) {
    Intent intent = new Intent(getActivity(), SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE_BITMAP, bitmap);
    intent.putExtra(SuccessActivity.ARG_BARCODE, "4443452234"); // FIXME:
    intent.putExtra(SuccessActivity.ARG_CREDIT,
        getActivity().getIntent().getStringExtra(RedeemCreditActivity.EXTRA_CREDIT));
    startActivity(intent);
  }

  @Override
  public void onBarcodeFetchFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onBarcodeScanned(String code) {
    RedeemCreditRequest req = new RedeemCreditRequest();
    req.credit = new CreditRedemptionType();
    req.credit.id = mCredit.id;
    req.credit.locationId = mCredit.merchant.locations[0].locationId; // TODO: location
    req.credit.amount = mCreditToUse;
    req.credit.verificationCode = code;
    req.credit.verificationCode = code.substring(0, Math.min(8, code.length()));
    long userId = Register.getInstance().getUserId();
    RedeemCreditTask task = new RedeemCreditTask(userId);
    task.execute(req);
  }

  @Override
  public void onScanningCancelled() {
    // do nothing
  }

  public void onRegistrationSuccess(String code) {
    Intent intent = new Intent(getActivity(), SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE, code);
    intent.putExtra(SuccessActivity.ARG_CREDIT,
        getActivity().getIntent().getStringExtra(SuccessActivity.ARG_CREDIT));
    startActivity(intent);
  }

  public void onRegistrationFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), "failed to report to kikbak", Toast.LENGTH_SHORT).show();
  }

  private class RedeemCreditTask extends AsyncTask<RedeemCreditRequest, Void, RedeemCreditResponse> {

    private long mUserId;

    public RedeemCreditTask(long userId) {
      mUserId = userId;
    }

    @Override
    protected RedeemCreditResponse doInBackground(RedeemCreditRequest... params) {
      try {
        RedeemCreditRequest req = params[0];
        String uri = Http.getUri(RedeemCreditRequest.PATH + mUserId);
        RedeemCreditResponse resp = Http.execute(uri, req, RedeemCreditResponse.class);
        return resp;
      } catch (IOException e) {
        android.util.Log.d("MMM", "exception:" + e);
      }
      return null;
    }

    @Override
    protected void onPostExecute(RedeemCreditResponse result) {
      if (result == null || result.status.code != StatusType.OK) {
        onRegistrationFailed();
        return;
      }

      // TODO: update balance!
      onRegistrationSuccess(result.response.authorizationCode);
    }

  }

}
