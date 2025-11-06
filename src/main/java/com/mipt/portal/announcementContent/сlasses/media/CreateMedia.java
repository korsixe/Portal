package com.mipt.portal.announcementContent.сlasses.media;

import lombok.RequiredArgsConstructor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
public class CreateMedia {
  private final List<String> mediaList;
  private final Scanner scanner;
  private final String mediaDirectory;
  private final int maxFiles;

  public void addMedia() {
    System.out.println("ДОБАВЛЕНИЕ МЕДИА");
    System.out.println("Можно добавить до " + maxFiles + " файлов");

    while (true) {
      System.out.println("Текущее количество файлов: " + mediaList.size() + "/" + maxFiles);
      System.out.println("1. Добавить файл по пути");
      System.out.println("2. Завершить добавление");
      System.out.print("Выберите действие: ");

      String input = scanner.nextLine();

      if (input.equals("2")) {
        break;
      } else if (input.equals("1")) {
        if (mediaList.size() >= maxFiles) {
          System.out.println("Достигнут лимит файлов! Максимум: " + maxFiles);
          continue;
        }
        addFileByPath();
      } else {
        System.out.println(" Неверный ввод! Выберите 1 или 2");
      }
    }
  }

  private void addFileByPath() {
    if (mediaList.size() >= maxFiles) {
      System.out.println("Достигнут лимит файлов! Максимум: " + maxFiles);
      return;
    }
    System.out.print("Введите полный путь к файлу: ");
    String filePath = scanner.nextLine().trim();

    File sourceFile = new File(filePath);


    try {
      String fileName = sourceFile.getName();

      if (mediaList.size() >= maxFiles) {
        System.out.println("Достигнут лимит файлов во время копирования!");
        return;
      }
      Path targetPath = Paths.get(mediaDirectory + fileName);
      Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
      mediaList.add(fileName);
      System.out.println("Файл добавлен: " + fileName);
      System.out.println("");

    } catch (IOException e) {
      System.out.println("Ошибка при копировании: " + e.getMessage());
    }
  }
}