public class Main {
  public static void main(String[] args) {
    System.out.println("=== Тестирование подключения к БД ===");

    // 1. Создаем таблицу
    DatabaseManager.createTable();

    // 2. Добавляем тестовых пользователей
    DatabaseManager.insertUser("Иван Иванов", "ivan@mail.ru");
    DatabaseManager.insertUser("Мария Петрова", "maria@mail.ru");
    DatabaseManager.insertUser("Алексей Сидоров", "alex@mail.ru");

    // 3. Читаем и выводим всех пользователей
    DatabaseManager.selectAllUsers();

    System.out.println("=== Завершено ===");
  }
}