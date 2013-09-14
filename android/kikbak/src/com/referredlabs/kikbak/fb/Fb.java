
package com.referredlabs.kikbak.fb;

import android.net.Uri;

public class Fb {
  public final static String[] READ_PERMISSIONS = {
      "email"
  };
  public final static String[] PUBLISH_PERMISSIONS = {
      "publish_actions"
  };

  public static Uri getFriendPhotoUri(long friendId) {
    return Uri.parse("https://graph.facebook.com/" + friendId + "/picture?type=square");
  }
}
