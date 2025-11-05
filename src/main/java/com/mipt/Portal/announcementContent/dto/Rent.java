package com.mipt.Portal.announcementContent.dto;

import com.mipt.Portal.announcementContent.announcementResources.Enums.PricePeriod;

public class Rent extends Sale {
  private PricePeriod pricePeriod;

  public Rent(PricePeriod pricePeriod) {
    super();
    this.pricePeriod = pricePeriod;
  }

  public PricePeriod getPricePeriod() {
    return pricePeriod;
  }

  public void setPricePeriod(PricePeriod pricePeriod) {
    this.pricePeriod = pricePeriod;
  }
}
