
package com.referredlabs.kikbak.data;

public class RegisterUserResponse {
  public UserIdType userId;
  public StatusType status;

  public void validate() {
    if (userId == null || status == null)
      throw new ValidationException();
  }
}
