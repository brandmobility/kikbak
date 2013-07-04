
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.referredlabs.kikbak.R;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;

public class BarcodeScannerFragment extends DialogFragment {

  public interface OnBarcodeScanningListener {
    public void onBarcodeScanned(String barcodeResult);

    public void onScanningCancelled();
  }

  OnBarcodeScanningListener mListener;

  private Camera mCamera;
  private CameraPreview mPreviewSurface;
  private Handler mAutoFocusHandler;

  ImageScanner mScanner;

  private boolean mPreviewing = false;

  private View mMainView;

  PreviewCallback mPreviewCallback = new PreviewCallback() {
    public void onPreviewFrame(byte[] data, Camera camera) {
      Camera.Parameters parameters = camera.getParameters();
      Size size = parameters.getPreviewSize();

      Image barcode = new Image(size.width, size.height, "Y800");
      barcode.setData(data);

      int result = mScanner.scanImage(barcode);

      if (result != 0) {
        dismiss();
        releaseCamera();

        SymbolSet syms = mScanner.getResults();
        for (Symbol sym : syms) {
          // TODO: check how many and what kind of symbols may be in syms
          mListener.onBarcodeScanned("barcode result: " + sym.getData());
        }
      }
    }
  };

  // Mimic continuous auto-focusing
  AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
    public void onAutoFocus(boolean success, Camera camera) {
      mAutoFocusHandler.postDelayed(mDoAutoFocusRunnable, 1000);
    }
  };

  private Runnable mDoAutoFocusRunnable = new Runnable() {
    public void run() {
      if (mPreviewing) {
        // autoFocus should only be used when surface is created and set
        // otherwise it may fail, mPreviewing is guarding this condition
        mCamera.autoFocus(mAutoFocusCallback);
      }
    }
  };

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Fragment f = getTargetFragment();
    if (f instanceof OnBarcodeScanningListener) {
      mListener = (OnBarcodeScanningListener) f;
    } else {
      mListener = (OnBarcodeScanningListener) activity;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LayoutInflater inflater = getActivity().getLayoutInflater();
    mMainView = inflater.inflate(R.layout.barcode_scanner, null, false);

    mAutoFocusHandler = new Handler();

    /* Instance barcode scanner */
    mScanner = new ImageScanner();
    mScanner.setConfig(0, Config.X_DENSITY, 3);
    mScanner.setConfig(0, Config.Y_DENSITY, 3);

    mPreviewSurface = new CameraPreview(getActivity());
    FrameLayout previewLayout = (FrameLayout) mMainView.findViewById(R.id.cameraPreview);
    previewLayout.addView(mPreviewSurface);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
        .setTitle("Scan now")
        .setView(mMainView)
        .create();
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    mListener.onScanningCancelled();
  }

  /** A safe way to get an instance of the Camera object? */
  public static Camera getCameraInstance() {
    Camera c = null;
    try {
      c = Camera.open();
    } catch (Exception e) {
      // TODO: add error handling, when can an exception be thrown while opening a camera
    }
    return c;
  }

  /**
   * This method should only be called after Surface for displaying the preview is created.
   * 
   * @param holder A Holder of a Surface for the camera preview.
   * @throws IOException Thrown when Camera.setPreviewDisplay(holder) fails
   */
  private void startCameraPreview(SurfaceHolder holder) throws IOException {
    if (mCamera == null) {
      mCamera = getCameraInstance();
    } else {
      // stop preview before making changes
      try {
        if (mCamera != null) {
          mCamera.stopPreview();
        }
      } catch (Exception e) {
        // ignore: tried to stop a non-existent preview
      }
    }
    if(mCamera == null) {
      dismiss();
      return;
    }

    // let's set the surface for the preview
    mCamera.setPreviewDisplay(holder);

    // Hard code camera surface rotation 90 degs to match Activity view in portrait
    mCamera.setDisplayOrientation(90);

    mCamera.setPreviewCallback(mPreviewCallback);
    mCamera.startPreview();
    mPreviewing = true;

    // autoFocus() can only be used when the surface is created and set otherwise it may fail
    mCamera.autoFocus(mAutoFocusCallback);
    /*
     * // Other way to start continuous autofocus could be using camera's "continuous" // focus mode
     * but it's only available from API level >=9. // Since our application is destined also for
     * older Android versions // we'll stick here to our continuous auto focus hack above.
     * Camera.Parameters parameters = camera.getParameters(); for (String f :
     * parameters.getSupportedFocusModes()) { if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
     * mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); autoFocusCallback = null;
     * break; } }
     */
  }

  private void releaseCamera() {
    if (mCamera != null) {
      mPreviewing = false;
      mCamera.setPreviewCallback(null);
      mCamera.release();
      mCamera = null;
    }
  }

  /**
   * A SurfaceView for displaying the camera's preview. </br> This class is also responsible for
   * camera handling - camera opening, camera releasing, and registering camera listeners since it
   * knows the best when the surface is created (it implements SurfaceHolder.Callback). It is
   * important to register and unregister camera listeners in certain moments of the surface life.
   */
  private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;

    public CameraPreview(Context context) {
      super(context);
      // Install a SurfaceHolder.Callback so we get notified when the
      // underlying surface is created and destroyed.
      mHolder = getHolder();
      mHolder.addCallback(this);

      // deprecated setting, but required on Android versions prior to 3.0
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      // The Surface has been created, now tell the camera where to draw the preview.
      try {
        // start preview only after display is ready and set
        startCameraPreview(holder);
      } catch (IOException e) {
        Log.d("DBG", "Error setting camera preview: " + e.getMessage());
      }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      releaseCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      if (mHolder.getSurface() == null) {
        // preview surface does not exist
        return;
      }

      try {
        startCameraPreview(mHolder);
      } catch (Exception e) {
        Log.d("DBG", "Error starting camera preview: " + e.getMessage());
      }
    }
  }

  static {
    System.loadLibrary("iconv");
  }

}
