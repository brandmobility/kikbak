
package com.referredlabs.kikbak.ui;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony.Sms;

import com.google.gson.Gson;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaSmsFragment extends ShareViaBase {

  private static final int REQUEST_SEND_SMS = 2;

  private ClientOfferType mOffer;
  private ShareStatusListener mListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOffer = new Gson().fromJson(getArguments().getString(ARG_OFFER), ClientOfferType.class);

    share();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (ShareStatusListener) activity;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_SEND_SMS) {
      mListener.onShareFinished();
      dismiss();
    }
  }

  private void share() {
    if (mTask == null) {
      mTask = new ShareTask();
      mTask.execute();
    }
  }

  protected void onShareFinished(String body) {

    Intent intent = new Intent();

    // Google Hangout is broken and does not handle sms intent correctly
    if (isHangoutDefaultSmsApp()) {
      intent.setPackage("com.google.android.talk");
      intent.setAction(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_TEXT, body);
    } else {
      intent.setAction(Intent.ACTION_SENDTO);
      intent.setData(Uri.parse("sms:"));
      intent.putExtra("sms_body", body);
    }
    startActivityForResult(intent, REQUEST_SEND_SMS);
  }

  @SuppressLint("NewApi")
  boolean isHangoutDefaultSmsApp() {
    final String hangoutPackage = "com.google.android.talk";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      String pkg = Sms.getDefaultSmsPackage(getActivity());
      if (hangoutPackage.equals(pkg))
        return true;
    }

    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
    PackageManager pm = getActivity().getPackageManager();
    ResolveInfo result = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    if (result != null && hangoutPackage.equals(result.activityInfo.packageName))
      return true;

    return false;
  }

  protected void onShareFailed() {
    mListener.onShareFailed();
    dismiss();
  }

  private class ShareTask extends TaskEx {

    private String mBody;

    @Override
    protected void doInBackground() throws IOException {
      Bundle args = getArguments();
      String photoPath = args.getString(ARG_PHOTO_PATH);
      ClientOfferType offer = new Gson().fromJson(args.getString(ARG_OFFER), ClientOfferType.class);

      String imageUrl = offer.giveImageUrl;
      if (photoPath != null) {
        long userId = Register.getInstance().getUserId();
        imageUrl = Http.uploadImage(userId, photoPath);
      }
      reportToKikbak(imageUrl);
    }

    private void reportToKikbak(String imageUrl) throws IOException {
      final long userId = Register.getInstance().getUserId();
      Bundle args = getArguments();
      ShareExperienceRequest req = new ShareExperienceRequest();
      req.experience = new SharedType();
      req.experience.caption = args.getString(ARG_COMMENT);
      req.experience.employeeId = args.getString(ARG_EMPLYOYEE);
      req.experience.imageUrl = imageUrl;
      req.experience.locationId = args.containsKey(ARG_LOCATION_ID)
          ? args.getLong(ARG_LOCATION_ID) : null;
      req.experience.merchantId = mOffer.merchantId;
      req.experience.offerId = mOffer.id;
      req.experience.type = SharedType.SHARE_MODE_SMS;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      ShareExperienceResponse resp = Http.execute(uri, req, ShareExperienceResponse.class);
      mBody = resp.template.body;
    }

    @Override
    protected void onSuccess() {
      onShareFinished(mBody);
    }

    @Override
    protected void onFailed(Exception exception) {
      onShareFailed();
    }
  }
}
