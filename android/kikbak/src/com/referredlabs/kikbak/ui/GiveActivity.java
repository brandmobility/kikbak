
package com.referredlabs.kikbak.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.ui.ShareOptionsFragment3.OnShareMethodSelectedListener;
import com.referredlabs.kikbak.ui.ShareSuccessDialog.OnShareSuccessListener;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

public class GiveActivity extends KikbakActivity implements OnClickListener,
    OnShareMethodSelectedListener, ShareStatusListener, OnShareSuccessListener {

  public static final String ARG_OFFER = "offer";

  private static final String STATE_PHOTO_URI = "photo_uri";
  private static final String STATE_CROP_URI = "crop_uri";

  private static final int REQUEST_TAKE_PHOTO = 1;
  private static final int REQUEST_CROP_PHOTO = 2;

  private ClientOfferType mOffer;
  private ImageView mImage;
  private View mTakePhoto;
  private View mRetakePhoto;
  private EditText mComment;
  private Uri mPhotoUri;
  private Uri mCroppedPhotoUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_give);
    mImage = (ImageView) findViewById(R.id.image);
    mComment = (EditText) findViewById(R.id.comment);
    mTakePhoto = findViewById(R.id.take_photo);
    mTakePhoto.setOnClickListener(this);
    mRetakePhoto = findViewById(R.id.retake_photo);
    mRetakePhoto.setOnClickListener(this);

    findViewById(R.id.give).setOnClickListener(this);
    findViewById(R.id.terms).setOnClickListener(this);
    String data = getIntent().getStringExtra(ARG_OFFER);
    mOffer = new Gson().fromJson(data, ClientOfferType.class);
    setupViews();

    reportSeen();
    mComment.addTextChangedListener(new ChangeWatcher());
  }

  private void setupViews() {
    Uri uri = Uri.parse(mOffer.giveImageUrl);
    Picasso.with(this).load(uri).into(mImage);
    ((TextView) findViewById(R.id.name)).setText(mOffer.merchantName);
    String give = getString(R.string.give_give, mOffer.giftDesc);
    ((TextView) findViewById(R.id.gift_desc)).setText(give);
    ((TextView) findViewById(R.id.gift_desc_opt)).setText(mOffer.giftDetailedDesc);
    String get = getString(R.string.give_get, mOffer.kikbakDesc);
    ((TextView) findViewById(R.id.reward_desc)).setText(get);
    ((TextView) findViewById(R.id.reward_desc_opt)).setText(mOffer.kikbakDetailedDesc);

    IconBarHelper iconBar = new IconBarHelper(findViewById(R.id.icon_bar),
        new IconBarActionHandler(this));
    iconBar.setLink(mOffer.merchantUrl);

    Nearest nearest = new Nearest(mOffer.locations);
    iconBar.setPhone(Long.toString(nearest.get().phoneNumber));
    iconBar.setLocation(nearest);
  }

  // TO BEREMOVED
  // ---begin---
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem item = menu.add("spa");
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onPhotoTaken();
    mOffer.giveImageUrl = "http://m.kikbak.me/images/serenity_spa_user.png";
    Picasso.with(this).load(Uri.parse(mOffer.giveImageUrl)).into(mImage);
    return super.onOptionsItemSelected(item);
  }

  // ---end---

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.take_photo:
      case R.id.retake_photo:
        onTakePhotoClicked();
        break;

      case R.id.give:
        onShareClicked();
        break;

      case R.id.terms:
        onTermsClicked();
        break;
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mPhotoUri != null)
      outState.putParcelable(STATE_PHOTO_URI, mPhotoUri);
    if (mCroppedPhotoUri != null)
      outState.putParcelable(STATE_CROP_URI, mCroppedPhotoUri);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mPhotoUri = savedInstanceState.getParcelable(STATE_PHOTO_URI);
    mCroppedPhotoUri = savedInstanceState.getParcelable(STATE_CROP_URI);

    if (mCroppedPhotoUri != null) {
      onPhotoTaken();
    }
  }

  protected void onTakePhotoClicked() {
    try {
      File file = getTempFile();
      mPhotoUri = Uri.fromFile(file);
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
      startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void onPhotoTakenAsync() {
    mImage.postDelayed(new Runnable() {
      @Override
      public void run() {
        onPhotoTaken();
        mComment.requestFocus();
      }
    }, 500);
  }

  protected void onPhotoTaken() {
    reportPhotoTaken();
    mTakePhoto.setVisibility(View.INVISIBLE);
    findViewById(R.id.take_photo_label).setVisibility(View.INVISIBLE);
    mRetakePhoto.setVisibility(View.VISIBLE);
    mImage.setImageURI(mCroppedPhotoUri);

    FrameLayout frame = (FrameLayout) findViewById(R.id.overlay);
    Drawable overlay = getResources().getDrawable(R.drawable.grd_give_post);
    frame.setForeground(overlay);
  }

  protected void onTermsClicked() {
    String title = getString(R.string.terms_title);
    String url = mOffer.tosUrl;
    NoteDialog dialog = NoteDialog.newInstance(title, url);
    dialog.show(getSupportFragmentManager(), null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
      try {
        File file = getTempFile();
        mCroppedPhotoUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mPhotoUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCroppedPhotoUri);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }

    if (requestCode == REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
      removeFile(mPhotoUri);
      mPhotoUri = null;
      onPhotoTakenAsync();
      return;
    }

  }

  protected void onShareClicked() {
    ShareOptionsFragment3 dialog = ShareOptionsFragment3.newInstance(mOffer);
    dialog.show(getSupportFragmentManager(), null);
  }

  private File getTempFile() throws IOException {
    // TODO: what if there is no sd card ?
    // need a way to grant access to a file in app space
    return File.createTempFile("kikbak", ".jpg", getExternalCacheDir());
  }

  private static void removeFile(Uri uri) {
    if (uri != null) {
      File f = new File(uri.getPath());
      f.delete();
    }
  }

  @Override
  public void onSendViaEmail(String employee, MerchantLocationType location) {
    reportShared(Log.CONST_CHANNEL_EMAIL);
    String comment = mComment.getText().toString();
    String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
    long locationId = location == null ? -1 : location.locationId;
    ShareViaEmailFragment shareFrag = ShareViaEmailFragment.newInstance(mOffer, comment, path,
        employee, locationId);
    shareFrag.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onSendViaSms(String employee, MerchantLocationType location) {
    reportShared(Log.CONST_CHANNEL_SMS);
    String comment = mComment.getText().toString();
    String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
    long locationId = location == null ? -1 : location.locationId;
    ShareViaSmsFragment shareFrag = ShareViaSmsFragment.newInstance(mOffer, comment, path,
        employee, locationId);
    shareFrag.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onSendViaFacebook(String employee, MerchantLocationType location) {
    reportShared(Log.CONST_CHANNEL_FACEBOOK);
    String comment = mComment.getText().toString();
    String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
    long locationId = location == null ? -1 : location.locationId;
    ShareViaFacebookFragment publish = ShareViaFacebookFragment.newInstance(mOffer, comment, path,
        employee, locationId);
    publish.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onShareFinished() {
    showShareSuccess();
  }

  @Override
  public void onShareFailed() {
    Toast.makeText(this, R.string.share_failed_toast, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onShareCancelled() {
  }

  private void showShareSuccess() {
    reportShareSuccess();
    ShareSuccessDialog dialog = new ShareSuccessDialog();
    dialog.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onShareSuccessDismissed() {
    // do nothing, stay on give screen
  }

  // ANALITICS

  private void reportSeen() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mOffer.id));
    FlurryAgent.logEvent(Log.EVENT_OFFER_SEEN, map);
  }

  private void reportPhotoTaken() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mOffer.id));
    FlurryAgent.logEvent(Log.EVENT_OFFER_PHOTO_TAKEN, map);
  }

  private void reportShared(String channel) {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mOffer.id));
    map.put(Log.ARG_CHANNEL, channel);
    FlurryAgent.logEvent(Log.EVENT_OFFER_SHARED, map);
  }

  private void reportShareSuccess() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mOffer.id));
    // map.put(Log.ARG_CHANNEL, channel);
    FlurryAgent.logEvent(Log.EVENT_OFFER_SHARE_SUCCESS, map);
  }

  private void reportTextChanged() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Log.ARG_OFFER_ID, Long.toString(mOffer.id));
    FlurryAgent.logEvent(Log.EVENT_OFFER_COMMENT, map);
  }

  private class ChangeWatcher implements TextWatcher {

    private boolean mReported;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      if (!mReported) {
        reportTextChanged();
        mReported = true;
      }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
  }
}
