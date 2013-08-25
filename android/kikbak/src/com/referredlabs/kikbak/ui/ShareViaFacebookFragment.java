
package com.referredlabs.kikbak.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.fb.FbObjectApi;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaFacebookFragment extends DialogFragment {

  final long userId = Register.getInstance().getUserId();

  private static final String ARG_OFFER = "offer";
  private static final String ARG_COMMENT = "comment";
  private static final String ARG_PHOTO_PATH = "photo_uri";
  private static final String ARG_EMPLYOYEE = "emplyee";
  private static final String ARG_LOCATION_ID = "location_id";
  private static final String ARG_OTHER_ADDRESS = "other";

  private static final int REQUEST_FB_AUTH = 1;

  private boolean mPermissionRequested;
  private PublishTask mTask;
  private ShareStatusListener mListener;
  private ClientOfferType mOffer;

  private StatusCallback mFbStatusCallback = new StatusCallback() {
    @Override
    public void call(Session session, SessionState state, Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

  public static ShareViaFacebookFragment newInstance(ClientOfferType offer, String comment,
      String photoPath, String employee, long locationId, String address) {
    ShareViaFacebookFragment fragment = new ShareViaFacebookFragment();
    Bundle args = new Bundle();
    args.putString(ARG_OFFER, new Gson().toJson(offer));
    args.putString(ARG_COMMENT, comment);
    args.putString(ARG_PHOTO_PATH, photoPath);
    args.putString(ARG_EMPLYOYEE, employee);
    args.putLong(ARG_LOCATION_ID, locationId); // may be -1
    args.putString(ARG_OTHER_ADDRESS, address); // may be null
    fragment.setArguments(args);
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (ShareStatusListener) activity;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOffer = new Gson().fromJson(getArguments().getString(ARG_OFFER), ClientOfferType.class);
    Session.openActiveSession(getActivity(), this, true, mFbStatusCallback);
  }

  private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
    if (session.isOpened()) {
      List<String> permissions = session.getPermissions();
      if (!permissions.containsAll(Arrays.asList(Fb.PUBLISH_PERMISSIONS))) {
        if (!mPermissionRequested) {
          requestPublishPermissions(session);
          mPermissionRequested = true;
        }
        return;
      }
      publishStory();
    }
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
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mTask != null)
      mTask.cancel(false);
  }

  private void publishStory() {
    if (mTask == null) {
      mTask = new PublishTask();
      mTask.execute();
    }
  }

  private void requestPublishPermissions(Session session) {
    if (session != null) {
      Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this,
          Arrays.asList(Fb.PUBLISH_PERMISSIONS))
          .setDefaultAudience(SessionDefaultAudience.FRIENDS)
          .setRequestCode(REQUEST_FB_AUTH);
      session.requestNewPublishPermissions(newPermissionsRequest);
    }
  }

  private class PublishTask extends AsyncTask<Void, Void, Void> {

    private boolean mFbSuccess = false;
    private boolean mKikbakSuccess = false;

    @Override
    protected Void doInBackground(Void... params) {
      Bundle args = getArguments();
      Session session = Session.getActiveSession();
      String comment = args.getString(ARG_COMMENT);
      String photoPath = args.getString(ARG_PHOTO_PATH);
      ClientOfferType offer = new Gson().fromJson(args.getString(ARG_OFFER), ClientOfferType.class);

      try {
        String imageUrl = offer.giveImageUrl;
        if (photoPath != null) {
          long userId = Register.getInstance().getUserId();
          imageUrl = Http.uploadImage(userId, photoPath);
        }
        String code = reportToKikbak(imageUrl);
        FbObjectApi.publishStory(session, offer, imageUrl, comment, code);
        mFbSuccess = true;
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
      req.experience.employeeId = args.getString(ARG_EMPLYOYEE);
      req.experience.imageUrl = imageUrl;
      req.experience.locationId = args.getLong(ARG_LOCATION_ID);
      req.experience.merchantId = mOffer.merchantId;
      req.experience.offerId = mOffer.id;
      req.experience.type = SharedType.SHARE_MODE_FACEBOOK;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      ShareExperienceResponse resp = Http.execute(uri, req, ShareExperienceResponse.class);
      return resp.referrerCode;
    }

    @Override
    protected void onPostExecute(Void result) {
      dismiss();
      if (mListener != null) {
        if (mFbSuccess && mKikbakSuccess) {
          mListener.onShareFinished();
        } else {
          mListener.onShareFailed();
        }
      }
    }
  }

}
