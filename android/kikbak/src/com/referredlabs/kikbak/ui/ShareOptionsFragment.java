
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.referredlabs.kikbak.R;

public class ShareOptionsFragment extends DialogFragment implements OnClickListener {

  public static final String ARG_MERCHANT_NAME = "merchant";
  public static final String SPECIAL_VERIZON = "verizon";

  private EditText mVerizonId;

  public interface OnShareMethodSelectedListener {
    public void onSendViaEmail(String id);

    public void onSendViaFacebook(String id);

    public void onPostOnFacebook(String id);
  }

  private OnShareMethodSelectedListener mListener;

  public static ShareOptionsFragment newInstance(String merchantName) {
    ShareOptionsFragment dialog = new ShareOptionsFragment();
    Bundle args = new Bundle();
    args.putString(ARG_MERCHANT_NAME, merchantName);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, 0);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnShareMethodSelectedListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    boolean isVerizon = isVerizon();
    int layout = isVerizon ? R.layout.fragment_share_options_verizon
        : R.layout.fragment_share_options;
    View v = inflater.inflate(layout, container, false);
    v.findViewById(R.id.email_send_to_friends).setOnClickListener(this);
    v.findViewById(R.id.facebook_post_on_timeline).setOnClickListener(this);
    v.findViewById(R.id.facebook_send_to_friends).setOnClickListener(this);
    mVerizonId = (EditText) v.findViewById(R.id.verizon_employee_id);
    return v;
  }

  private boolean isVerizon() {
    String name = getArguments().getString(ARG_MERCHANT_NAME);
    if (name != null && name.toLowerCase().contains(SPECIAL_VERIZON))
      return true;
    return false;
  }

  @Override
  public void onClick(View v) {
    String id = mVerizonId != null ? mVerizonId.getText().toString() : null;

    switch (v.getId()) {
      case R.id.email_send_to_friends:
        mListener.onSendViaEmail(id);
        break;
      case R.id.facebook_send_to_friends:
        mListener.onSendViaFacebook(id);
        break;
      case R.id.facebook_post_on_timeline:
        mListener.onPostOnFacebook(id);
        break;
    }
    dismiss();
  }

}
