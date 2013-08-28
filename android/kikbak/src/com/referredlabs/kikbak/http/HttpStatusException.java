
package com.referredlabs.kikbak.http;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.referredlabs.kikbak.data.StatusType;

public class HttpStatusException extends IOException {
  private static final long serialVersionUID = 8922056018682523738L;

  private int mHttpStatusCode;
  private String mResponse;
  StatusType mStatus;
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

  public StatusType getStatus() {
    if (!mStatusChecked) {
      mStatus = getStatusFromResponse();
      mStatusChecked = true;
    }
    return mStatus;
  }

  private StatusType getStatusFromResponse() {
    try {
      JsonReader reader = new JsonReader(new StringReader(mResponse));
      reader.beginObject();
      reader.nextName();
      AnyResponseWithStatus resp = new Gson().fromJson(reader, AnyResponseWithStatus.class);
      return resp.status;
    } catch (Exception e) {
      // ignore
    }
    return null;
  }

  public int getServerStatusCode() {
    StatusType status = getStatus();
    if (status != null)
      return status.code;
    return -1;
  }

  private static class AnyResponseWithStatus {
    public StatusType status;
  }

}
