
package com.referredlabs.kikbak.tasks;

import com.flurry.android.FlurryAgent;
import com.referredlabs.kikbak.data.UpdateFriendResponse;
import com.referredlabs.kikbak.data.UpdateFriendsRequest;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.utils.Register;

public class UpdateFriendsTask extends Task {

  private String mAccessToken;

  public UpdateFriendsTask(String accessToken) {
    mAccessToken = accessToken;
  }

  @Override
  protected void doInBackground() {
    try {
      long id = Register.getInstance().getUserId();
      UpdateFriendsRequest request = new UpdateFriendsRequest();
      request.access_token = mAccessToken;
      String uri = Http.getUri(UpdateFriendsRequest.PATH + id);
      Http.execute(uri, request, UpdateFriendResponse.class);
    } catch (Exception e) {
      FlurryAgent.onError(Log.E_UPDATE_FRIENDS, e.getMessage(), Log.CLASS_NETWORK);
    }
  }
}
