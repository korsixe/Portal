package com.mipt.portal.announcementContent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MediaTest {

  public static void main(String[] args) throws SQLException, IOException {
    Connection connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/myproject",
        "myuser",
        "mypassword"
    );

    MediaManager manager = new MediaManager(connection, 1);

    // Загружаем фото
    manager.loadFromDB();

    // Добавляем и сохраняем
    manager.addPhoto("/Users/elizavetaorlova/Downloads/кот_2.jpg");
    manager.saveToDB();

    // Сохраняем в папку
    saveToFolder(manager, "/Users/elizavetaorlova/IdeaProjects/Portal1/exported_photos/");

    // файлы в бд
    manager.showPhotos();
    System.out.println(manager.getPhotosCount());

    manager.deleteAllPhotos();

    connection.close();
  }

  // я локально проверяю, не ломается ли файл после извлечения из бд, откроется ли фотка
  private static void saveToFolder(MediaManager manager, String folderPath) throws IOException {
    File folder = new File(folderPath);
    if (folder.exists()) {
      File[] files = folder.listFiles();
      if (files != null) {
        for (File file : files) {
          file.delete();
        }
      }
    }
    folder.mkdirs();

    for (int i = 0; i < manager.getPhotosCount(); i++) {
      byte[] photoData = manager.getPhotoByIndex(i);
      File outputFile = new File(folder, "photo_" + i + ".jpg");
      Files.write(outputFile.toPath(), photoData);
    }
  }
}