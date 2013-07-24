
package com.referredlabs.kikbak.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import java.io.IOException;
import java.util.ArrayList;

public class ShareViaEmailFragment extends DialogFragment {

  private static final String ARG_OFFER = "offer";
  private static final String ARG_COMMENT = "comment";
  private static final String ARG_PHOTO_PATH = "photo_path";

  private static final int REQUEST_SELECT_CONTACTS = 1;

  private ClientOfferType mOffer;
  private ShareTask mTask;
  private ShareStatusListener mListener;
  private ArrayList<String> mContacts;

  public static ShareViaEmailFragment newInstance(ClientOfferType offer, String comment,
      String photoPath) {
    ShareViaEmailFragment fragment = new ShareViaEmailFragment();
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
    intent.putExtra(PickContactsActivity.ARG_TYPE, PickContactsActivity.TYPE_EMAIL);
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
    if (requestCode == REQUEST_SELECT_CONTACTS && resultCode == Activity.RESULT_OK) {
      mContacts = data.getStringArrayListExtra(PickContactsActivity.DATA);
      if (mContacts != null && mContacts.size() > 0) {
        share();
        return;
      }
    }

    mListener.onShareFinished(false);
    dismiss();
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

  protected void onShareFinished(String title, String body) {
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse("mailto:"));
    intent.putExtra(Intent.EXTRA_EMAIL, mContacts.toArray(new String[mContacts.size()]));
    intent.putExtra(Intent.EXTRA_SUBJECT, title);
    Spanned text = Html.fromHtml(body);
    intent.putExtra(Intent.EXTRA_TEXT, text.toString());
    intent.putExtra(Intent.EXTRA_HTML_TEXT, body);
    startActivity(intent);
    dismiss();
    mListener.onShareFinished(true);
  }

  protected void onShareFailed() {
    mListener.onShareFinished(false);
  }

  private class ShareTask extends AsyncTask<Void, Void, Void> {

    private ShareTemplateResponse mTemplate;
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
        String code = reportToKikbak(imageUrl);
        fetchTemplate(code, imageUrl);

        mKikbakSuccess = true;
      } catch (Exception e) {
        android.util.Log.d("MMM", "exception " + e);
      }
      return null;
    }

    private String reportToKikbak(String imageUrl) throws IOException {
      final long userId = Register.getInstance().getUserId();
      Bundle args = getArguments();
      ShareExperienceRequest req = new ShareExperienceRequest();
      req.experience = new SharedType();
      req.experience.caption = args.getString(ARG_COMMENT);
      req.experience.fbImageId = -1; // FIXME
      req.experience.employeeId = ""; // FIXME
      req.experience.imageUrl = imageUrl;
      req.experience.locationId = mOffer.locations[0].locationId; // TODO:
      req.experience.merchantId = mOffer.merchantId;
      req.experience.offerId = mOffer.id;
      req.experience.type = SharedType.SHARE_MODE_EMAIL;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      ShareExperienceResponse resp = Http.execute(uri, req, ShareExperienceResponse.class);
      return resp.referrerCode;
    }

    void fetchTemplate(String reffererCode, String imageUrl) throws IOException {
      String uri = getTemplateUri(reffererCode, imageUrl);
      mTemplate = Http.execute(uri, ShareTemplateResponse.class);
    }

    private String getTemplateUri(String code, String imageUrl) {
      String name = Register.getInstance().getUserName();
      String comment = getArguments().getString(ARG_COMMENT);

      Uri.Builder b = new Uri.Builder();
      b.scheme("http").authority("54.244.124.116").path("/s/email.php");
      b.appendQueryParameter("name", name);
      b.appendQueryParameter("code", code);
      b.appendQueryParameter("desc", comment);
      b.appendQueryParameter("url", imageUrl);
      return b.build().toString();
    }

    @Override
    protected void onPostExecute(Void result) {
      if (mKikbakSuccess) {
        onShareFinished(mTemplate.title, mTemplate.body);
      } else {
        onShareFailed();
      }
    }
  }
  
  private static class ShareTemplateResponse {
    public String title;
    public String body;

    // NOTE: GET request without top level type in response;
  }
}
