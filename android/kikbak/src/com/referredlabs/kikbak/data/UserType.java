
package com.referredlabs.kikbak.data;

import com.facebook.model.GraphUser;

public class UserType {

  public String email;
  public String first_name;
  public String gender;
  public long id;
  public String last_name;
  public String link;
  public String locale;
  public String name;
  public int timezone;
  public String updated_time;
  public boolean verified;

  public static UserType createFromFacebook(GraphUser fb) {
    UserType user = new UserType();
    user.email = ((String) fb.getProperty("email"));
    user.name = (fb.getName());
    user.first_name = (fb.getFirstName());
    user.last_name = (fb.getLastName());
    user.gender = ((String) fb.getProperty("gender"));
    user.link = (fb.getLink());
    user.locale = ((String) fb.getProperty("locale"));
    // Object tz = ((Double) fb.getProperty("timezone"));
    // tz may be a Integer or Double
    user.timezone = 0; // TODO: fixme
    user.updated_time = ((String) fb.getProperty("updated_time"));
    user.verified = ((Boolean) fb.getProperty("verified"));
    user.id = Long.valueOf(fb.getId());
    return user;
  }
}
