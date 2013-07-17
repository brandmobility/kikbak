
package com.referredlabs.kikbak.ui;

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
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PublishFragment extends DialogFragment {

  public interface ShareStatusListener {
    void onShareFinished(boolean success);
  }

  final long userId = Register.getInstance().getUserId();

  private static final String ARG_COMMENT = "comment";
  private static final String ARG_PHOTO_PATH = "photo_uri";
  private static final String ARG_LOCATION_ID = "location_id";
  private static final String ARG_MERCHANT_ID = "merchant_id";
  private static final String ARG_OFFER_ID = "offer_id";

  private static final int REQUEST_FB_AUTH = 1;

  private boolean mPermissionRequested;
  private PublishTask mTask;
  private ShareStatusListener mListener;

  private StatusCallback mFbStatusCallback = new StatusCallback() {
    @Override
    public void call(Session session, SessionState state, Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

  public static PublishFragment newInstance(String comment, String photoPath, long offerId,
      long merchantId, long locationId) {
    PublishFragment fragment = new PublishFragment();
    Bundle args = new Bundle();
    args.putString(ARG_COMMENT, comment);
    args.putString(ARG_PHOTO_PATH, photoPath);
    args.putLong(ARG_OFFER_ID, offerId);
    args.putLong(ARG_MERCHANT_ID, merchantId);
    args.putLong(ARG_LOCATION_ID, locationId);
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

      try {
        long imageId = Fb.publishGift(session, comment, photoPath);
        mFbSuccess = true;
        reportToKikbak(imageId);
        mKikbakSuccess = true;
      } catch (Exception e) {
        android.util.Log.d("MMM", "exception " + e);
      }
      return null;
    }

    private void reportToKikbak(long imageId) {
      final long userId = Register.getInstance().getUserId();
      Bundle args = getArguments();
      ShareExperienceRequest req = new ShareExperienceRequest();
      req.experience = new SharedType();
      req.experience.caption = args.getString(ARG_COMMENT);
      req.experience.fbImageId = imageId;
      req.experience.imageUrl = ""; // FIXME
      req.experience.locationId = args.getLong(ARG_LOCATION_ID);
      req.experience.merchantId = args.getLong(ARG_MERCHANT_ID);
      req.experience.offerId = args.getLong(ARG_OFFER_ID);
      req.experience.type = SharedType.SHARE_MODE_FACEBOOK;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      try {
        ShareExperienceResponse resp = Http.execute(uri, req, ShareExperienceResponse.class);
      } catch (IOException e) {
        android.util.Log.d("MMM", "Exception: " + e);
      }
    }

    @Override
    protected void onPostExecute(Void result) {
      dismiss();
      if (mListener != null)
        mListener.onShareFinished(true);
    }
  }

}
