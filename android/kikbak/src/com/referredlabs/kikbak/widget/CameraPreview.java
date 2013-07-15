
package com.referredlabs.kikbak.widget;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * A SurfaceView for displaying the camera's preview. </br> This class is also responsible for
 * camera managing - camera opening, camera releasing, and registering camera listeners since it
 * knows the best when the surface is created (it implements SurfaceHolder.Callback). It is
 * important to register and unregister camera listeners in certain moments of the surface life.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

  private SurfaceHolder mHolder;

  private Camera mCamera;

  /**
   * A callback to be delivered a single preview frame every time we restart the preview to deliver
   * the first previewed frame to it.
   */
  private PreviewCallback mOneShotPreviewCallback;

  public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public CameraPreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CameraPreview(Context context) {
    super(context);
    init();
  }

  private void init() {
    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);

    // deprecated setting, but required on Android versions prior to 3.0
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  // ***************************** start of surface callbacks **************************************

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

  // *********************************** end of surface callbacks ********************************

  public void stopCallbacks() {
    mHolder.removeCallback(this);
  }

  public Size getCameraPreviewSize() {
    Camera.Parameters parameters = mCamera.getParameters();
    return parameters.getPreviewSize();
  }

  public Rect getPreviewViewArea() {
    return new Rect(getLeft(), getTop(), getWidth(), getHeight());
  }

  /** A safe way to get an instance of the Camera object? */
  private static Camera getCameraInstance() {
    Camera c = null;
    try {
      c = Camera.open();
    } catch (Exception e) {
      // TODO: add error handling, when can an exception be thrown while opening a camera
    }
    return c;
  }

  public void releaseCamera() {
    if (mCamera != null) {
      mCamera.setPreviewCallback(null);
      mCamera.release();
      mCamera = null;
    }
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

    // TODO: what to do ? why it can be still null after Camera.open()?
    if (mCamera == null) {
      return;
    }

    // let's set the surface for the preview
    mCamera.setPreviewDisplay(holder);

    // TODO: read this documentation and fix the case when using front camera
    // which is flipped (example in documentation)
    // Hard code camera surface rotation 90 degs to match Activity view in portrait
    mCamera.setDisplayOrientation(90);

    mCamera.setOneShotPreviewCallback(mOneShotPreviewCallback);
    mCamera.startPreview();
  }

  public void setOneShotPreviewCallback(PreviewCallback previewCallback) {
    mOneShotPreviewCallback = previewCallback;
  }

  /**
   * Request delivering of a single preview frame to a "one shot" preview callback registered with
   * {@link CameraPreview#setOneShotPreviewCallback}.
   */
  public void deliverSinglePreviewFrame() {
    if (mOneShotPreviewCallback != null && mCamera != null) { // TODO: && mPreviewing ?
      mCamera.setOneShotPreviewCallback(mOneShotPreviewCallback);
    }
  }
}
