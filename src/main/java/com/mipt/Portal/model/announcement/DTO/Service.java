package com.mipt.Portal.model.announcement.DTO;

public class Service extends Announcement {

  private String serviceCategory;
  private int experienceYears;
  private boolean hasCertification;


  public Service(String serviceCategory, int experienceYears) {
    super();
    this.serviceCategory = serviceCategory;
    this.experienceYears = experienceYears;
    this.hasCertification = hasCertification;
  }

  public String getServiceCategory() { return serviceCategory; }
  public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }

  public int getExperienceYears() { return experienceYears; }
  public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

  public boolean isHasCertification() { return hasCertification; }
  public void setHasCertification(boolean hasCertification) { this.hasCertification = hasCertification; }


 }

