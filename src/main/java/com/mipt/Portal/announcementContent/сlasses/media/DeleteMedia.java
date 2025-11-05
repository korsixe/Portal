package com.mipt.Portal.announcementContent.classes.media;

import com.mipt.Portal.announcementContent.сlasses.media.DBManager;
import lombok.RequiredArgsConstructor;
import java.io.*;
import java.util.List;

@RequiredArgsConstructor
public class DeleteMedia {
  private final List<String> mediaList;
  private final String mediaDirectory;
  private final DBManager dbManager;
  private final int adId;

  public void deleteFile(String fileName) {
    File file = new File(mediaDirectory + fileName);
    if (file.exists()) {
      file.delete();
      mediaList.remove(fileName);

      dbManager.deleteFileFromDB(adId);

      System.out.println("Файл удален: " + fileName);
    } else {
      System.out.println("Файл не найден: " + fileName);
    }
  }

  public void deleteAllFiles() {
    for (String fileName : mediaList) {
      File file = new File(mediaDirectory + fileName);
      file.delete();
    }
    mediaList.clear();

    dbManager.deleteFileFromDB(adId);

    System.out.println("Все файлы удалены!");
  }
}