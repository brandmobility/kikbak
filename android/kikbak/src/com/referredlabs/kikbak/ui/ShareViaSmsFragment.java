
package com.referredlabs.kikbak.ui;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaSmsFragment extends DialogFragment {

  private static final String ARG_OFFER = "offer";
  private static final String ARG_COMMENT = "comment";
  private static final String ARG_PHOTO_PATH = "photo_path";

  private static final int REQUEST_SELECT_CONTACTS = 1;
  private static final int REQUEST_SEND_SMS = 2;

  private ClientOfferType mOffer;
  private ShareTask mTask;
  private ShareStatusListener mListener;
  private ArrayList<String> mContacts;

  public static ShareViaSmsFragment newInstance(ClientOfferType offer, String comment,
      String photoPath) {
    ShareViaSmsFragment fragment = new ShareViaSmsFragment();
    Bundle args = new Bundle();
    args.putString(ARG_OFFER, new Gson().toJson(offer));
    args.putString(ARG_COMMENT, comment);
    args.putString(ARG_PHOTO_PATH, photoPath);
    fragment.setArguments(args);
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOffer = new Gson().fromJson(getArguments().getString(ARG_OFFER), ClientOfferType.class);

    // start picker
    Intent intent = new Intent(getActivity(), PickContactsActivity.class);
    intent.putExtra(PickContactsActivity.ARG_TYPE, PickContactsActivity.TYPE_PHONE);
    startActivityForResult(intent, REQUEST_SELECT_CONTACTS);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (ShareStatusListener) activity;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    ProgressDialog dialog = new ProgressDialog(getActivity());
    dialog.setMessage(getActivity().getString(R.string.share_in_progress));
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
    return dialog;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_SELECT_CONTACTS) {
      if (resultCode == Activity.RESULT_OK) {
        mContacts = data.getStringArrayListExtra(PickContactsActivity.DATA);
        share();
      } else {
        mListener.onShareFinished(false);
        dismiss();
      }
    }

    if (requestCode == REQUEST_SEND_SMS) {
      mListener.onShareFinished(true);
      dismiss();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mTask != null)
      mTask.cancel(false);
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
    mListener.onShareFinished(false);
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

  private class ShareTask extends AsyncTask<Void, Void, Void> {

    private String mBody;
    private boolean mKikbakSuccess = false;

    @Override
    protected Void doInBackground(Void... params) {
      Bundle args = getArguments();
      String photoPath = args.getString(ARG_PHOTO_PATH);
      ClientOfferType offer = new Gson().fromJson(args.getString(ARG_OFFER), ClientOfferType.class);

      try {
        String imageUrl = offer.giveImageUrl;
        if (photoPath != null) {
          long userId = Register.getInstance().getUserId();
          imageUrl = Http.uploadImage(userId, photoPath);
        }
        reportToKikbak(imageUrl);
        mKikbakSuccess = true;
      } catch (Exception e) {
        android.util.Log.d("MMM", "exception " + e);
      }
      return null;
    }

    private void reportToKikbak(String imageUrl) throws IOException {
      final long userId = Register.getInstance().getUserId();
      Bundle args = getArguments();
      ShareExperienceRequest req = new ShareExperienceRequest();
      req.experience = new SharedType();
      req.experience.caption = args.getString(ARG_COMMENT);
      req.experience.employeeId = ""; // FIXME
      req.experience.imageUrl = imageUrl;
      req.experience.locationId = mOffer.locations[0].locationId; // TODO:
      req.experience.merchantId = mOffer.merchantId;
      req.experience.offerId = mOffer.id;
      req.experience.type = SharedType.SHARE_MODE_SMS;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      ShareExperienceResponse resp = Http.execute(uri, req, ShareExperienceResponse.class);
      mBody = resp.template.body;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (mKikbakSuccess) {
        onShareFinished(mBody);
      } else {
        onShareFailed();
      }
    }
  }
}
