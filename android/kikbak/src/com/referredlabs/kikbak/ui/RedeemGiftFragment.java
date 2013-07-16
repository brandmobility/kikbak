
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.referredlabs.kikbak.data.GiftRedemptionType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.RedeemGiftRequest;
import com.referredlabs.kikbak.data.RedeemGiftResponse;
import com.referredlabs.kikbak.data.StatusType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.fb.PicassoFb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.ui.BarcodeScannerFragment.OnBarcodeScanningListener;
import com.referredlabs.kikbak.ui.ConfirmationDialog.ConfirmationListener;
import com.referredlabs.kikbak.utils.Nearest;
import com.referredlabs.kikbak.utils.Register;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class RedeemGiftFragment extends Fragment implements OnClickListener, ConfirmationListener,
    OnBarcodeScanningListener {

  private static final int REQUEST_NOT_IN_STORE = 1;
  private static final int REQUEST_SCAN_CONFIRMATION = 2;

  private GiftType mGift;
  private TextView mName;
  private ImageView mImage;
  private ImageView mFriendPhoto;
  private TextView mFriendName;
  private TextView mFriendComment;

  private TextView mGiftValue;
  private TextView mGiftDesc;
  private Button mRedeemInStore;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = getActivity().getIntent().getStringExtra(RedeemGiftActivity.EXTRA_GIFT);
    mGift = new Gson().fromJson(data, GiftType.class);
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

    Location loc = LocationFinder.getLastLocation();

    IconBarHelper ih = new IconBarHelper(root, new IconBarActionHandler(getActivity()));
    Nearest location = new Nearest(mGift.merchant.locations);
    location.determineNearestLocation(loc.getLatitude(), loc.getLongitude());
    ih.setLink(mGift.merchant.url);
    ih.setLocation(location);
    ih.setPhone(Long.toString(location.getPhoneNumber()));
    setupViews();

    return root;
  }

  private void setupViews() {
    mName.setText(mGift.merchant.name);
    if (mGift.fbImageId > 0) {
      Uri uri = Uri.parse("fb://image/" + mGift.fbImageId);
      PicassoFb.with(getActivity()).load(uri).into(mImage);
    } else {
      Uri uri = Uri.parse(mGift.imageUrl);
      Picasso.with(getActivity()).load(uri).into(mImage);
    }

    Uri friendUri = Fb.getFriendPhotoUri(mGift.fbFriendId);
    Picasso.with(getActivity()).load(friendUri).into(mFriendPhoto);
    mFriendName.setText(mGift.friendName);
    mFriendComment.setText(mGift.caption);

    mGiftValue.setText(Reward.getGiftValueString(mGift));
    mGiftDesc.setText(mGift.desc);

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

    if (ValidationType.QR.equals(mGift.validationType)) {
      // scanning QR code
      showScanConfirmation();
    } else if (ValidationType.POS.equals(mGift.validationType)) {
      // barcode generation
      mRedeemInStore.setEnabled(false);
      new BarcodeTask().execute();
    }
  }

  private boolean isInStore() {
    Location loc = LocationFinder.getLastLocation();
    Nearest nearest = new Nearest(mGift.merchant.locations);
    nearest.determineNearestLocation(loc.getLatitude(), loc.getLongitude());
    return C.BYPASS_STORE_CHECK || nearest.getDistance() < C.IN_STORE_DISTANCE;
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
    RedeemGiftRequest req = new RedeemGiftRequest();
    req.gift = new GiftRedemptionType();
    req.gift.id = mGift.id;
    req.gift.friendUserId = mGift.friendUserId;
    req.gift.locationId = mGift.merchant.locations[0].locationId;
    req.gift.verificationCode = code;
    req.gift.verificationCode = code.substring(0, Math.min(8, code.length()));
    long userId = Register.getInstance().getUserId();
    new RedeemTask(userId).execute(req);
  }

  @Override
  public void onScanningCancelled() {
    // do nothing
    mRedeemInStore.setEnabled(true);
  }

  public void onBarcodeFetched(Bitmap bitmap) {
    Intent intent = new Intent(getActivity(), SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE_BITMAP, bitmap);
    intent.putExtra(SuccessActivity.ARG_BARCODE, "4443452234"); // FIXME:
    intent.putExtra(SuccessActivity.ARG_GIFT,
        getActivity().getIntent().getStringExtra(RedeemGiftActivity.EXTRA_GIFT));
    startActivity(intent);
  }

  public void onBarcodeFetchFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
  }

  public void onRegistrationSuccess(String code) {
    Intent intent = new Intent(getActivity(), SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_GIFT,
        getActivity().getIntent().getStringExtra(RedeemGiftActivity.EXTRA_GIFT));
    intent.putExtra(SuccessActivity.ARG_BARCODE, code);
    startActivity(intent);
  }

  public void onRegistrationFailed() {
    mRedeemInStore.setEnabled(true);
    Toast.makeText(getActivity(), "failed to report to kikbak", Toast.LENGTH_SHORT).show();
  }

  private class RedeemTask extends AsyncTask<RedeemGiftRequest, Void, RedeemGiftResponse> {

    private long mUserId;

    public RedeemTask(long userId) {
      mUserId = userId;
    }

    @Override
    protected RedeemGiftResponse doInBackground(RedeemGiftRequest... params) {
      try {
        RedeemGiftRequest req = params[0];
        String uri = Http.getUri(RedeemGiftRequest.PATH + mUserId);
        RedeemGiftResponse resp = Http.execute(uri, req, RedeemGiftResponse.class);
        return resp;
      } catch (IOException e) {
        android.util.Log.d("MMM", "exception:" + e);
      }
      return null;
    }

    @Override
    protected void onPostExecute(RedeemGiftResponse result) {
      if (result == null || result.status.code != StatusType.OK) {
        onRegistrationFailed();
        return;
      }
      onRegistrationSuccess(result.authorizationCode);
    }

  }

  private class BarcodeTask extends AsyncTask<Void, Void, Void> {

    private long mUserId;
    private long mGiftId;
    private Bitmap mBitmap;

    @Override
    protected void onPreExecute() {
      mUserId = Register.getInstance().getUserId();
      mGiftId = mGift.id;
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        mBitmap = Http.fetchBarcode(mUserId, mGiftId);
      } catch (Exception e) {
        Log.e("MMM", "Exception: " + e);
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (mBitmap != null) {
        onBarcodeFetched(mBitmap);
      } else {
        onBarcodeFetchFailed();
      }
    }
  }

}
