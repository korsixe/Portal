package com.mipt.Portal.model;


public interface Location {
  Long getId();
  void setId(Long id);

  Double getLatitude();
  void setLatitude(Double latitude);

  Double getLongitude();
  void setLongitude(Double longitude);

  String getAddress();
  void setAddress(String address);

  String getCity();
  void setCity(String city);

  String getCountry();
  void setCountry(String country);

  String getPostalCode();
  void setPostalCode(String postalCode);

}
