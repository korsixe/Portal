package com.mipt.portal;

public class Main {
  public static void main(String[] args) {
    System.out.println("🚀 Запуск Portal Application");

    // Создаем экземпляр DatabaseManager
    DatabaseManager dbManager = new DatabaseManager();

    // Вызываем методы без static
    dbManager.createTables();
    dbManager.insertData();

    // остальной код
  }
}