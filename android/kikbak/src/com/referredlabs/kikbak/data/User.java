package com.referredlabs.kikbak.data;

import com.facebook.model.GraphUser;

public class User {

  private String email;
  private String first_name;
  private String gender;
  private long id;
  private String last_name;
  private String link;
  private String locale;
  private String name;
  private int timezone;
  private String updated_time;
  private String username;
  private boolean verified;

  public User() {
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return first_name;
  }

  public void setFirstName(String first_name) {
    this.first_name = first_name;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getLastName() {
    return last_name;
  }

  public void setLastName(String last_name) {
    this.last_name = last_name;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTimezone() {
    return timezone;
  }

  public void setTimezone(int timezone) {
    this.timezone = timezone;
  }

  public String getUpdatedTime() {
    return updated_time;
  }

  public void setUpdatedTime(String updated_time) {
    this.updated_time = updated_time;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username == null ? "" : username;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }
  
  public static User createTestUser(String name) {
    User user = new User();
    user.setEmail(name + "@kikbak.com");
    user.setName(name);
    user.setFirstName(name);
    user.setLastName("wonderland");
    user.setGender("female");
    user.setId(System.currentTimeMillis());
    user.setLink("http://www.alice.com/" + name);
    user.setLocale("us");
    user.setTimezone(0);
    user.setUpdatedTime("2013-10-05");
    user.setVerified(true);
    return user;
  }

  public static User createFromFacebook(GraphUser fb) {
    User user = new User();
    user.setEmail((String) fb.getProperty("email"));
    user.setName(fb.getName());
    user.setFirstName(fb.getFirstName());
    user.setLastName(fb.getLastName());
    user.setGender((String) fb.getProperty("gender"));
    user.setLink(fb.getLink());
    user.setLocale((String) fb.getProperty("locale"));
    user.setTimezone((Integer) fb.getProperty("timezone"));
    user.setUpdatedTime((String) fb.getProperty("updated_time"));
    user.setVerified((Boolean) fb.getProperty("verified"));
    user.setUsername(fb.getUsername());

    String id = fb.getId();

    return user;
  }

}
