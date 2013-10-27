
package com.referredlabs.kikbak.twitter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.referredlabs.kikbak.Kikbak;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHelper {

  private static final String TWITTER_CONSUMER_KEY = "f9EjYG1kPF3Lu0V4ag1vg";
  private static final String TWITTER_CONSUMER_SECRET = "D19nLn4ZN3beZxAoX51tx5k3OW8WknkNgvQnJCy1jc";
  private static final String TWITTER_CALLBACK_URL = "oauth://kikbak";
  private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
  private static final String SHARED_PREF_KEY = "twitter";
  private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
  private static final String PREF_KEY_OAUTH_SECRET = "oauth_secret";

  private Twitter mTwitter;

  RequestToken mRequestToken;

  public TwitterHelper() {
    Configuration config = getConfiguration();
    TwitterFactory factory = new TwitterFactory(config);
    mTwitter = factory.getInstance();
  }

  public boolean isAuthorized() {
    return getPrefs().contains(PREF_KEY_OAUTH_TOKEN);
  }

  public void resetAuthorization() {
    getPrefs().edit().clear().commit();
  }

  public String getAuthorizationUrl() throws TwitterException {
    mRequestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
    return mRequestToken.getAuthorizationURL();
  }

  public boolean processAuthUrl(String urlString) {
    try {
      Uri uri = Uri.parse(urlString);
      String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
      AccessToken accessToken = mTwitter.getOAuthAccessToken(mRequestToken, verifier);

      Editor e = getPrefs().edit();
      e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
      e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
      e.commit();
      return true;
    } catch (Exception e) {
      resetAuthorization();
    }
    return false;
  }

  public void publish(String message) throws TwitterException {
    SharedPreferences prefs = getPrefs();
    String access_token = prefs.getString(PREF_KEY_OAUTH_TOKEN, "");
    String access_token_secret = prefs.getString(PREF_KEY_OAUTH_SECRET, "");
    AccessToken accessToken = new AccessToken(access_token, access_token_secret);
    Configuration config = getConfiguration();
    Twitter twitter = new TwitterFactory(config).getInstance(accessToken);
    twitter.updateStatus(message);
  }

  private SharedPreferences getPrefs() {
    return Kikbak.getInstance().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
  }

  private Configuration getConfiguration() {
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
    return builder.build();
  }

}
