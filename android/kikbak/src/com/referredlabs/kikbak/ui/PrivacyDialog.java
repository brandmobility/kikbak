
package com.referredlabs.kikbak.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;

import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.R;

public class PrivacyDialog extends DialogFragment {

  public static PrivacyDialog newInstance() {
    return new PrivacyDialog();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    WebView webView = new WebView(getActivity());
    webView.loadUrl(C.PRIVACY_URI);
    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    b.setTitle(R.string.privacy_policy);
    b.setView(webView);
    b.setPositiveButton(android.R.string.ok, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    return b.create();
  }
}
