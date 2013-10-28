
package com.referredlabs.kikbak.ui;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaSmsFragment extends ShareViaBase {

  private static final int REQUEST_SELECT_CONTACTS = 1;
  private static final int REQUEST_SEND_SMS = 2;

  private ClientOfferType mOffer;
  private ShareStatusListener mListener;
  private ArrayList<String> mContacts;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOffer = new Gson().fromJson(getArguments().getString(ARG_OFFER), ClientOfferType.class);

    // start picker
    Intent intent = new Intent(getActivity(), PickContactsActivity.class);
    intent.putExtra(PickContactsActivity.ARG_TYPE, PickContactsActivity.TYPE_PHONE);
    intent.putExtra(PickContactsActivity.ARG_BUTTON_TEXT, R.string.share_pick_button_sms);
    startActivityForResult(intent, REQUEST_SELECT_CONTACTS);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (ShareStatusListener) activity;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_SELECT_CONTACTS) {
      if (resultCode == Activity.RESULT_OK) {
        if (data != null)
          mContacts = data.getStringArrayListExtra(PickContactsActivity.DATA);
        share();
      } else {
        mListener.onShareCancelled();
        dismiss();
      }
    }

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
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("sms:" + getSmsRecipients()));
    intent.putExtra("sms_body", body);
    startActivityForResult(intent, REQUEST_SEND_SMS);
  }

  protected void onShareFailed() {
    mListener.onShareFailed();
    dismiss();
  }

  private String getSmsRecipients() {
    if (mContacts == null || mContacts.size() == 0)
      return "";

    StringBuilder b = new StringBuilder();
    for (String contact : mContacts) {
      b.append(contact).append(',');
    }
    return b.toString();
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
      req.experience.locationId = longOrNull(args.getLong(ARG_LOCATION_ID));
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
