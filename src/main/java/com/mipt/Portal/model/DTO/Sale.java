package com.mipt.Portal.model.DTO;

import com.mipt.Portal.model.resources.Enums.ItemCondition;

public class Sale extends Announcement {
  private ItemCondition condition;

  public Sale() {
    super();
    this.condition = condition;
  }

  public ItemCondition getCondition() { return condition; }
  public void setCondition(ItemCondition condition) { this.condition = condition; }

}