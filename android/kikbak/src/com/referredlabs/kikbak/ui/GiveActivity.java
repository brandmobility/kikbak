
package com.referredlabs.kikbak.ui;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.ui.ShareOptionsFragment.OnShareMethodSelectedListener;
import com.referredlabs.kikbak.ui.ShareSuccessDialog.OnShareSuccessListener;
import com.referredlabs.kikbak.utils.Nearest;
import com.squareup.picasso.Picasso;

public class GiveActivity extends FragmentActivity implements OnClickListener,
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
  }

  private void setupViews() {
    Uri uri = Uri.parse(mOffer.giveImageUrl);
    Picasso.with(this).load(uri).into(mImage);
    ((TextView) findViewById(R.id.name)).setText(mOffer.merchantName);
    ((TextView) findViewById(R.id.gift_desc)).setText(mOffer.giftDesc);
    ((TextView) findViewById(R.id.gift_desc_opt)).setText(mOffer.giftDetailedDesc);
    ((TextView) findViewById(R.id.reward_desc)).setText(mOffer.kikbakDesc);
    ((TextView) findViewById(R.id.reward_desc_opt)).setText(mOffer.kikbakDetailedDesc);

    IconBarHelper iconBar = new IconBarHelper(findViewById(R.id.icon_bar),
        new IconBarActionHandler(this));
    iconBar.setLink(mOffer.merchantUrl);

    Nearest nearest = new Nearest(mOffer.locations);
    iconBar.setPhone(Long.toString(nearest.get().phoneNumber));
    iconBar.setLocation(nearest);
  }

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
    mTakePhoto.setVisibility(View.INVISIBLE);
    findViewById(R.id.take_photo_label).setVisibility(View.INVISIBLE);
    mRetakePhoto.setVisibility(View.VISIBLE);
    mImage.setImageURI(mCroppedPhotoUri);
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
    ShareOptionsFragment dialog = ShareOptionsFragment.newInstance(mOffer.merchantName);
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
  public void onSendViaEmail(String id) {
    String comment = mComment.getText().toString();
    String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
    ShareViaEmailFragment shareFrag = ShareViaEmailFragment.newInstance(mOffer, comment, path);
    shareFrag.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onSendViaSms(String id) {
    String comment = mComment.getText().toString();
    String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
    ShareViaSmsFragment shareFrag = ShareViaSmsFragment.newInstance(mOffer, comment, path);
    shareFrag.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onSendViaFacebook(String id) {
    String comment = mComment.getText().toString();
    String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
    ShareViaFacebookFragment publish = ShareViaFacebookFragment.newInstance(mOffer, comment, path,
        id);
    publish.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onShareFinished(boolean success) {
    if (success) {
      showShareSuccess();
    } else {
      Toast.makeText(this, R.string.share_failed_toast, Toast.LENGTH_LONG).show();
    }
  }

  private void showShareSuccess() {
    ShareSuccessDialog dialog = new ShareSuccessDialog();
    dialog.show(getSupportFragmentManager(), null);
  }

  @Override
  public void onShareSuccessDismissed() {
    finish();
  }
}
