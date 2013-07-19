
package com.referredlabs.kikbak.ui;

import android.graphics.Bitmap;

public interface RedeemSuccessCallback {
  void success(String barcode, Bitmap barcodeBitmap);

  void finished();
}
