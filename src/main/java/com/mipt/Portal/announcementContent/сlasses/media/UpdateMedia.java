package com.mipt.Portal.announcementContent.сlasses.media;

import lombok.RequiredArgsConstructor;
import java.util.*;
import com.mipt.Portal.announcementContent.classes.media.CreateMedia;

@RequiredArgsConstructor
public class UpdateMedia {
  private final List<String> mediaList;
  private final Scanner scanner;
  private final String mediaDirectory;
  private final int maxFiles;

  public void updateFile(String oldFileName) {
    DeleteMedia deleteMedia = new DeleteMedia(mediaList, mediaDirectory);
    CreateMedia createMedia = new CreateMedia(mediaList, scanner, mediaDirectory, 1);

    deleteMedia.deleteFile(oldFileName);
    System.out.println("Теперь добавьте новый файл:");
    createMedia.addMedia();
  }
}