package com.mipt.Portal.announcementContent.classes.media;

import com.mipt.Portal.announcementContent.сlasses.media.*;

import lombok.RequiredArgsConstructor;
import java.util.*;
@RequiredArgsConstructor
public class UpdateMedia {
  private final List<String> mediaList;
  private final Scanner scanner;
  private final String mediaDirectory;
  private final int maxFiles;
  private final DBManager dbManager;
  private final int adId;

  public void updateFile(String oldFileName) {
    com.mipt.Portal.announcementContent.classes.media.DeleteMedia deleteMedia = new com.mipt.Portal.announcementContent.classes.media.DeleteMedia(mediaList, mediaDirectory, dbManager, adId);
    com.mipt.Portal.announcementContent.classes.media.CreateMedia createMedia = new com.mipt.Portal.announcementContent.classes.media.CreateMedia(mediaList, scanner, mediaDirectory, maxFiles, dbManager, adId);

    deleteMedia.deleteFile(oldFileName);
    System.out.println("Теперь добавьте новый файл:");
    createMedia.addMedia();
  }
}