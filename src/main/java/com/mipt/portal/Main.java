package com.mipt.portal;

public class Main {
  public static void main(String[] args) {
    System.out.println("🚀 Запуск Portal Application\n");

    // 1. Создаем таблицы
    DatabaseManager.createTables();

    // 2. Добавляем тестовые данные
    DatabaseManager.insertTestData();

    // 3. Показываем пользователей
    DatabaseManager.selectAllUsers();

    // 4. Показываем объявления
    DatabaseManager.selectAllAds();

    // 5. Добавляем нового пользователя
    DatabaseManager.insertUser("Новый Пользователь", "new@mail.ru");

    // 6. Добавляем новое объявление
    DatabaseManager.insertAd("Новое объявление", "Тестовое описание", 1, 10000.00);

    // 7. Поиск объявлений
    DatabaseManager.searchAds("MacBook");
    DatabaseManager.searchAds("котенок");

    System.out.println("\n✅ Программа завершена!");
  }
}