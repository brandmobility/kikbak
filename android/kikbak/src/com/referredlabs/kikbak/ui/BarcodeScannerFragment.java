
package com.referredlabs.kikbak.ui;

import java.util.EnumMap;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.widget.CameraPreview;
import com.referredlabs.kikbak.widget.PointsView;

public class BarcodeScannerFragment extends DialogFragment implements ResultPointCallback {
  // Container Activity must implement this interface
  public interface OnBarcodeScanningListener {
    public void onBarcodeScanned(String barcodeResult);

    public void onScanningCancelled();
  }

  OnBarcodeScanningListener mListener;

  private CameraPreview mPreviewSurface;
  private PointsView mPointsView;

  DecodeBarcodeTask mDecodeBarcodeTask = new DecodeBarcodeTask();

  private QRCodeReader mQrCodeReader = new QRCodeReader();

  /**
   * This PreviewCallback is always registered as a "single frame" preview callback. It is thread
   * safe to start AsyncTask here every time we get the frame because the started AsyncTask requests
   * new "single frame" to process only after the first processing is done.
   */
  PreviewCallback mPreviewCallback = new PreviewCallback() {
    public void onPreviewFrame(byte[] data, Camera camera) {
      DecodeBarcodeTask decodeBarcodeTask = new DecodeBarcodeTask();
      decodeBarcodeTask.execute(data);
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, 0);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Fragment target = getTargetFragment();
    if (target instanceof OnBarcodeScanningListener) {
      mListener = (OnBarcodeScanningListener) target;
    } else {
      mListener = (OnBarcodeScanningListener) activity;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setStyle(STYLE_NO_TITLE, 0);
    View root = inflater.inflate(R.layout.barcode_scanner, null, false);

    mPreviewSurface = (CameraPreview) root.findViewById(R.id.camera_preview);
    mPreviewSurface.setOneShotPreviewCallback(mPreviewCallback);

    mPointsView = (PointsView) root.findViewById(R.id.points_view);
    mPointsView.setCameraPreview(mPreviewSurface);

    if (Build.PRODUCT.contains("sdk")) {
      // running on emulator
      root.postDelayed(new Runnable() {
        @Override
        public void run() {
          mListener.onBarcodeScanned("bug");
        }
      }, 2000);
    }

    return root;
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    mDecodeBarcodeTask.cancel(false);

    super.onCancel(dialog);
    mListener.onScanningCancelled();
  }

  /**
   * Contains code from client.android.CaptureActivity.handleDecodeInternally()
   * 
   * @param rawResult
   */
  private void finishBarcodeScanning(Result rawResult) {
    dismiss();
    // Stop the surface callbacks so it never gets callbacks like surfaceCreated()
    // or surfaceChanged() again - they could break releaseCamera()
    // that we've just called above and/or it can force another onPreviewFrame().
    mPreviewSurface.stopCallbacks();
    mPreviewSurface.releaseCamera();

    String contents = rawResult.getText();
    contents.replace("\r", "");

    mListener.onBarcodeScanned(contents);
  }

  /**
   * Searches for a 2D barcode (QR code type) in the given as byte array camera frame. If it finds
   * one it decodes the hidden message in it using com.google.zxing.qrcode.QRCodeReader and delivers
   * the decoding Result on UI thread. If the searched barcode was not found in the camera frame it
   * orders another camera frame for the mPreviewCallback.
   */
  class DecodeBarcodeTask extends AsyncTask<byte[], Void, Result> {

    @Override
    protected Result doInBackground(byte[]... params) {
      return decodePreviewFrame(params[0]);
    }

    @Override
    protected void onPostExecute(Result result) {
      super.onPostExecute(result);
      if (result != null) {
        finishBarcodeScanning(result);
      } else {
        // request another frame from camera to process
        mPreviewSurface.deliverSinglePreviewFrame();
      }
    }

    /**
     * Contains code from com.google.zxing.client.android.DecodeHandler.decode()
     * 
     * @param frameData
     */
    private Result decodePreviewFrame(byte[] frameData) {
      Result rawResult = null;

      // get default camera's preview size
      Size size = mPreviewSurface.getCameraPreviewSize();

      PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(frameData,
          size.width, size.height,
          mPreviewSurface.getLeft(), mPreviewSurface.getTop(),
          size.width, size.height, false);

      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

      Map<DecodeHintType, Object> hints =
          new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
      hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, BarcodeScannerFragment.this);
      try {
        // rawResult will never be null if it couldn't be decoded an exception is thrown
        rawResult = mQrCodeReader.decode(bitmap, hints);
        return rawResult;
      } catch (ReaderException re) {
        // couldn't decode this byte array - continue
      }

      mQrCodeReader.reset();
      return null;
    }
  }

  @Override
  public void foundPossibleResultPoint(ResultPoint point) {
    mPointsView.addPossibleResultPoint(point);
  }
}
