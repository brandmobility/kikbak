
package com.referredlabs.kikbak.http;

import java.io.IOException;

public class HttpStatusException extends IOException {
  private static final long serialVersionUID = 8922056018682523738L;

  private int mStatusCode;
  private String mResponse;

  public HttpStatusException(int statusCode, String response) {
    mStatusCode = statusCode;
    mResponse = response;
  }

  int getStatusCode() {
    return mStatusCode;
  }

  String getResponse() {
    return mResponse;
  }

}