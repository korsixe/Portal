package com.mipt.portal.announcement;

public class Main {
  public static void main(String[] args) {
    AdManager adManager = new AdManager();
    Announcement ad = adManager.createAd(1);
    adManager.editAd(ad);
  }
}