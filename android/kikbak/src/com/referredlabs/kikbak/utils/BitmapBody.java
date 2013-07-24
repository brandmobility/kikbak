
package com.referredlabs.kikbak.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

import java.io.IOException;
import java.io.OutputStream;

public class BitmapBody extends AbstractContentBody {

  private Bitmap mBitmap;

  public BitmapBody(Bitmap bitmap) {
    super("image/jpeg");
    mBitmap = bitmap;
  }

  @Override
  public String getFilename() {
    return "image.png";
  }

  @Override
  public void writeTo(OutputStream out) throws IOException {
    mBitmap.compress(CompressFormat.JPEG, 90, out);
  }

  @Override
  public String getCharset() {
    return null;
  }

  @Override
  public long getContentLength() {
    return -1;
  }

  @Override
  public String getTransferEncoding() {
    return MIME.ENC_BINARY;
  }

}
