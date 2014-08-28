
package com.referredlabs.kikbak.ui;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.referredlabs.crop.CropActivity;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.OfferType;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.ui.ShareOptionsFragment.OnShareMethodSelectedListener;
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
  private int mImgWidth;
  private int mImgHeight;

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
    String data = getIntent().getStringExtra(ARG_OFFER);
    mOffer = new Gson().fromJson(data, ClientOfferType.class);
    setupViews();

    reportSeen();
    mComment.addTextChangedListener(new ChangeWatcher());

    ViewTreeObserver vto = mImage.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        mImgWidth = mImage.getWidth();
        mImgHeight = mImage.getHeight();
      }
    });
  }

  private void setupViews() {
    Uri uri = getUserPhotoUri();
    if (uri != null) {
      mCroppedPhotoUri = uri;
      setupViewsWithUserPhoto();
    } else {
      uri = Uri.parse(mOffer.giveImageUrl);
      Picasso.with(this).load(uri).into(mImage);
    }
    ((TextView) findViewById(R.id.name)).setText(mOffer.merchantName);

    ViewStub stub = (ViewStub) findViewById(R.id.gift_details);
    if (mOffer.offerType == OfferType.both) {
      stub.setLayoutResource(R.layout.gift_details_both);
    } else if (mOffer.offerType == OfferType.give_only) {
      stub.setLayoutResource(R.layout.gift_details_give_only);
    } else {
      stub.setLayoutResource(R.layout.gift_details_get_only);
    }
    stub.inflate();
    findViewById(R.id.terms).setOnClickListener(this);

    // FIXME:VZ
    TextView expiration = (TextView) findViewById(R.id.expiration);
    if (expiration != null) {
      String date = DateFormat.getMediumDateFormat(this).format(new Date(mOffer.endDate));
      String text = getString(R.string.offer_expires, date);
      expiration.setText(text);
    }

    // FIXME:VZ
    TextView privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
    if (privacyPolicy != null) {
      privacyPolicy.setOnClickListener(this);
    }

    if (mOffer.offerType == OfferType.both || mOffer.offerType == OfferType.give_only) {
      String give = getString(R.string.give_give, mOffer.giftDesc);
      ((TextView) findViewById(R.id.gift_desc)).setText(give);
      ((TextView) findViewById(R.id.gift_desc_opt)).setText(mOffer.giftDetailedDesc);
    }
    if (mOffer.offerType == OfferType.both || mOffer.offerType == OfferType.get_only) {
      String get = getString(R.string.give_get, mOffer.kikbakDesc);
      get = "Get a $50 prepaid\ndebit card"; // FIXME: VZ
      ((TextView) findViewById(R.id.reward_desc)).setText(get);
      ((TextView) findViewById(R.id.reward_desc_opt)).setText(mOffer.kikbakDetailedDesc);
    }
    IconBarHelper iconBar = new IconBarHelper(findViewById(R.id.icon_bar),
        new IconBarActionHandler(this));
    iconBar.setLink(mOffer.merchantUrl);
    // FIXME:VZ
    findViewById(R.id.web_icon).setVisibility(View.GONE);

    Nearest nearest = new Nearest(mOffer.merchantName, mOffer.locations);
    iconBar.setPhone(Long.toString(nearest.get().phoneNumber));
    iconBar.setLocation(nearest);
  }

  Uri getUserPhotoUri() {
    try {
      File userPhotoFile = getUserPhotoFile();
      if (userPhotoFile.exists()) {
        Uri uri = Uri.fromFile(userPhotoFile);
        return uri;
      }
    } catch (IOException e) {
      // ignore
    }
    return null;
  }

  @Override
  public boolean dispatchTouchEvent(final MotionEvent ev) {
    final View currentFocus = getCurrentFocus();
    if ((currentFocus instanceof EditText) && !isTouchInsideView(ev, currentFocus)) {
      hideSoftKeyboard();
    }
    return super.dispatchTouchEvent(ev);
  }

  private void hideSoftKeyboard() {
    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
  }

  private static int[] sLocTmp = new int[2];

  private boolean isTouchInsideView(MotionEvent ev, View view) {
    view.getLocationOnScreen(sLocTmp);
    return ev.getRawX() > sLocTmp[0] && ev.getRawY() > sLocTmp[1]
        && ev.getRawX() < (sLocTmp[0] + view.getWidth())
        && ev.getRawY() < (sLocTmp[1] + view.getHeight());
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
      case R.id.privacy_policy:
        onPrivacyPolicyClicked();
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
      file.setReadable(true, false);
      file.setWritable(true, false);
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
    setupViewsWithUserPhoto();
  }

  private void setupViewsWithUserPhoto() {
    mTakePhoto.setVisibility(View.INVISIBLE);
    findViewById(R.id.take_photo_label).setVisibility(View.INVISIBLE);
    mRetakePhoto.setVisibility(View.VISIBLE);
    mImage.setImageDrawable(null);
    mImage.setImageURI(mCroppedPhotoUri);

    FrameLayout frame = (FrameLayout) findViewById(R.id.overlay);
    Drawable overlay = getResources().getDrawable(R.drawable.grd_give_post);
    frame.setForeground(overlay);
  }

  protected void onTermsClicked() {
    String url = mOffer.tosUrl;
    TermsDialog dialog = TermsDialog.newInstance(url);
    dialog.show(getSupportFragmentManager(), null);
  }

  protected void onPrivacyPolicyClicked() {
    PrivacyDialog dialog = PrivacyDialog.newInstance();
    dialog.show(getSupportFragmentManager(), null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
      try {
        File file = getUserPhotoFile();
        mCroppedPhotoUri = Uri.fromFile(file);
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
          file.createNewFile();
          file.setReadable(true, false);
          file.setWritable(true, false);
          intent = new Intent("com.android.camera.action.CROP");
        } else {
          intent = new Intent(this, CropActivity.class);
        }

        int ox = 800;
        int oy = 800;
        if (mImgWidth > 0 && mImgHeight > 0) {
          float a = ((float) mImgWidth / (float) mImgHeight);
          if (a > 1.0f) {
            oy /= a;
          } else {
            ox /= a;
          }
        }

        intent.setDataAndType(mPhotoUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCroppedPhotoUri);
        intent.putExtra("outputX", ox);
        intent.putExtra("outputY", oy);
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
    ShareOptionsFragment dialog = ShareOptionsFragment.newInstance(mOffer);
    dialog.show(getSupportFragmentManager(), null);
  }

  private File getTempFile() throws IOException {
    return File.createTempFile("tmp-", ".jpg", getCacheDir());
  }

  private File getUserPhotoFile() throws IOException {
    File file = new File(getCacheDir(), "user-" + Long.toHexString(mOffer.id) + ".jpg");
    return file;
  }

  private static void removeFile(Uri uri) {
    if (uri != null) {
      File f = new File(uri.getPath());
      f.delete();
    }
  }

  @Override
  public void onShareVia(String method, Class<? extends DialogFragment> fragClass, Bundle extraArgs) {
    reportShared(method);
    try {
      DialogFragment fragment = fragClass.newInstance();

      Bundle args = new Bundle();
      // add extra arguments form select share dialog
      if (extraArgs != null)
        args.putAll(extraArgs);

      // add standard arguments
      args.putString(ARG_OFFER, new Gson().toJson(mOffer));

      String comment = mComment.getText().toString();
      args.putString(ShareViaBase.ARG_COMMENT, comment);

      String path = mCroppedPhotoUri == null ? null : mCroppedPhotoUri.getPath();
      args.putString(ShareViaBase.ARG_PHOTO_PATH, path);

      fragment.setArguments(args);
      fragment.setRetainInstance(true);
      fragment.show(getSupportFragmentManager(), null);
    } catch (Exception e) {
      // ignore
    }
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
    ShareSuccessDialog dialog = ShareSuccessDialog.newInstance(mOffer.offerType);
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
