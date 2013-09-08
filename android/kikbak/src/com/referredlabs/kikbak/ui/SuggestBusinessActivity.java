
package com.referredlabs.kikbak.ui;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.referredlabs.kikbak.Kikbak;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.SuggestBusinessRequest;
import com.referredlabs.kikbak.data.SuggestBusinessResponse;
import com.referredlabs.kikbak.data.SuggestBusinessType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.tasks.Task;
import com.referredlabs.kikbak.ui.SuggestSuccessDialog.SuggestSuccessListener;
import com.referredlabs.kikbak.utils.Register;

public class SuggestBusinessActivity extends KikbakActivity implements
    OnClickListener, SuggestSuccessListener {
  private static final String STATE_PHOTO_URI = "photo_uri";
  private static final String STATE_CROP_URI = "crop_uri";

  private static final int REQUEST_TAKE_PHOTO = 1;
  private static final int REQUEST_CROP_PHOTO = 2;

  private Uri mPhotoUri;
  private Uri mCroppedPhotoUri;
  private ImageView mImage;
  private View mTakePhoto;
  private View mRetakePhoto;
  private Button mSend;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_suggest_business);

    mImage = (ImageView) findViewById(R.id.image);

    mTakePhoto = findViewById(R.id.take_photo);
    mTakePhoto.setOnClickListener(this);
    mRetakePhoto = findViewById(R.id.retake_photo);
    mRetakePhoto.setOnClickListener(this);

    mSend = (Button) findViewById(R.id.send);
    mSend.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.take_photo:
        onTakePhotoClicked();
        break;

      case R.id.retake_photo:
        onRetakePhotoClicked();
        break;

      case R.id.send:
        onSendClicked();
        break;
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
      // TODO: maybe at least display a toast to the user
      e.printStackTrace();
    }
  }

  protected void onPhotoTaken() {
    mTakePhoto.setVisibility(View.INVISIBLE);
    findViewById(R.id.take_photo_label).setVisibility(View.INVISIBLE);
    mTakePhoto.setOnClickListener(null);
    mRetakePhoto.setVisibility(View.VISIBLE);
    mImage.setImageURI(mCroppedPhotoUri);
  }

  protected void onPhotoTakenAsync() {
    mImage.post(new Runnable() {
      @Override
      public void run() {
        onPhotoTaken();
      }
    });
  }

  protected void onRetakePhotoClicked() {
    Kikbak.getInstance().removeFileAsync(mCroppedPhotoUri);
    mCroppedPhotoUri = null;
    onTakePhotoClicked();
  }

  private File getTempFile() throws IOException {
    // TODO: what if there is no sd card ?
    // need a way to grant access to a file in app space
    // getDir("temp", MODE_WORLD_WRITEABLE); this mode is depricated
    return File.createTempFile("kikbak", ".jpg", getExternalCacheDir());
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
      Kikbak.getInstance().removeFileAsync(mPhotoUri);
      mPhotoUri = null;
      onPhotoTakenAsync();
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Kikbak.getInstance().removeFileAsync(mCroppedPhotoUri);
  }

  private void onSendClicked() {
    showSuccessDialog();

    String name = ((EditText) findViewById(R.id.business_name)).getText().toString();
    String why = ((EditText) findViewById(R.id.business_greatness)).getText().toString();
    String path = mCroppedPhotoUri != null ? mCroppedPhotoUri.getPath() : null;
    new SuggestTask(name, why, path).execute();
    mCroppedPhotoUri = null;
  }

  private void showSuccessDialog() {
    new SuggestSuccessDialog().show(getSupportFragmentManager(), null);
  }

  private static class SuggestTask extends Task {

    private String mBusinessName;
    private String mWhy;
    private String mPhotoPath;
    private long mUserId;

    public SuggestTask(String name, String why, String path) {
      mUserId = Register.getInstance().getUserId();
      mBusinessName = name;
      mWhy = why;
      mPhotoPath = path;
    }

    @Override
    protected void doInBackground() throws IOException {

      String imageUrl = null;
      if (mPhotoPath != null) {
        imageUrl = Http.uploadImage(mUserId, mPhotoPath);
      }
      SuggestBusinessType business = new SuggestBusinessType();
      business.business_name = mBusinessName;
      business.why = mWhy;
      business.image_url = imageUrl;
      SuggestBusinessRequest req = new SuggestBusinessRequest();
      req.business = business;
      String uri = Http.getUri(SuggestBusinessRequest.PATH + mUserId);
      Http.execute(uri, req, SuggestBusinessResponse.class);
    }

    @Override
    protected void done() {
      if (mPhotoPath != null) {
        new File(mPhotoPath).delete();
      }
      if (!isSuccessful()) {
        Exception e = getException();
        FlurryAgent.onError(Log.E_SUGGEST_BUSINNES, e.getMessage(), Log.CLASS_NETWORK);
      }
    }
  }

  @Override
  public void onSuggestSuccessDismissed() {
    finish();
  };
}
