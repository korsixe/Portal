package com.mipt.Portal.model.announcementContent.Classes;

public class AddMedia {
  public String uploadFile(String file, Long adId) {
    try {
      // 1. Сохраняем файл в папку
      String fileName = "file_" + System.currentTimeMillis() + ".jpg";

      // 2. Сохраняем в базу
      saveToDb(fileName, adId);

      return fileName;

    } catch (Exception e) {
      return "Ошибка: " + e.getMessage();
    }
  }

  private void saveToDb(String fileName, Long adId) {
    try {
      // запись в базу данных
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}