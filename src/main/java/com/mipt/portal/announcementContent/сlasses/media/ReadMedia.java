package com.mipt.portal.announcementContent.сlasses.media;

import lombok.RequiredArgsConstructor;
import java.io.*;
import java.util.*;

@RequiredArgsConstructor
public class ReadMedia {
  private final List<String> mediaList;
  private final String mediaDirectory;

  public void showAllFiles() {
    System.out.println("ВСЕ ФАЙЛЫ");
    for (String fileName : mediaList) {
      System.out.println("- " + fileName);
    }
  }

  public File getFile(String fileName) {
    return new File(mediaDirectory + fileName);
  }
}
