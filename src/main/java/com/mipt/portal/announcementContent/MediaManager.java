package com.mipt.portal.announcementContent;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class MediaManager {

  private final Connection connection;
  private final int adId;
  private List<byte[]> photos = new ArrayList<>();

  public void addPhoto(String filePath) throws IOException {
    byte[] fileData = Files.readAllBytes(new File(filePath).toPath());
    photos.add(fileData);
  }

  public void showPhotos() {
    for (int i = 0; i < photos.size(); i++) {
      System.out.println(i + ". " + photos.get(i).length + " байт");
    }
  }

  public void saveToDB() throws SQLException {
    byte[][] photosArray = photos.toArray(new byte[0][]);
    Array sqlArray = connection.createArrayOf("BYTEA", photosArray);

    String sql = "UPDATE ads SET photos = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setArray(1, sqlArray);
      stmt.setInt(2, adId);
      stmt.executeUpdate();
    }
  }

  public void deleteFromDB(int index) throws SQLException {
    if (index < 0 || index >= photos.size()) {
      throw new IllegalArgumentException("Неверный индекс: " + index);
    }
    photos.remove(index);
    saveToDB();
  }

  public void loadFromDB() throws SQLException {
    String sql = "SELECT photos FROM ads WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, adId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        Array photosArray = rs.getArray("photos");
        if (photosArray != null) {
          byte[][] dbPhotos = (byte[][]) photosArray.getArray();
          photos = new ArrayList<>(Arrays.asList(dbPhotos));
        }
      }
    }
  }

  public byte[] getPhotoByIndex(int index) {
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
}