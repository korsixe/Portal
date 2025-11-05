package com.mipt.Portal.announcementContent.сlasses.media;

import lombok.RequiredArgsConstructor;
import java.sql.*;

@RequiredArgsConstructor
public class DBManager {
  private final Connection connection;

  public void saveFileToDB(int adId, byte[] fileData) {
    String sql = "UPDATE ads SET photos = ? WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setBytes(1, fileData);
      statement.setInt(2, adId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }

  public void deleteFileFromDB(int adId) {
    String sql = "UPDATE ads SET photos = NULL WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, adId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }

  public byte[] loadFileFromDB(int adId) {
    String sql = "SELECT photos FROM ads WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, adId);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getBytes("photos");
      }
    } catch (SQLException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
    return null;
  }
}
