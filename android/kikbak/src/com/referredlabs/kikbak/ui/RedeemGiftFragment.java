
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.GiftRedemptionType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.RedeemGiftRequest;
import com.referredlabs.kikbak.data.RedeemGiftResponse;
import com.referredlabs.kikbak.data.RedeemKikbakRequest;
import com.referredlabs.kikbak.data.RedeemKikbakResponse;
import com.referredlabs.kikbak.data.StatusType;
import com.referredlabs.kikbak.fb.Fb;
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

  private GiftType mGift;
  private TextView mName;
  private ImageView mImage;
  private ImageView mFriendPhoto;
  private TextView mFriendName;
  private TextView mFriendComment;

  private TextView mGiftValue;
  private TextView mGiftDesc;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    String data = getActivity().getIntent().getStringExtra(RedeemGiftActivity.EXTRA_GIFT);
    mGift = new Gson().fromJson(data, GiftType.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_redeem_gift, container, false);

    root.findViewById(R.id.scan).setOnClickListener(this);
    root.findViewById(R.id.terms).setOnClickListener(this);
    root.findViewById(R.id.learn_more).setOnClickListener(this);

    mName = (TextView) root.findViewById(R.id.name);
    mImage = (ImageView) root.findViewById(R.id.image);

    mFriendPhoto = (ImageView) root.findViewById(R.id.friend_photo);
    mFriendName = (TextView) root.findViewById(R.id.friend_name);
    mFriendComment = (TextView) root.findViewById(R.id.friend_comment);

    mGiftValue = (TextView) root.findViewById(R.id.gift_value);
    mGiftDesc = (TextView) root.findViewById(R.id.gift_desc);

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
    mName.setText(mGift.name);
    Uri uri = Uri.parse(mGift.merchant.imageUrl); // switch to facebook image id if present
    Picasso.with(getActivity()).load(uri).into(mImage);

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
      case R.id.scan:
        onScanClicked();
        break;
      case R.id.terms:
        onTermsClicked();
        break;
      case R.id.learn_more:
        onLearnMoreClicked();
        break;
    }
  }

  private void onScanClicked() {
    String msg = getString(R.string.redeem_gift_confirmation);
    ConfirmationDialog dialog = ConfirmationDialog.newInstance(msg);
    dialog.setTargetFragment(this, 0);
    dialog.show(getFragmentManager(), null);
  }

  private void onTermsClicked() {
    String title = getString(R.string.terms_title);
    String msg = mGift.terms;
    NoteDialog dialog = NoteDialog.newInstance(title, msg);
    dialog.show(getFragmentManager(), null);
  }

  private void onLearnMoreClicked() {
    String title = getString(R.string.learn_more_title);
    String msg = getString(R.string.terms_example);
    NoteDialog dialog = NoteDialog.newInstance(title, msg);
    dialog.show(getFragmentManager(), null);
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
    new RequestTask(userId).execute(req);
  }

  @Override
  public void onScanningCancelled() {
    // TODO Auto-generated method stub

  }

  public void onRegstrationSuccess(String code) {
    Intent intent = new Intent(getActivity(), SuccessActivity.class);
    startActivity(intent);
  }

  private class RequestTask extends AsyncTask<RedeemGiftRequest, Void, RedeemGiftResponse> {

    private long mUserId;

    public RequestTask(long userId) {
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
        Toast.makeText(getActivity(), "failed to report to kikbak", Toast.LENGTH_SHORT).show();
        return;
      }
      onRegstrationSuccess(result.authorizationCode);
    }

  }

}
