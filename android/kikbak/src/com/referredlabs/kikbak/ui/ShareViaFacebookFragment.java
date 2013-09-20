
package com.referredlabs.kikbak.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.fb.FbObjectApi;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.tasks.UpdateFriendsTask;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaFacebookFragment extends SharingDialog {

  final long userId = Register.getInstance().getUserId();

  private static final String ARG_OFFER = "offer";
  private static final String ARG_COMMENT = "comment";
  private static final String ARG_PHOTO_PATH = "photo_uri";
  private static final String ARG_EMPLYOYEE = "emplyee";
  private static final String ARG_LOCATION_ID = "location_id";

  private static final int REQUEST_FB_AUTH = 1;

  private boolean mPermissionRequested;
  private ShareStatusListener mListener;
  private ClientOfferType mOffer;

  private StatusCallback mFbStatusCallback = new StatusCallback() {
    @Override
    public void call(Session session, SessionState state, Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

  public static ShareViaFacebookFragment newInstance(ClientOfferType offer, String comment,
      String photoPath, String employee, long locationId) {
    ShareViaFacebookFragment fragment = new ShareViaFacebookFragment();
    Bundle args = new Bundle();
    args.putString(ARG_OFFER, new Gson().toJson(offer));
    args.putString(ARG_COMMENT, comment);
    args.putString(ARG_PHOTO_PATH, photoPath);
    args.putString(ARG_EMPLYOYEE, employee);
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
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

  private class PublishTask extends TaskEx {

    @Override
    protected void doInBackground() throws IOException {
      Bundle args = getArguments();
      Session session = Session.getActiveSession();
      String comment = args.getString(ARG_COMMENT);
      String photoPath = args.getString(ARG_PHOTO_PATH);
      ClientOfferType offer = new Gson().fromJson(args.getString(ARG_OFFER), ClientOfferType.class);

      UpdateFriendsTask update = new UpdateFriendsTask(session.getAccessToken());
      update.execute();

      String imageUrl = offer.giveImageUrl;
      if (photoPath != null) {
        long userId = Register.getInstance().getUserId();
        imageUrl = Http.uploadImage(userId, photoPath);
      }

      ShareExperienceResponse resp = reportToKikbak(imageUrl);
      FbObjectApi.publishStory(session, offer, resp.template.landingUrl, imageUrl, comment,
          resp.referrerCode);
    }

    private ShareExperienceResponse reportToKikbak(String imageUrl) throws IOException {
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
      req.experience.type = SharedType.SHARE_MODE_FACEBOOK;

      String uri = Http.getUri(ShareExperienceRequest.PATH + userId);
      return Http.execute(uri, req, ShareExperienceResponse.class);
    }

    @Override
    protected void onSuccess() {
      dismiss();
      mListener.onShareFinished();
    }

    @Override
    protected void onFailed(Exception e) {
      dismiss();
      FlurryAgent.onError(Log.E_PUBLISH_STORY, e.getMessage(), Log.CLASS_NETWORK);
      mListener.onShareFailed();
    }
  }
}
