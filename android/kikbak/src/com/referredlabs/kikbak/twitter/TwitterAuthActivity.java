
package com.referredlabs.kikbak.twitter;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.ui.KikbakActivity;

public class TwitterAuthActivity extends KikbakActivity {

  private TwitterHelper mTwitterHelper;
  private WebView mWebView;
  private MyWebViewClient mWebClient = new MyWebViewClient();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_twitter_auth);
    mWebView = (WebView) findViewById(R.id.webview);
    mWebView.setWebViewClient(mWebClient);

    mTwitterHelper = new TwitterHelper();
    mTask = new RequestAuthTask();
    mTask.execute();
  }

  class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.startsWith("oauth")) {
        mTask = new GetAccessTokenTask(url);
        mTask.execute();
      }
      return true;
    }
  }

  class RequestAuthTask extends TaskEx {
    private String mAuthUrl;

    @Override
    protected void doInBackground() throws Exception {
      mAuthUrl = mTwitterHelper.getAuthorizationUrl();
    }

    @Override
    protected void onSuccess() {
      mWebView.loadUrl(mAuthUrl);
    }

    @Override
    protected void onFailed(Exception exception) {
      setResult(RESULT_CANCELED);
      finish();
    }
  }

  class GetAccessTokenTask extends TaskEx {
    private String mUrl;

    GetAccessTokenTask(String url) {
      mUrl = url;
    }

    @Override
    protected void doInBackground() throws Exception {
      mTwitterHelper.processAuthUrl(mUrl);
    }

    @Override
    protected void onSuccess() {
      setResult(RESULT_OK);
      finish();
    }

    @Override
    protected void onFailed(Exception exception) {
      setResult(RESULT_CANCELED);
      finish();
    }
  }
}
