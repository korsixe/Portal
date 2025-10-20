import java.sql.*;

public class DatabaseManager {
  private static final String URL = "jdbc:sqlite:/home/korsixe/Документы/portal.db";

  public static Connection connect() {
    Connection conn = null;
    try {
      // Явно регистрируем драйвер
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection(URL);
      System.out.println("✅ Подключение к SQLite установлено!");
    } catch (ClassNotFoundException e) {
      System.out.println("❌ Драйвер SQLite не найден!");
      System.out.println("Проверьте зависимость в pom.xml");
    } catch (SQLException e) {
      System.out.println("❌ Ошибка подключения: " + e.getMessage());
      System.out.println("Проверьте путь: " + URL);
    }
    return conn;
  }

  public static void createTable() {
    String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE
            );
        """;

    try (Connection conn = connect()) {
      // Проверяем, что подключение успешно
      if (conn == null) {
        System.out.println("❌ Не удалось подключиться к БД");
        return;
      }

      try (Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
        System.out.println("✅ Таблица создана!");
      }
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при создании таблицы: " + e.getMessage());
    }
  }

  public static void insertUser(String name, String email) {
    String sql = "INSERT INTO users(name, email) VALUES(?, ?)";

    try (Connection conn = connect()) {
      if (conn == null) {
        System.out.println("❌ Не удалось подключиться к БД");
        return;
      }

      try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.executeUpdate();
        System.out.println("✅ Пользователь добавлен!");
      }
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при добавлении пользователя: " + e.getMessage());
    }
  }

  public static void selectAllUsers() {
    String sql = "SELECT * FROM users";

    try (Connection conn = connect()) {
      if (conn == null) {
        System.out.println("❌ Не удалось подключиться к БД");
        return;
      }

      try (Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {

        System.out.println("Список пользователей:");
        System.out.println("ID\tName\tEmail");
        System.out.println("--\t----\t-----");

        while (rs.next()) {
          System.out.println(rs.getInt("id") + "\t" +
              rs.getString("name") + "\t" +
              rs.getString("email"));
        }
      }
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при чтении данных: " + e.getMessage());
    }
  }
}