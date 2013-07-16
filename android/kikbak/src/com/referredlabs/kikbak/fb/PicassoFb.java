
package com.referredlabs.kikbak.fb;

import android.content.Context;

import com.facebook.Session;
import com.squareup.picasso.Picasso;

public class PicassoFb {
  public static Picasso with(Context context) {
    Session session = Session.openActiveSessionFromCache(context);
    Picasso picasso = new Picasso.Builder(context).loader(
        new FbImageDownloader(context, session)).build();
    return picasso;
  }
}
