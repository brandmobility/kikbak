
package com.referredlabs.kikbak.tasks;

import android.os.AsyncTask;

import com.facebook.model.GraphUser;
import com.referredlabs.kikbak.data.FriendType;
import com.referredlabs.kikbak.data.UpdateFriendResponse;
import com.referredlabs.kikbak.data.UpdateFriendsRequest;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateFriends extends AsyncTask<Void, Void, Void> {

  @Override
  protected Void doInBackground(Void... params) {
    long id = Register.getInstance().getUserId();
    List<GraphUser> users = Fb.getFriends();
    if (users != null && users.size() > 0) {
      List<FriendType> friends = toFriendList(users);
      UpdateFriendsRequest request = new UpdateFriendsRequest();
      request.friends = friends.toArray(new FriendType[friends.size()]);
      String uri = Http.getUri(UpdateFriendsRequest.PATH + id);
      try {
        UpdateFriendResponse response = Http.execute(uri, request, UpdateFriendResponse.class);
        android.util.Log.d("MMM", "OK");
      } catch (IOException e) {
        android.util.Log.d("MMM", "exception:" + e);
        // log error
      }
    }
    return null;
  }

  private List<FriendType> toFriendList(List<GraphUser> users) {
    ArrayList<FriendType> result = new ArrayList<FriendType>(users.size());
    for (GraphUser user : users) {
      FriendType friend = toFriend(user);
      result.add(friend);
    }
    return result;
  }

  private FriendType toFriend(GraphUser user) {
    FriendType friend = new FriendType();
    friend.id = Long.valueOf(user.getId());
    friend.first_name = user.getFirstName();
    friend.last_name = user.getLastName();
    friend.name = user.getName();
    friend.username = user.getUsername();
    return friend;
  }

}
