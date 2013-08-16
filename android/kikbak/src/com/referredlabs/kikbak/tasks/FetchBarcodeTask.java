
package com.referredlabs.kikbak.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.referredlabs.kikbak.http.Http;

public class FetchBarcodeTask extends AsyncTask<Void, Void, Void> {

  public interface OnBarcodeFetched {
    void onBarcodeFetched(String barcode, Bitmap bitmap);

    void onBarcodeFetchFailed();
  }

  private OnBarcodeFetched mListener;
  private long mUserId;
  private long mAllocatedGiftId;
  private String mBarcode;
  private Bitmap mBitmap;

  public FetchBarcodeTask(OnBarcodeFetched listener, long userId, long allocatedGiftId) {
    mListener = listener;
    mUserId = userId;
    mAllocatedGiftId = allocatedGiftId;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      Pair<String, Bitmap> result = Http.fetchBarcode(mUserId, mAllocatedGiftId);
      mBarcode = result.first;
      mBitmap = result.second;
    } catch (Exception e) {
      Log.e("MMM", "Exception: " + e);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    if (mBitmap != null) {
      mListener.onBarcodeFetched(mBarcode, mBitmap);
    } else {
      mListener.onBarcodeFetchFailed();
    }
  }
}
