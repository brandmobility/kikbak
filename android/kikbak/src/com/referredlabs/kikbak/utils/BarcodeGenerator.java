
package com.referredlabs.kikbak.utils;

import java.util.EnumMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.referredlabs.kikbak.R;

public class BarcodeGenerator {
  public static Bitmap generateQrCode(Context ctx, String code) {

    try {
      int size = ctx.getResources().getDimensionPixelSize(R.dimen.qr_code_size);
      Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
      hints.put(EncodeHintType.MARGIN, 0);
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix result = writer.encode(code, BarcodeFormat.QR_CODE, size, size, hints);
      int width = result.getWidth();
      int height = result.getHeight();
      int[] pixels = new int[width * height];
      for (int y = 0; y < height; y++) {
        int offset = y * width;
        for (int x = 0; x < width; x++) {
          pixels[offset + x] = result.get(x, y) ? 0xFF333333 : 0xFFFFFFFF;
        }
      }
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
      return bitmap;
    } catch (Exception e) {
      Log.e("MMM", "generating failed");
    }
    return null;
  }
}
