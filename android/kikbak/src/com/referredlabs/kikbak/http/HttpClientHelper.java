
package com.referredlabs.kikbak.http;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

public class HttpClientHelper {

  private static final String USER_AGENT = "kikbak-v1";
  // Default connection and socket timeout of 20 seconds. Tweak to taste.
  private static final int SOCKET_OPERATION_TIMEOUT = 20 * 1000;

  private static DefaultHttpClient sClient;

  public static DefaultHttpClient getHttpClient() {
    return sClient;
  }

  public static void createInstance(Context context) {
    HttpParams params = new BasicHttpParams();

    // Turn off stale checking. Our connections break all the time anyway,
    // and it's not worth it to pay the penalty of checking every time.
    HttpConnectionParams.setStaleCheckingEnabled(params, false);

    HttpConnectionParams.setConnectionTimeout(params, SOCKET_OPERATION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
    HttpConnectionParams.setSocketBufferSize(params, 8192);

    // Don't handle redirects -- return them to the caller. Our code
    // often wants to re-POST after a redirect, which we must do ourselves.
    HttpClientParams.setRedirecting(params, false);

    // Use a session cache for SSL sockets
    SSLSessionCache sessionCache = context == null ? null : new SSLSessionCache(context);

    // Set the specified user agent and register standard protocols.
    HttpProtocolParams.setUserAgent(params, USER_AGENT);
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http",
        PlainSocketFactory.getSocketFactory(), 80));
    schemeRegistry.register(new Scheme("https",
        SSLCertificateSocketFactory.getHttpSocketFactory(
            SOCKET_OPERATION_TIMEOUT, sessionCache), 443));

    ClientConnectionManager manager =
        new ThreadSafeClientConnManager(params, schemeRegistry);

    // We use a factory method to modify superclass initialization
    // parameters without the funny call-a-static-method dance.
    DefaultHttpClient client = new DefaultHttpClient(manager, params);

    PersistentCookieStore cookieStore = new PersistentCookieStore(context);
    client.setCookieStore(cookieStore);

    sClient = client;
  }
}
