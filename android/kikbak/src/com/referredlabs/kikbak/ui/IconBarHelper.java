
package com.referredlabs.kikbak.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.utils.LocaleUtils;
import com.referredlabs.kikbak.utils.Nearest;

public class IconBarHelper implements OnClickListener {

  public interface IconBarListener {
    void onMapIconClicked(Nearest location);

    void onPhoneIconClicked(String phone);

    void onWebIconClicked(String url);

  }

  protected View mRootView;
  protected TextView mDistance;
  protected View mPhoneIcon;
  protected View mWebIcon;
  protected String mUrl;
  protected String mPhone;
  protected Nearest mLocation;
  protected IconBarListener mListener;

  public IconBarHelper(View root, IconBarListener listener) {
    mRootView = root;
    mListener = listener;

    mDistance = (TextView) root.findViewById(R.id.distance);
    mDistance.setOnClickListener(this);

    mPhoneIcon = root.findViewById(R.id.phone_icon);
    mPhoneIcon.setOnClickListener(this);

    mWebIcon = root.findViewById(R.id.web_icon);
    mWebIcon.setOnClickListener(this);
  }

  public void setLink(String url) {
    mUrl = url;
  }

  public void setPhone(String phone) {
    mPhone = phone;
  }

  public void setDistance(float distance, Object location) {
    String txt = LocaleUtils.getLocalizedDistance(mRootView.getContext(), distance);
    mDistance.setText(txt);
  }

  public void setLocation(Nearest location) {
    mLocation = location;
    float distance = location.getDistance();
    String txt = LocaleUtils.getLocalizedDistance(mRootView.getContext(), distance);
    mDistance.setText(txt);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.web_icon:
        if (mListener != null)
          mListener.onWebIconClicked(mUrl);
        break;
      case R.id.phone_icon:
        if (mListener != null)
          mListener.onPhoneIconClicked(mPhone);
        break;
      case R.id.distance:
        if (mListener != null)
          mListener.onMapIconClicked(mLocation);
        break;
    }
  }
}
