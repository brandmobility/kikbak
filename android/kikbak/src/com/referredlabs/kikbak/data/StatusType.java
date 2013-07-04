
package com.referredlabs.kikbak.data;

public class StatusType {

  public static final int OK = 0;
  public static final int AUTH_ERROR = 1;
  public static final int INVALID_VERIFICATION_CODE_ERROR = 2;
  public static final int ERROR = 3;

  public String value;
  public int code;
  public String version;

  void validate() {
    if (version == null)
      throw new ValidationException();
  }
}
