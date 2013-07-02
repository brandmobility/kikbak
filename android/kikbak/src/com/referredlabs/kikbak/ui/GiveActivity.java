
package com.referredlabs.kikbak.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.ui.PublishFragment.ShareStatusListener;
import com.referredlabs.kikbak.ui.ShareOptionsFragment.OnShareMethodSelectedListener;

import java.io.File;
import java.io.IOException;

public class GiveActivity extends FragmentActivity implements OnClickListener,
    OnShareMethodSelectedListener,
    ShareStatusListener {

  private static final String STATE_PHOTO_URI = "photo_uri";
  private static final String STATE_CROP_URI = "crop_uri";

  private static final int REQUEST_TAKE_PHOTO = 1;
  private static final int REQUEST_CROP_PHOTO = 2;

  private ImageView mImage;
  private View mTakePhoto;
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

    findViewById(R.id.give).setOnClickListener(this);
    findViewById(R.id.terms).setOnClickListener(this);
    findViewById(R.id.learn_more).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.take_photo:
        onTakePhotoClicked();
        break;

      case R.id.give:
        onShareClicked();
        break;

      case R.id.terms:
        onTermsClicked();
        break;

      case R.id.learn_more:
        onLearnMoreClicked();
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
    mImage.post(new Runnable() {
      @Override
      public void run() {
        onPhotoTaken();
      }
    });
  }

  protected void onPhotoTaken() {
    mTakePhoto.setVisibility(View.INVISIBLE);
    mImage.setImageURI(mCroppedPhotoUri);
  }

  protected void onTermsClicked() {
    String title = getString(R.string.terms_title);
    String msg = getString(R.string.terms_example);
    NoteDialog dialog = NoteDialog.newInstance(title, msg);
    dialog.show(getFragmentManager(), null);
  }

  protected void onLearnMoreClicked() {
    String title = getString(R.string.learn_more_title);
    String msg = getString(R.string.terms_example);
    NoteDialog dialog = NoteDialog.newInstance(title, msg);
    dialog.show(getFragmentManager(), null);
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
    }
  }

  protected void onShareClicked() {
    ShareOptionsFragment dialog = ShareOptionsFragment.newInstance();
    dialog.show(getFragmentManager(), "");
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
  public void onSendViaEmail() {
    Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onSendViaFacebook() {
    Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onPostOnFacebook() {
    android.util.Log.d("MMM", "post on timeline");
    if (mCroppedPhotoUri == null) {
      Toast.makeText(this, "Take photo first!", Toast.LENGTH_LONG).show();
      return;
    }

    String path = mCroppedPhotoUri.getPath();
    String comment = mComment.getText().toString();
    PublishFragment publish = PublishFragment.newInstance(comment, path);
    publish.show(getSupportFragmentManager(), "");
  }

  @Override
  public void onShareFinished(boolean success) {
    String txt = "You have shared a gift " + (success ? "succesfully" : "unsuccesfully");
    Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    finish();
  }
}
