package com.mipt.Portal.model.announcement.DTO;

import com.mipt.Portal.model.announcement.resources.Enums.PricePeriod;

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
