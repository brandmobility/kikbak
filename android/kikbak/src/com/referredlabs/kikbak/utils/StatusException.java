
package com.referredlabs.kikbak.utils;

@SuppressWarnings("serial")
public class StatusException extends Exception {
  private Enum<?> mStatus;

  public StatusException(Enum<?> status) {
    mStatus = status;
  }

  @SuppressWarnings("unchecked")
  public <T extends Enum<?>> T getStatus() {
    return (T) mStatus;
  }
}
