package com.mipt.portal.announcementContent.dto;

import com.mipt.portal.announcementContent.announcementResources.Enums.Condition;

public class Sale extends Announcement {
  private Condition condition;

  public Sale() {
    super();
    this.condition = condition;
  }

  public Condition getCondition() { return condition; }
  public void setCondition(Condition condition) { this.condition = condition; }

}