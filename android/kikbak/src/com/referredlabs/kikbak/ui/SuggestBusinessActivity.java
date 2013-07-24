package com.referredlabs.kikbak.ui;

import java.io.File;
import java.io.IOException;

import com.referredlabs.kikbak.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SuggestBusinessActivity extends Activity implements
		OnClickListener {
	private static final String STATE_PHOTO_URI = "photo_uri";
	private static final String STATE_CROP_URI = "crop_uri";

	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final int REQUEST_CROP_PHOTO = 2;

	private Uri mPhotoUri;
	private Uri mCroppedPhotoUri;
	private ImageView mImage;
	private View mTakePhoto;
	private View mRetakePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest_business);

		mImage = (ImageView) findViewById(R.id.image);
		
		mTakePhoto = findViewById(R.id.take_photo);
		mTakePhoto.setOnClickListener(this);
		mRetakePhoto = findViewById(R.id.retake_photo);
		mRetakePhoto.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image:
			onTakePhotoClicked();
			break;

		case R.id.retake_photo:
			onTakePhotoClicked();
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

	private File getTempFile() throws IOException {
		// TODO: what if there is no sd card ?
		// need a way to grant access to a file in app space
		// getDir("temp", MODE_WORLD_WRITEABLE); this mode is depricated
		return File.createTempFile("kikbak", ".jpg", getExternalCacheDir());
	}

	private static void removeFile(Uri uri) {
		if (uri != null) {
			File f = new File(uri.getPath());
			f.delete();
		}
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
}
