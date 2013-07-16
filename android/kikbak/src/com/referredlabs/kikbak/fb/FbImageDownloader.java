
package com.referredlabs.kikbak.fb;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.LruCache;

import com.facebook.Session;
import com.squareup.picasso.UrlConnectionLoader;

import java.io.IOException;

public class FbImageDownloader extends UrlConnectionLoader {

  private static final String FB_SCHEME = "fb";

  private static LruCache<String, String> sCache = new LruCache<String, String>(10);

  private Session mSession;

  public FbImageDownloader(Context context, Session session) {
    super(context);
    mSession = session;
  }

  @Override
  public Response load(Uri uri, boolean localCacheOnly) throws IOException {
    if (FB_SCHEME.equals(uri.getScheme())) {
      if (mSession == null)
        throw new IOException("Facebook session closed.");
      String id = uri.getLastPathSegment();

      String url = sCache.get(id);
      if (url == null) {
        url = Fb.getPhotoUrl(mSession, id);
        sCache.put(id, url);
      }
      uri = Uri.parse(url);
    }
    return super.load(uri, localCacheOnly);
  }
}
