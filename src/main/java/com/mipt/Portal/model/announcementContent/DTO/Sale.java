package com.mipt.Portal.model.announcementContent.DTO;

import com.mipt.Portal.model.announcementContent.resources.Enums.Condition;

public class Sale extends Announcement {
  private Condition condition;

  public Sale() {
    super();
    this.condition = condition;
  }

  public Condition getCondition() { return condition; }
  public void setCondition(Condition condition) { this.condition = condition; }

}