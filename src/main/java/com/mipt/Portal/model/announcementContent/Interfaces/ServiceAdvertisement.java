package com.mipt.Portal.model.announcementContent.Interfaces;

public interface ServiceAdvertisement extends Advertisement {

  void setServiceCategory(String category);
  String getServiceCategory();

  void setExperienceYears(int years);
  int getExperienceYears();

  void setHasCertification(boolean certified);
  boolean hasCertification();

  void setServiceArea(String area);
  String getServiceArea();

  void setDurationHours(int hours);
  int getDurationHours();

  Boolean isValidService();
}
