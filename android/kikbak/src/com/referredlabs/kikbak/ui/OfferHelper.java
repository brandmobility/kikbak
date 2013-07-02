
package com.referredlabs.kikbak.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.utils.Distance;

public class OfferHelper {

  private View mRootView;

  private TextView mName;
  private TextView mDistance;
  public ImageView mImage;

  public OfferHelper(View view) {
    mRootView = view;
    mName = (TextView) view.findViewById(R.id.offer_name);
    mDistance = (TextView) view.findViewById(R.id.distance);
    mImage = (ImageView) view.findViewById(R.id.offer_image);
  }

  void setName(String name) {
    mName.setText(name);
  }

  void setDistance(float distance) {
    String txt = Distance.getLocalizedDistance(mRootView.getContext(), distance);
    mDistance.setText(txt);
  }

}
