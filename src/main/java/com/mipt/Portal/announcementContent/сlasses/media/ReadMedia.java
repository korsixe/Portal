package com.mipt.Portal.announcementContent.classes.media;

import com.mipt.Portal.announcementContent.сlasses.media.DBManager;
import lombok.RequiredArgsConstructor;
import java.io.*;
import java.util.*;

@RequiredArgsConstructor
public class ReadMedia {
  private final List<String> mediaList;
  private final String mediaDirectory;
  private final DBManager dbManager;
  private final int adId;

  public void showAllFiles() {
    System.out.println("ВСЕ ФАЙЛЫ");
    for (String fileName : mediaList) {
      System.out.println("- " + fileName);
    }

    byte[] fileFromDB = dbManager.loadFileFromDB(adId);
    if (fileFromDB != null) {
      System.out.println("Файл в БД: " + fileFromDB.length + " байт");
    }
  }

  public File getFile(String fileName) {
    return new File(mediaDirectory + fileName);
  }
}