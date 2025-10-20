package com.mipt.portal.ad;

public class Main {
  public static void main(String[] args) {
    AdManager adManager = new AdManager();
    Ad ad = adManager.createAd("shabunina.ao@phystech.edu");
    adManager.editAd(ad);
  }
}