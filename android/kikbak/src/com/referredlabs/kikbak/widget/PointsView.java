
package com.referredlabs.kikbak.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.referredlabs.kikbak.R;

import java.util.ArrayList;
import java.util.List;

public class PointsView extends View {
  private static final long ANIMATION_DELAY = 80L;
  private static final int CURRENT_POINT_OPACITY = 0xA0;
  private static final int MAX_RESULT_POINTS = 20;
  private static final int POINT_SIZE = 6;

  private CameraPreview mCameraPreview;
  private final Paint mPaint;
  private final int mResultPointColor;
  private List<ResultPoint> mPossibleResultPoints;
  private List<ResultPoint> mLastPossibleResultPoints;

  /**
   * Holds a reference to a float array that symbolizes a point detected in the camera preview that
   * is currently being drawn on top of the camera preview in this View's onDraw().
   */
  private float[] mPointToDraw = new float[2];

  // This constructor is used when the class is built from an XML resource.
  public PointsView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every
    // time in onDraw().
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Resources resources = getResources();
    mResultPointColor = resources.getColor(R.color.scanner_point);
    mPossibleResultPoints = new ArrayList<ResultPoint>(5);
    mLastPossibleResultPoints = new ArrayList<ResultPoint>(5);
  }

  public void setCameraPreview(CameraPreview cameraPreview) {
    mCameraPreview = cameraPreview;
  }

  @Override
  public void onDraw(Canvas canvas) {
    if (mCameraPreview == null) {
      return; // not ready yet, early draw before done configuring
    }

    Rect frame = mCameraPreview.getPreviewViewArea();

    float w = mCameraPreview.getCameraPreviewSize().width;
    float h = mCameraPreview.getCameraPreviewSize().height;
    float newW = getWidth();
    float newH = getHeight();

    List<ResultPoint> currentPoints = mPossibleResultPoints;
    List<ResultPoint> lastPoints = mLastPossibleResultPoints;

    // let's draw big points
    mPaint.setAlpha(CURRENT_POINT_OPACITY);
    mPaint.setColor(mResultPointColor);
    for (ResultPoint point : currentPoints) {
      scalePointFromCamera(point, w, h, newW, newH, mPointToDraw);
      canvas.drawCircle(mPointToDraw[0], mPointToDraw[1], POINT_SIZE, mPaint);
    }

    // now let's draw small points that were drawn as big in the last onDraw() call
    mPaint.setAlpha(CURRENT_POINT_OPACITY / 2);
    mPaint.setColor(mResultPointColor);
    float radius = POINT_SIZE / 2.0f;
    for (ResultPoint point : lastPoints) {
      scalePointFromCamera(point, w, h, newW, newH, mPointToDraw);
      canvas.drawCircle(mPointToDraw[0], mPointToDraw[1], radius, mPaint);
    }

    mPossibleResultPoints = lastPoints;
    mLastPossibleResultPoints = currentPoints;

    mPossibleResultPoints.clear();

    // Request another update at the animation interval
    postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE,
        frame.top - POINT_SIZE, frame.right + POINT_SIZE, frame.bottom
            + POINT_SIZE);
  }

  /**
   * Scales a point detected in the camera preview - pointFromCamera - to be drawn properly in this
   * View (which is a layer on top of the camera preview View) considering the fact that camera
   * preview is turned 90 degrees clockwise, and that camera's resolution is different than the
   * camera preview View's size.
   * 
   * @param pointFromCamera A point detected in the original camera preview (before turning or
   *          resizing the preview's view surface).
   * @param cameraPreviewWidth Original camera preview width, taken from the camera's resolution.
   * @param cameraPreviewHeight Original camera preview height, taken from the camera's resolution.
   * @param previewViewWidth Camera preview View's width (the surface that the user sees and on top
   *          of which the points are drawn).
   * @param previewViewHeight Camera preview View's height (the surface that the user sees and on
   *          top of which the points are drawn).
   * @param scaledPoint An array of two floats to be filled by this method. It represents the result
   *          point to draw on top of the camera preview View such that scaledPoint[0] holds that
   *          point's X and scaledPoint[1] holds this point's Y coordinate.
   */
  private void scalePointFromCamera(ResultPoint pointFromCamera,
      float cameraPreviewWidth, float cameraPreviewHeight,
      float previewViewWidth, float previewViewHeight, float[] scaledPoint) {
    float x = pointFromCamera.getX();
    float y = pointFromCamera.getY();
    float newX = cameraPreviewHeight - y;
    float newY = x;

    scaledPoint[0] = newX * (previewViewWidth / cameraPreviewHeight);
    scaledPoint[1] = newY * (previewViewHeight / cameraPreviewWidth);
  }

  public void addPossibleResultPoint(final ResultPoint point) {
    post(new Runnable() {
      @Override
      public void run() {
        List<ResultPoint> points = mPossibleResultPoints;
        points.add(point);
        int size = points.size();
        if (size > MAX_RESULT_POINTS) {
          // trim it
          points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
        }
      }
    });
  }
}
