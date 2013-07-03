
package com.referredlabs.kikbak.data;

public class GetUserOffersRequest {
  public static final String PATH = "/user/offer/";
  
  public UserLocationType userLocation;

  public static GetUserOffersRequest create(double latitude, double longitude) {
    GetUserOffersRequest request = new GetUserOffersRequest();
    request.userLocation = new UserLocationType();
    request.userLocation.latitude = latitude;
    request.userLocation.longitude = longitude;
    return request;
  }
}
