
package com.referredlabs.kikbak.utils;

import java.util.EnumMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.referredlabs.kikbak.R;

public class BarcodeGenerator {

  private static final String TAG = "BarcodeGenerator";

  public static Bitmap generateQrCode(Context ctx, String code) {

    try {
      int size = ctx.getResources().getDimensionPixelSize(R.dimen.qr_code_size);
      Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
      hints.put(EncodeHintType.MARGIN, 0);
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix result = writer.encode(code, BarcodeFormat.QR_CODE, size, size, hints);
      return toBitmap(result);
    } catch (Exception e) {
      Log.e(TAG, "generating qrcode failed");
    }
    return null;
  }

  public static Bitmap generateUpcaCode(Context ctx, String code) {

    try {
      int w = ctx.getResources().getDimensionPixelSize(R.dimen.barcode_upca_widht);
      int h = ctx.getResources().getDimensionPixelSize(R.dimen.barcode_upca_height);
      Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
      hints.put(EncodeHintType.MARGIN, 0);
      UPCAWriter writer = new UPCAWriter();
      BitMatrix result = writer.encode(code, BarcodeFormat.UPC_A, w, h, hints);
      return toBitmap(result);
    } catch (Exception e) {
      Log.e(TAG, "generating barcode failed");
    }
    return null;
  }

  public static Bitmap generateCode128(Context ctx, String code) {

    try {
      int w = ctx.getResources().getDimensionPixelSize(R.dimen.barcode_code128_widht);
      int h = ctx.getResources().getDimensionPixelSize(R.dimen.barcode_code128_height);
      Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
      hints.put(EncodeHintType.MARGIN, 0);
      Code128Writer writer = new Code128Writer();
      BitMatrix result = writer.encode(code, BarcodeFormat.CODE_128, w, h, hints);
      return toBitmap(result);
    } catch (Exception e) {
      Log.e(TAG, "generating barcode failed");
    }
    return null;
  }

  private static Bitmap toBitmap(BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    int[] pixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      int offset = y * width;
      for (int x = 0; x < width; x++) {
        pixels[offset + x] = matrix.get(x, y) ? 0xFF333333 : 0xFFFFFFFF;
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
  }
}
