
package com.referredlabs.kikbak.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import com.referredlabs.kikbak.C;
import com.referredlabs.kikbak.http.Http;

import java.io.IOException;

public class FetchEmailTemplate extends AsyncTask<Void, Void, Void> {

  public interface OnEmailTemplateFetched {
    void onEmailTemplateFetched(String title, String body);

    void onEmailTemplateFetchFailed();
  }

  private String mUserName;
  private String mCode;
  private String mComment;
  private String mImageUrl;
  private OnEmailTemplateFetched mListener;
  private Template mTemplate;

  FetchEmailTemplate(OnEmailTemplateFetched listener, String name, String code, String comment,
      String imageUrl) {
    mUserName = name;
    mCode = code;
    mComment = comment;
    mImageUrl = imageUrl;
    mListener = listener;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      String uri = getUri();
      mTemplate = Http.execute(uri, Template.class);
    } catch (IOException e) {
    }
    return null;
  }

  private String getUri() {
    Uri.Builder b = new Uri.Builder();
    b.scheme("http").authority(C.SERVER).path("/s/email.php");
    b.appendQueryParameter("name", mUserName);
    b.appendQueryParameter("code", mCode);
    b.appendQueryParameter("desc", mComment);
    b.appendQueryParameter("url", mImageUrl);
    return b.build().toString();
  }

  @Override
  protected void onPostExecute(Void result) {
    if (mTemplate != null) {
      mListener.onEmailTemplateFetched(mTemplate.title, mTemplate.body);
    } else {
      mListener.onEmailTemplateFetchFailed();
    }
  }

  private static class Template {
    public String title;
    public String body;
  }
}
