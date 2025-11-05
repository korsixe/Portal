package com.mipt.Portal.announcementContent.classes.media;

import com.mipt.Portal.announcementContent.сlasses.media.DBManager;
import lombok.RequiredArgsConstructor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
public class CreateMedia {
  private final List<String> mediaList;
  private final Scanner scanner;
  private final String mediaDirectory;
  private final int maxFiles;
  private final DBManager dbManager;
  private final int adId;

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
        System.out.println("Неверный ввод! Выберите 1 или 2");
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

    if (!sourceFile.exists()) {
      System.out.println("Файл не найден: " + filePath);
      return;
    }

    try {
      String fileName = sourceFile.getName();
      byte[] fileBytes = Files.readAllBytes(sourceFile.toPath());

      if (mediaList.size() >= maxFiles) {
        System.out.println("Достигнут лимит файлов во время копирования!");
        return;
      }

      // Копируем файл в папку
      Files.copy(sourceFile.toPath(), Paths.get(mediaDirectory + fileName), StandardCopyOption.REPLACE_EXISTING);
      mediaList.add(fileName);

      // Сохраняем в БД
      dbManager.saveFileToDB(adId, fileBytes);

      System.out.println("Файл добавлен: " + fileName);
      System.out.println("Размер: " + fileBytes.length + " байт");

    } catch (IOException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }
}