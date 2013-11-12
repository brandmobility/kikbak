
package com.referredlabs.kikbak.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaEmailFragment extends ShareViaBase {

  private static final int REQUEST_SEND_EMAIL = 2;

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
    if (requestCode == REQUEST_SEND_EMAIL) {
      // We do not know if user cancelled sending or not
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

  protected void onShareFinished(String subject, String body) {
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse("mailto:"));
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.putExtra(Intent.EXTRA_TEXT, body);
    // Spanned text = Html.fromHtml(body);
    // intent.putExtra(Intent.EXTRA_TEXT, text.toString());
    // intent.putExtra(Intent.EXTRA_HTML_TEXT, body);
    startActivityForResult(intent, REQUEST_SEND_EMAIL);
  }

  protected void onShareFailed() {
    mListener.onShareFailed();
    dismiss();
  }

  private class ShareTask extends TaskEx {

    private String mSubject;
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
      req.experience.type = SharedType.SHARE_MODE_EMAIL;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      ShareExperienceResponse resp = Http.execute(uri, req, ShareExperienceResponse.class);
      mSubject = resp.template.subject;
      mBody = resp.template.body;
    }

    @Override
    protected void onSuccess() {
      onShareFinished(mSubject, mBody);
    }

    @Override
    protected void onFailed(Exception exception) {
      onShareFailed();
    }
  }
}
