package com.referredlabs.kikbak.data;

public class StatusType {
  public String value;
  public int code;
  public String version;

  void validate() {
    if (version == null)
      throw new ValidationException();
  }
}
