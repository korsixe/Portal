package com.mipt.portal.ad;

public class Main {
  public static void main(String[] args) {
    AdManager adManager = new AdManager();
    Ad ad = adManager.createAd(1);
    adManager.editAd(ad);
  }
}