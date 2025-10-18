package main.java.com.mipt.portal.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final String URL = "jdbc:mysql://localhost:3306/portal";
  private static final String USER = "root";
  private static final String PASSWORD = "050514";

  public static Connection getConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(URL, USER, PASSWORD);
      System.out.println("Подключение успешно!");
    } catch (SQLException e) {
      System.err.println("Ошибка подключения: " + e.getMessage());
    }
    return connection;
  }
}
