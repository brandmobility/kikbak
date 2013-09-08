
package com.referredlabs.kikbak.http;

import java.io.IOException;

public class HttpStatusException extends IOException {
  private static final long serialVersionUID = 8922056018682523738L;

  private int mHttpStatusCode;
  private String mResponse;
  boolean mStatusChecked;

  public HttpStatusException(String uri, int statusCode, String response) {
    super("HTTP " + statusCode + " " + uri);
    mHttpStatusCode = statusCode;
    mResponse = response;
  }

  public int getStatusCode() {
    return mHttpStatusCode;
  }

  public String getResponse() {
    return mResponse;
  }
}
