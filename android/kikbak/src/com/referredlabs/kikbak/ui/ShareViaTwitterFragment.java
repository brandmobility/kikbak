
package com.referredlabs.kikbak.ui;

import java.io.IOException;

import twitter4j.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.twitter.TwitterAuthActivity;
import com.referredlabs.kikbak.twitter.TwitterHelper;
import com.referredlabs.kikbak.utils.Register;

public class ShareViaTwitterFragment extends SharingDialog {

  private static final String ARG_OFFER = "offer";
  private static final String ARG_COMMENT = "comment";
  private static final String ARG_PHOTO_PATH = "photo_uri";
  private static final String ARG_EMPLYOYEE = "emplyee";
  private static final String ARG_LOCATION_ID = "location_id";

  private static final int REQUEST_TWITTER_AUTH = 1;

  private ShareStatusListener mListener;
  private ClientOfferType mOffer;
  private TwitterHelper mTwitterHelper;

  public static ShareViaTwitterFragment newInstance(ClientOfferType offer, String comment,
      String photoPath, String employee, long locationId) {
    ShareViaTwitterFragment fragment = new ShareViaTwitterFragment();
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

    mTwitterHelper = new TwitterHelper();
    if (mTwitterHelper.isAuthorized()) {
      publishTweet();
    }
    else {
      Intent intent = new Intent(getActivity(), TwitterAuthActivity.class);
      startActivityForResult(intent, REQUEST_TWITTER_AUTH);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_CANCELED) {
      mListener.onShareCancelled();
    }

    publishTweet();
  }

  private void publishTweet() {
    if (mTask == null) {
      mTask = new PublishTask();
      mTask.execute();
    }
  }

  private class PublishTask extends TaskEx {

    @Override
    protected void doInBackground() throws IOException, TwitterException {
      Bundle args = getArguments();
      String photoPath = args.getString(ARG_PHOTO_PATH);
      ClientOfferType offer = new Gson().fromJson(args.getString(ARG_OFFER), ClientOfferType.class);
      String imageUrl = offer.giveImageUrl;
      if (photoPath != null) {
        long userId = Register.getInstance().getUserId();
        imageUrl = Http.uploadImage(userId, photoPath);
      }

      ShareExperienceResponse resp = reportToKikbak(imageUrl);
      mTwitterHelper.publish(resp.template.body);
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
      req.experience.type = SharedType.SHARE_MODE_SMS; // TODO: SharedType.SHARE_MODE_TWITTER

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
      FlurryAgent.onError(Log.E_PUBLISH_TWEET, e.getMessage(), Log.CLASS_NETWORK);
      mListener.onShareFailed();
    }
  }

}
