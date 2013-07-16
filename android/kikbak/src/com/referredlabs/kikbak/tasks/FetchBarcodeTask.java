
package com.referredlabs.kikbak.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.referredlabs.kikbak.http.Http;

public class FetchBarcodeTask extends AsyncTask<Void, Void, Void> {

  public interface OnBarcodeFetched {
    void onBarcodeFetched(Bitmap bitmap);

    void onBarcodeFetchFailed();
  }

  private OnBarcodeFetched mListener;
  private long mUserId;
  private long mGiftOrCreditId;
  private Bitmap mBitmap;

  public FetchBarcodeTask(OnBarcodeFetched listener, long userId, long giftOrCreditId) {
    mListener = listener;
    mUserId = userId;
    mGiftOrCreditId = giftOrCreditId;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      mBitmap = Http.fetchBarcode(mUserId, mGiftOrCreditId);
    } catch (Exception e) {
      Log.e("MMM", "Exception: " + e);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    if (mBitmap != null) {
      mListener.onBarcodeFetched(mBitmap);
    } else {
      mListener.onBarcodeFetchFailed();
    }
  }
}
