package com.mipt.Portal.model.announcementContent.Interfaces;

import com.mipt.Portal.model.announcementContent.resources.Enums.PricePeriod;

import java.time.LocalDateTime;


public interface AdvertisementEditor {
  Advertisement getAdvertisement();
  void setAdvertisement(Advertisement advertisement);

  User getEditor();
  void setEditor(User editor);

  LocalDateTime getEditTime();
  void setEditTime(LocalDateTime editTime);

  void editTitle(String newTitle);

  void editDescription(String newDescription);

  void editPrice(Double newPrice);
  void editPricePeriod(PricePeriod newPricePeriod);


  void hideAdvertisement();
  void showAdvertisement();
  void deleteAdvertisement();
  void restoreAdvertisement();

  Boolean canEdit();
}