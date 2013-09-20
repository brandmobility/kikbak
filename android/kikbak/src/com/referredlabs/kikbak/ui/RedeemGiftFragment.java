
package com.referredlabs.kikbak.ui;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.D;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.GiftRedemptionType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.RedeemGiftRequest;
import com.referredlabs.kikbak.data.RedeemGiftResponse;
import com.referredlabs.kikbak.data.RedeemGiftStatus;
import com.referredlabs.kikbak.data.ShareInfoType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.store.DataStore;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.ui.BarcodeScannerFragment.OnBarcodeScanningListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;
import com.referredlabs.kikbak.utils.LocaleUtils;
import com.referredlabs.kikbak.utils.Nearest;
import com.referredlabs.kikbak.utils.Register;
import com.referredlabs.kikbak.utils.StatusException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class RedeemGiftFragment extends KikbakFragment implements OnClickListener,
    ConfirmationListener,
    OnBarcodeScanningListener {

  public interface RedeemGiftCallback {
    // overlay
    void onRedeemGiftSuccess(String barcode);

    // integrated
    void onRedeemGiftSuccess(String barcode, Bitmap barcodeBitmap);
  }

  private static final int REQUEST_NOT_IN_STORE = 1;
  private static final int REQUEST_SCAN_CONFIRMATION = 2;
  private static final int REQUEST_INVALID_CODE = 3;
  private GiftType mGift;
  private int mShareIdx;
  private TextView mName;
  private ImageView mImage;
  private ImageView mFriendPhoto;
  private TextView mFriendName;
  private TextView mFriendComment;

  private TextView mGiftValue;
  private TextView mGiftDesc;
  private Button mRedeemInStore;
  private Button mRedeemOnline;

  RedeemGiftCallback mCallback;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = getActivity().getIntent().getStringExtra(RedeemGiftActivity.EXTRA_GIFT);
    mGift = new Gson().fromJson(data, GiftType.class);
    mShareIdx = getActivity().getIntent().getIntExtra(RedeemGiftActivity.EXTRA_SHARE_IDX, 0);
    mCallback = (RedeemGiftCallback) activity;
    reportSeen();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_gift, container, false);

    root.findViewById(R.id.terms).setOnClickListener(this);

    mName = (TextView) root.findViewById(R.id.name);
    mImage = (ImageView) root.findViewById(R.id.image);

    mFriendPhoto = (ImageView) root.findViewById(R.id.friend_photo);
    mFriendName = (TextView) root.findViewById(R.id.friend_name);
    mFriendComment = (TextView) root.findViewById(R.id.friend_comment);

    mGiftValue = (TextView) root.findViewById(R.id.gift_value);
    mGiftDesc = (TextView) root.findViewById(R.id.gift_desc);
    mRedeemInStore = (Button) root.findViewById(R.id.redeem_store);
    mRedeemInStore.setOnClickListener(this);

    mRedeemOnline = (Button) root.findViewById(R.id.redeem_online);
    mRedeemOnline.setOnClickListener(this);

    IconBarHelper ih = new IconBarHelper(root, new IconBarActionHandler(getActivity()));
    Nearest nearest = new Nearest(mGift.merchant.locations);
    ih.setLink(mGift.merchant.url);
    ih.setLocation(nearest);
    ih.setPhone(Long.toString(nearest.get().phoneNumber));
    setupViews();

    return root;
  }

  private void setupViews() {
    ShareInfoType share = mGift.shareInfo[mShareIdx];

    mName.setText(mGift.merchant.name);
    Uri uri = Uri.parse(share.imageUrl);
    Picasso.with(getActivity()).load(uri).into(mImage);

    Uri friendUri = Fb.getFriendPhotoUri(share.fbFriendId);
    Picasso.with(getActivity()).load(friendUri).into((Target) mFriendPhoto);
    mFriendName.setText(share.friendName);
    mFriendComment.setText(share.caption);

    mGiftValue.setText(LocaleUtils.getGiftValueString(getActivity(), mGift));
    mGiftDesc.setText(mGift.detailedDesc);

    // not supported yet
    // if (RedemptionLocationType.ALL.equals(mGift.redemptionLocationType)) {
    // mRedeemOnline.setVisibility(View.VISIBLE);
    // }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.terms:
        onTermsClicked();
        break;
      case R.id.redeem_store:
        onRedeemInStoreClicked();
        break;
      case R.id.redeem_online:
        onRedeemOnlineClicked();
        break;
    }
  }

  private void onTermsClicked() {
    String title = getString(R.string.terms_title);
    String url = mGift.tosUrl;
    NoteDialog dialog = NoteDialog.newInstance(title, url);
    dialog.show(getFragmentManager(), null);
  }

  private void onRedeemInStoreClicked() {
    if (!isInStore()) {
      showNotInStore();
      return;
    }

    if (ValidationType.QRCODE.equals(mGift.validationType)) {
      // scanning QR code
      showScanConfirmation();
    } else if (ValidationType.BARCODE.equals(mGift.validationType)) {
      // barcode generation
      mRedeemInStore.setEnabled(false);
      mTask = new BarcodeTask();
      mTask.execute();
    }
  }

  private void onRedeemOnlineClicked() {
    registerRedemption("online");
  }

  private boolean isInStore() {
    Nearest nearest = new Nearest(mGift.merchant.locations);
    return D.BYPASS_STORE_CHECK || nearest.getDistance() < C.IN_STORE_DISTANCE;
  }

  private void showNotInStore() {
    String msg = getString(R.string.redeem_not_in_store);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_NOT_IN_STORE);
    dialog.show(getFragmentManager(), null);
  }

  private void showScanConfirmation() {
    String msg = getString(R.string.redeem_gift_confirmation);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_SCAN_CONFIRMATION);
    dialog.show(getFragmentManager(), null);
  }

  @Override
  public void onYesClick(int requestCode) {
    switch (requestCode) {
      case REQUEST_NOT_IN_STORE:
        onRedeemInStoreClicked();
        break;
      case REQUEST_SCAN_CONFIRMATION:
      case REQUEST_INVALID_CODE:
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
    mRedeemInStore.setEnabled(false);
  }

  @Override
  public void onBarcodeScanned(String code) {
    registerRedemption(code);
  }

  private void registerRedemption(String code) {
    mTask = new RedeemTask(code);
    mTask.execute();
  }

  @Override
  public void onScanningCancelled() {
    mRedeemInStore.setEnabled(true);
  }

  public void onBarcodeFetched(String barcode, Bitmap bitmap) {
    mCallback.onRedeemGiftSuccess(barcode, bitmap);
  }

  public void onBarcodeFetchFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), R.string.redeem_failure, Toast.LENGTH_SHORT).show();
  }

  public void onRegistrationSuccess(String code) {
    if (ValidationType.QRCODE.equals(mGift.validationType)) {
      DataStore.getInstance().giftUsed(mGift.offerId);
    }
    mCallback.onRedeemGiftSuccess(code);
  }

  public void onRegistrationFailed(Exception exception) {
    mRedeemInStore.setEnabled(true);

    if (exception instanceof StatusException) {
      StatusException e = (StatusException) exception;
      RedeemGiftStatus status = e.getStatus();
      if (status == RedeemGiftStatus.INVALID_CODE) {
        showInvalidCodeDialog();
        return;
      }
      if (status == RedeemGiftStatus.LIMIT_REACH) {
        // TODO: implement me
        return;
      }
    }

    // default
    Toast.makeText(getActivity(), R.string.redeem_failure, Toast.LENGTH_SHORT).show();
  }

  private void showInvalidCodeDialog() {
    String msg = getString(R.string.redeem_invalid_code);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, REQUEST_INVALID_CODE);
    dialog.show(getFragmentManager(), null);
  }

  private void reportSeen() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mGift.offerId));
    float distance = new Nearest(mGift.merchant.locations).getDistance();
    map.put(Log.ARG_DISTANCE, Float.toString(distance));
    FlurryAgent.logEvent(Log.EVENT_GIFT_SEEN, map);
  }

  private class RedeemTask extends TaskEx {

    private long mUserId;
    private RedeemGiftRequest mReq;
    private String mAuthorizationCode;

    public RedeemTask(String code) {
      mUserId = Register.getInstance().getUserId();

      mReq = new RedeemGiftRequest();
      mReq.gift = new GiftRedemptionType();
      mReq.gift.id = mGift.shareInfo[mShareIdx].allocatedGiftId;
      mReq.gift.friendUserId = mGift.shareInfo[mShareIdx].friendUserId;
      mReq.gift.locationId = mGift.merchant.locations[0].locationId;
      mReq.gift.verificationCode = code;
    }

    @Override
    protected void doInBackground() throws IOException, StatusException {
      String uri = Http.getUri(RedeemGiftRequest.PATH + mUserId);
      RedeemGiftResponse resp = Http.execute(uri, mReq, RedeemGiftResponse.class);
      if (resp.status != RedeemGiftStatus.OK) {
        throw new StatusException(resp.status);
      }

      mAuthorizationCode = resp.authorizationCode;
    }

    @Override
    protected void onSuccess() {
      onRegistrationSuccess(mAuthorizationCode);
    }

    @Override
    protected void onFailed(Exception exception) {
      onRegistrationFailed(exception);
    }
  }

  private class BarcodeTask extends TaskEx {

    private long mUserId;
    private long mAllocatedGiftId;
    private String mBarcode;
    private Bitmap mBitmap;

    BarcodeTask() {
      mUserId = Register.getInstance().getUserId();
      mAllocatedGiftId = mGift.shareInfo[mShareIdx].allocatedGiftId;
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
