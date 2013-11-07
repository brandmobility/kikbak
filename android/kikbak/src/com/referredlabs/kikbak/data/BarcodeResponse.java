
package com.referredlabs.kikbak.data;

public class BarcodeResponse {
  
  public static String getPath(long userId, long allocatedGiftId) {
    return "/rewards/allocateBarcode/" + userId + "/" + allocatedGiftId + "/";
  }
  
  public String code;
}
