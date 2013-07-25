
package com.referredlabs.kikbak.fb;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.referredlabs.kikbak.data.ClientOfferType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.util.List;

public class Fb {
  public final static String[] READ_PERMISSIONS = {
      "email"
  };
  public final static String[] PUBLISH_PERMISSIONS = {
      "publish_actions"
  };

  public static long publishGift(Session session, String comment, String photoPath)
      throws FileNotFoundException, IOException {
    String photoId = uploadPhoto(session, photoPath);
    String url = getPhotoUrl(session, photoId);
    publishStory(session, comment, url);

    long id = getIdFromString(photoId);
    return id;
  }

  private static String uploadPhoto(Session session, String path) throws FileNotFoundException {
    File file = new File(path);
    Request req = Request.newUploadPhotoRequest(session, file, null);
    Response resp = req.executeAndWait();
    // TODO: handle errors
    GraphObject obj = resp.getGraphObject();
    String id = (String) obj.getProperty("id");
    return id;
  }

  public static String getPhotoUrl(Session session, String photoId) throws IOException {
    Request req = Request.newGraphPathRequest(session, photoId, null);
    Response resp = req.executeAndWait();

    if (resp.getError() != null)
      throw new IOException("Fb: can not get photo url for id:" + photoId);
    String url = (String) resp.getGraphObject().getProperty("source");
    return url;
  }

  private static void publishStory(Session session, String comment, String url) {
    Bundle postParams = new Bundle();
    postParams.putString("name", "Facebook SDK for Android");
    postParams.putString("caption", "Build great social apps and get more installs.");
    postParams.putString("description", comment);
    postParams.putString("link", "http://www.wp.pl");
    postParams.putString("picture", url);

    Request req = new Request(session, "me/feed", postParams, HttpMethod.POST, null);
    Response resp = req.executeAndWait();
    resp.getError();
  }

  public static void postOnTimeLine(final Context context, ClientOfferType offer, File photo) {
    final Session session = Session.openActiveSessionFromCache(context);
    try {
      Request req = Request.newUploadPhotoRequest(session, photo, new Callback() {

        @Override
        public void onCompleted(Response response) {
          android.util.Log.d("MMM", "response " + response);
          String id = (String) response.getGraphObject().getProperty("id");

          publishStory(context, id);
        }
      });

      RequestAsyncTask x = req.executeAsync();
    } catch (Exception e) {
      android.util.Log.d("MMM", "exception " + e);
    }
  }

  public static void getImageUrl(final Context context, String imageId) {
    final Session session = Session.openActiveSessionFromCache(context);
    try {
      String path = /* "http://graph.facebook.com/" + */imageId;
      Request req = Request.newGraphPathRequest(session, path, new Callback() {
        @Override
        public void onCompleted(Response response) {
          android.util.Log.d("MMM", "response " + response);

          String url = (String) response.getGraphObject().getProperty("source");
          publishStory(context, url);

        }
      });
      RequestAsyncTask x = req.executeAsync();
    } catch (Exception e) {
      android.util.Log.d("MMM", "exception " + e);
    }
  }

  private static void publishStory(final Context context, String photoUrl) {
    Session session = Session.getActiveSession();

    if (session != null) {

      // // Check for publish permissions
      // List<String> permissions = session.getPermissions();
      // if (!isSubsetOf(PERMISSIONS, permissions)) {
      // pendingPublishReauthorization = true;
      // Session.NewPermissionsRequest newPermissionsRequest = new Session
      // .NewPermissionsRequest(this, PERMISSIONS);
      // session.requestNewPublishPermissions(newPermissionsRequest);
      // return;
      // }

      Bundle postParams = new Bundle();
      postParams.putString("name", "Facebook SDK for Android");
      postParams.putString("caption", "Build great social apps and get more installs.");
      postParams
          .putString(
              "description",
              "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
      postParams.putString("link", "http://www.wp.pl");
      postParams.putString("picture", photoUrl);

      Request.Callback callback = new Request.Callback() {
        public void onCompleted(Response response) {
          JSONObject graphResponse = response
              .getGraphObject()
              .getInnerJSONObject();
          String postId = null;
          try {
            postId = graphResponse.getString("id");
          } catch (JSONException e) {
            Log.i("MMM",
                "JSON error " + e.getMessage());
          }
          FacebookRequestError error = response.getError();
          if (error != null) {
            Toast.makeText(context
                .getApplicationContext(),
                error.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(context
                .getApplicationContext(),
                postId,
                Toast.LENGTH_LONG).show();
          }
        }
      };

      Request request = new Request(session, "me/feed", postParams,
          HttpMethod.POST, callback);

      RequestAsyncTask task = new RequestAsyncTask(request);
      task.execute();
    }
  }

  public static List<GraphUser> getFriends() {
    Session activeSession = Session.getActiveSession();
    if (activeSession.getState().isOpened()) {
      Request friendRequest = Request.newMyFriendsRequest(activeSession, null);
      Bundle params = new Bundle();
      params.putString("fields", "id, name, username, first_name, last_name");
      friendRequest.setParameters(params);
      Response response = friendRequest.executeAndWait();
      List<GraphUser> users = Fb.typedListFromResponse(response, GraphUser.class);
      return users;
    }
    return null;
  }

  public static Uri getFriendPhotoUri(long friendId) {
    return Uri.parse("https://graph.facebook.com/" + friendId + "/picture?type=square");
  }

  private static <T extends GraphObject> List<T> typedListFromResponse(Response response,
      Class<T> clazz) {
    GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
    if (multiResult == null) {
      return null;
    }

    GraphObjectList<GraphObject> data = multiResult.getData();
    if (data == null) {
      return null;
    }

    return data.castToListOf(clazz);
  }

  private static long getIdFromString(String id) {
    long result = 0;
    try {
      result = Long.parseLong(id);
    } catch (NumberFormatException e) {
      // ignore
    }
    return result;
  }

}
