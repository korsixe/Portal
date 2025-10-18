package com.mipt.portal.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator {
  public static void createAdsTable() {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS ads ("
        + "id INT AUTO_INCREMENT PRIMARY KEY, "
        + "title VARCHAR(255) NOT NULL, "
        + "description TEXT NOT NULL, "
        + "category VARCHAR(100), "
        + "condition VARCHAR(50), "
        + "negotiablePrice BOOLEAN, "
        + "price INT, "
        + "location VARCHAR(100), "
        + "idUser INT, "
        + "status VARCHAR(20)"
        + ")";

    try (Connection connection = DatabaseConnection.getConnection();
        Statement statement = connection.createStatement()) {

      statement.executeUpdate(createTableSQL);
      System.out.println("Таблица 'ads' успешно создана!");

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}