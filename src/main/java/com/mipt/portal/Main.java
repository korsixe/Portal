package com.mipt.portal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
  public static void main(String[] args) {
    System.out.println("🚀 Запуск Portal Application");

    try {
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://localhost:5432/myproject",
          "myuser",
          "mypassword"
      );

      DatabaseManager dbManager = new DatabaseManager(connection);
      dbManager.createTables();
      System.out.println("✅ Таблицы успешно созданы!");
      dbManager.insertData();

    } catch (SQLException e) {
      System.err.println("❌ Ошибка подключения к базе данных: " + e.getMessage());
      e.printStackTrace();
    }
  }
}