package com.mipt.portal.announcementContent.сlasses.media;

import lombok.RequiredArgsConstructor;
import java.io.*;
import java.util.List;

@RequiredArgsConstructor
public class DeleteMedia {
  private final List<String> mediaList;
  private final String mediaDirectory;

  public void deleteFile(String fileName) {
    File file = new File(mediaDirectory + fileName);
    if (file.exists()) {
      file.delete();
      mediaList.remove(fileName);
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
    System.out.println("Все файлы удалены!");
  }
}
