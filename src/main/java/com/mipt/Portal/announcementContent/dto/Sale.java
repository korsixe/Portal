package com.mipt.Portal.announcementContent.dto;

import com.mipt.Portal.announcementContent.announcementResources.Enums.Condition;

public class Sale extends Announcement {
  private Condition condition;

  public Sale() {
    super();
    this.condition = condition;
  }

  public Condition getCondition() { return condition; }
  public void setCondition(Condition condition) { this.condition = condition; }

}