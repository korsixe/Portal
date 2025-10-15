package com.mipt.Portal.model;

public interface ProductAdvertisement extends Advertisement {

  void setBrand(String brand);
  String getBrand();

  void setCondition(String condition);
  String getCondition();

  void setWarrantyMonths(int months);
  int getWarrantyMonths();

  void setHasDelivery(boolean hasDelivery);
  boolean hasDelivery();

  Boolean isValidProduct();
}