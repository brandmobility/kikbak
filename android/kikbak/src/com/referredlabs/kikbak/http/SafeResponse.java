package com.referredlabs.kikbak.http;

public class SafeResponse<T, V> {
  public final T request;
  public final long startTime;
  public final long endTime;
  public final V response;
  public final Exception exception;

  public SafeResponse(T request, long startTime, long endTime, V result) {
    this(request, startTime, endTime, result, null);
  }

  public SafeResponse(T request, long startTime, long endTime, Exception exception) {
    this(request, startTime, endTime, null, exception);
  }

  private SafeResponse(T request, long startTime, long endTime, V result, Exception exception) {
    this.request = request;
    this.startTime = startTime;
    this.endTime = endTime;
    this.response = result;
    this.exception = exception;
  }

  public boolean isSuccessful() {
    return exception == null;
  }
}