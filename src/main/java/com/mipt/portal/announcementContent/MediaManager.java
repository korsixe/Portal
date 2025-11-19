package com.mipt.portal.announcementContent;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MediaManager implements AutoCloseable {

  private final Connection connection;
  private final int adId;
  private List<byte[]> photos = new ArrayList<>();

  public void addPhoto(String filePath) throws IOException {
    byte[] fileData = Files.readAllBytes(new File(filePath).toPath());
    photos.add(fileData);
  }

  public void addPhoto(byte[] fileData) {
    if (fileData != null && fileData.length > 0) {
      photos.add(fileData);
    }
  }

  public void showPhotos() {
    for (int i = 0; i < photos.size(); i++) {
      System.out.println(i + ". " + photos.get(i).length + " байт");
    }
  }

  public void saveToDB() throws SQLException {
    if (photos.isEmpty()) {
      System.out.println("⚠️ No photos to save");
      return;
    }

    // Сохраняем как массив bytea
    String sql = "UPDATE ads SET photos = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {

      // Преобразуем List<byte[]> в массив для PostgreSQL
      byte[][] photosArray = photos.toArray(new byte[photos.size()][]);
      Array sqlArray = connection.createArrayOf("bytea", photosArray);

      stmt.setArray(1, sqlArray);
      stmt.setInt(2, adId);

      int affectedRows = stmt.executeUpdate();

      if (affectedRows > 0) {
        System.out.println("✅ Successfully saved " + photos.size() + " photos to database");
      } else {
        System.err.println("❌ Failed to save photos - no rows affected");
      }
    }
  }

  public void loadFromDB() throws SQLException {
    String sql = "SELECT photos FROM ads WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, adId);
      ResultSet rs = stmt.executeQuery();

      photos.clear();
      if (rs.next()) {
        Array photosArray = rs.getArray("photos");
        if (photosArray != null) {
          Object[] dbPhotos = (Object[]) photosArray.getArray();
          if (dbPhotos != null) {
            for (Object photoData : dbPhotos) {
              if (photoData instanceof byte[]) {
                byte[] imageData = (byte[]) photoData;
                // Проверяем, что это действительно изображение (не кастомная сериализация)
                if (isValidImageData(imageData)) {
                  photos.add(imageData);
                }
              }
            }
            System.out.println("✅ Loaded " + photos.size() + " photos from database");
          }
        }
      }
    }
  }

  // Проверяем
  private boolean isValidImageData(byte[] data) {
    if (data == null || data.length < 100) return false; // Минимальный размер для JPEG

    // Проверяем сигнатуры JPEG, PNG и т.д.
    if (data.length >= 3) {
      // JPEG: FF D8 FF
      if ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF) {
        return true;
      }
      // PNG: 89 50 4E 47
      if ((data[0] & 0xFF) == 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47) {
        return true;
      }
    }

    // Если не распознали сигнатуру, но данные достаточно большие - считаем валидным
    return data.length > 1000;
  }

  // фото
  public void deleteFromDB(int index) throws SQLException {
    if (index < 0 || index >= photos.size()) {
      throw new IllegalArgumentException("Неверный индекс: " + index);
    }
    photos.remove(index);
    saveToDB();
  }

  public byte[] getPhotoByIndex(int index) {
    if (index < 0 || index >= photos.size()) {
      throw new IllegalArgumentException("Invalid photo index: " + index);
    }
    return photos.get(index);
  }

  public int getPhotosCount() {
    return photos.size();
  }

  public void deleteAllPhotos() throws SQLException {
    photos.clear();
    saveToDB();
  }

  public List<byte[]> getAllPhotos() {
    return new ArrayList<>(photos);
  }

  public List<byte[]> loadAndGetPhotos() throws SQLException {
    loadFromDB();
    return getAllPhotos();
  }

  @Override
  public void close() {
    photos.clear();
  }
}