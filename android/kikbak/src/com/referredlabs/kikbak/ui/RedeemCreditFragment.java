
package com.referredlabs.kikbak.ui;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
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
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.store.DataStore;
import com.referredlabs.kikbak.tasks.Task;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.ui.BarcodeScannerFragment.OnBarcodeScanningListener;
import com.referredlabs.kikbak.ui.ChangeAmountDialog.OnCreditChangedListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;
import com.referredlabs.kikbak.utils.Nearest;
import com.referredlabs.kikbak.utils.Register;
import com.squareup.picasso.Picasso;

public class RedeemCreditFragment extends Fragment implements OnClickListener,
    OnCreditChangedListener, ConfirmationListener, OnBarcodeScanningListener {

  public interface RedeemCreditCallback {
    // overlay
    void onRedeemCreditSuccess(double creditUsed, String code);

    // integrated
    void onRedeemCreditSuccess(double creditUsed, String barcode, Bitmap barcodeBitmap);
  }

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
  private Button mRedeemOnline;
  private boolean mCreditConfirmed;
  private RedeemCreditCallback mCallback;
  private Task mTask;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = activity.getIntent().getStringExtra(RedeemCreditActivity.EXTRA_CREDIT);
    mCredit = new Gson().fromJson(data, AvailableCreditType.class);
    mCreditToUse = mCredit.value;
    mCallback = (RedeemCreditCallback) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_credit, container, false);

    root.findViewById(R.id.terms).setOnClickListener(this);
    root.findViewById(R.id.change).setOnClickListener(this);

    mName = (TextView) root.findViewById(R.id.name);
    mImage = (ImageView) root.findViewById(R.id.image);
    mCreditValue = (TextView) root.findViewById(R.id.credit_value);
    mRedeemCount = (TextView) root.findViewById(R.id.redeem_count);
    mRedeemInStore = (Button) root.findViewById(R.id.redeem_store);
    mRedeemInStore.setOnClickListener(this);

    mRedeemOnline = (Button) root.findViewById(R.id.redeem_online);
    mRedeemOnline.setOnClickListener(this);

    setupViews();
    return root;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    resetTask();
  }

  private void resetTask() {
    if (mTask != null) {
      mTask.cancel(true);
      mTask = null;
    }
  }

  private void setupViews() {
    mName.setText(mCredit.merchant.name);
    Uri uri = Uri.parse(mCredit.imageUrl);
    Picasso.with(getActivity()).load(uri).into(mImage);
    setCreditAmount(mCredit.value);
    setRedeemCount(mCredit.redeemedGiftsCount);
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

      case R.id.redeem_online:
        onRedeemOnlineClicked();
        break;

      case R.id.change:
        onChangeAmountClicked();
        break;
      case R.id.terms:
        onTermsClicked();
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

    if (ValidationType.QRCODE.equals(mCredit.validationType)) {
      // scanning QR code
      showScanConfirmation();
    } else if (ValidationType.BARCODE.equals(mCredit.validationType)) {
      // barcode generation
      mRedeemInStore.setEnabled(false);
      long userId = Register.getInstance().getUserId();

      mTask = new FetchBarcodeTask(userId, mCredit.id);
      mTask.execute();
    }
  }

  private boolean isInStore() {
    Nearest nearest = new Nearest(mCredit.merchant.locations);
    return C.BYPASS_STORE_CHECK || nearest.getDistance() < C.IN_STORE_DISTANCE;
  }

  private void showNotInStore() {
    String msg = getString(R.string.redeem_not_in_store);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_NOT_IN_STORE);
    dialog.show(getFragmentManager(), null);
  }

  private void showCreditConfirmation() {
    String msg = getString(R.string.redeem_credit_confirmation, mCreditToUse);
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

  private void onRedeemOnlineClicked() {
    registerRedemption("online");
  }

  private void onChangeAmountClicked() {
    ChangeAmountDialog dialog = ChangeAmountDialog.newInstance(mCredit.value);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), null);
  }

  @Override
  public void onCreditChanged(double value) {
    setCreditAmount(value);
    mCreditToUse = value;
    mCreditConfirmed = false;
  }

  private void onTermsClicked() {
    String title = getString(R.string.terms_title);
    String url = mCredit.tosUrl;
    NoteDialog dialog = NoteDialog.newInstance(title, url);
    dialog.show(getFragmentManager(), null);
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
  public void onBarcodeScanned(String code) {
    registerRedemption(code);
  }

  private void registerRedemption(String code) {
    long userId = Register.getInstance().getUserId();
    long locationId = new Nearest(mCredit.merchant.locations).get().locationId;
    mTask = new RedeemCreditTask(userId, mCredit.id, locationId, mCreditToUse, code);
    mTask.execute();
  }

  @Override
  public void onScanningCancelled() {
    // do nothing
  }

  public void onBarcodeFetched(String barcode, Bitmap bitmap) {
    mCallback.onRedeemCreditSuccess(mCreditToUse, barcode, bitmap);
  }

  public void onBarcodeFetchFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), R.string.redeem_failure, Toast.LENGTH_SHORT).show();
  }

  public void onRegistrationSuccess(String code) {
    DataStore.getInstance().creditUsed(mCredit.id, mCreditToUse);
    mCallback.onRedeemCreditSuccess(mCreditToUse, code);
  }

  public void onRegistrationFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), R.string.redeem_failure, Toast.LENGTH_SHORT).show();
  }

  private class RedeemCreditTask extends TaskEx {

    private long mUserId;
    private RedeemCreditRequest mReq;
    private RedeemCreditResponse mResponse;

    public RedeemCreditTask(long userId, long creditId, long locationId, double amount, String code) {
      mUserId = userId;
      mReq = new RedeemCreditRequest();
      mReq.credit = new CreditRedemptionType();
      mReq.credit.id = creditId;
      mReq.credit.locationId = locationId;
      mReq.credit.amount = amount;
      mReq.credit.verificationCode = code;
    }

    @Override
    protected void doInBackground() throws IOException {
      String uri = Http.getUri(RedeemCreditRequest.PATH + mUserId);
      mResponse = Http.execute(uri, mReq, RedeemCreditResponse.class);
    }

    @Override
    protected void onSuccess() {
      onRegistrationSuccess(mResponse.response.authorizationCode);
    }

    @Override
    protected void onFailed(Exception exception) {
      onRegistrationFailed();
    }
  }

  private class FetchBarcodeTask extends TaskEx {

    private long mUserId;
    private long mAllocatedGiftId;
    private String mBarcode;
    private Bitmap mBitmap;

    public FetchBarcodeTask(long userId, long allocatedGiftId) {
      mUserId = userId;
      mAllocatedGiftId = allocatedGiftId;
    }

    @Override
    protected void doInBackground() throws IOException {
      Pair<String, Bitmap> result = Http.fetchBarcode(mUserId, mAllocatedGiftId);
      mBarcode = result.first;
      mBitmap = result.second;
    }

    @Override
    protected void onSuccess() {
      onBarcodeFetched(mBarcode, mBitmap);
    }

    @Override
    protected void onFailed(Exception exception) {
      onBarcodeFetchFailed();
    }
  }

}
