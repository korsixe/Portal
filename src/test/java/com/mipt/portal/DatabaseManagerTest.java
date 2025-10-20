package com.mipt.portal;

import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseManagerTest {

  private static final String TEST_DB_URL = "jdbc:sqlite:test_portal.db";
  private Connection testConn;

  @BeforeAll
  void setUp() throws SQLException {
    // Создаем тестовую базу данных
    testConn = DriverManager.getConnection(TEST_DB_URL);
    System.out.println("✅ Тестовая БД создана");
  }

  @AfterAll
  void tearDown() throws SQLException {
    // Закрываем соединение и удаляем тестовую БД
    if (testConn != null && !testConn.isClosed()) {
      testConn.close();
    }
    // Можно добавить удаление файла test_portal.db если нужно
    System.out.println("✅ Тестовая БД очищена");
  }

  @BeforeEach
  void createTestTables() throws SQLException {
    // Создаем тестовые таблицы перед каждым тестом
    String createUsersSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;

    String createAdsSQL = """
            CREATE TABLE IF NOT EXISTS ads (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                price DECIMAL(10, 2) NOT NULL,
                user_id INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users (id)
            )
        """;

    try (Statement stmt = testConn.createStatement()) {
      stmt.execute(createUsersSQL);
      stmt.execute(createAdsSQL);
    }
  }

  @AfterEach
  void clearTestData() throws SQLException {
    // Очищаем данные после каждого теста
    try (Statement stmt = testConn.createStatement()) {
      stmt.execute("DELETE FROM ads");
      stmt.execute("DELETE FROM users");
    }
  }

  @Test
  void testDatabaseConnection() {
    assertDoesNotThrow(() -> {
      Connection conn = DriverManager.getConnection(TEST_DB_URL);
      assertNotNull(conn, "Соединение с БД должно быть установлено");
      assertFalse(conn.isClosed(), "Соединение должно быть открытым");
      conn.close();
    }, "Подключение к БД не должно вызывать исключений");
  }

  @Test
  void testCreateTables() throws SQLException {
    // Проверяем что таблицы создаются без ошибок
    try (Statement stmt = testConn.createStatement()) {
      // Проверяем существование таблицы users
      ResultSet rs = stmt.executeQuery(
          "SELECT name FROM sqlite_master WHERE type='table' AND name='users'"
      );
      assertTrue(rs.next(), "Таблица users должна существовать");

      // Проверяем существование таблицы ads
      rs = stmt.executeQuery(
          "SELECT name FROM sqlite_master WHERE type='table' AND name='ads'"
      );
      assertTrue(rs.next(), "Таблица ads должна существовать");
    }
  }

  @Test
  void testInsertAndSelectUser() throws SQLException {
    // Тестируем вставку и выборку пользователя
    String insertSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
    try (PreparedStatement pstmt = testConn.prepareStatement(insertSQL)) {
      pstmt.setString(1, "Тестовый Пользователь");
      pstmt.setString(2, "test@mail.ru");
      int affectedRows = pstmt.executeUpdate();
      assertEquals(1, affectedRows, "Должна быть вставлена одна строка");
    }

    // Проверяем что пользователь добавлен
    String selectSQL = "SELECT * FROM users WHERE email = ?";
    try (PreparedStatement pstmt = testConn.prepareStatement(selectSQL)) {
      pstmt.setString(1, "test@mail.ru");
      ResultSet rs = pstmt.executeQuery();
      assertTrue(rs.next(), "Должен найтись добавленный пользователь");
      assertEquals("Тестовый Пользователь", rs.getString("name"));
      assertEquals("test@mail.ru", rs.getString("email"));
    }
  }

  @Test
  void testInsertAndSelectAd() throws SQLException {
    // Сначала добавляем пользователя
    String insertUserSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
    int userId;
    try (PreparedStatement pstmt = testConn.prepareStatement(insertUserSQL,
        Statement.RETURN_GENERATED_KEYS)) {
      pstmt.setString(1, "Автор Объявления");
      pstmt.setString(2, "author@mail.ru");
      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      assertTrue(rs.next());
      userId = rs.getInt(1);
    }

    // Добавляем объявление
    String insertAdSQL = """
            INSERT INTO ads (title, description, user_id, price) 
            VALUES (?, ?, ?, ?)
        """;
    try (PreparedStatement pstmt = testConn.prepareStatement(insertAdSQL)) {
      pstmt.setString(1, "Тестовое объявление");
      pstmt.setString(2, "Тестовое описание");
      pstmt.setInt(3, userId);
      pstmt.setDouble(4, 1000.50);
      int affectedRows = pstmt.executeUpdate();
      assertEquals(1, affectedRows, "Должно быть добавлено одно объявление");
    }

    // Проверяем объявление
    String selectSQL = "SELECT * FROM ads WHERE title = ?";
    try (PreparedStatement pstmt = testConn.prepareStatement(selectSQL)) {
      pstmt.setString(1, "Тестовое объявление");
      ResultSet rs = pstmt.executeQuery();
      assertTrue(rs.next(), "Должно найтись добавленное объявление");
      assertEquals("Тестовое описание", rs.getString("description"));
      assertEquals(1000.50, rs.getDouble("price"));
      assertEquals(userId, rs.getInt("user_id"));
    }
  }

  @Test
  void testUserEmailUniqueConstraint() {
    // Тестируем уникальность email
    String insertSQL = "INSERT INTO users (name, email) VALUES (?, ?)";

    // Первая вставка должна пройти успешно
    assertDoesNotThrow(() -> {
      try (PreparedStatement pstmt = testConn.prepareStatement(insertSQL)) {
        pstmt.setString(1, "Пользователь 1");
        pstmt.setString(2, "duplicate@mail.ru");
        pstmt.executeUpdate();
      }
    }, "Первая вставка с уникальным email должна пройти успешно");

    // Вторая вставка с тем же email должна вызвать исключение
    SQLException exception = assertThrows(SQLException.class, () -> {
      try (PreparedStatement pstmt = testConn.prepareStatement(insertSQL)) {
        pstmt.setString(1, "Пользователь 2");
        pstmt.setString(2, "duplicate@mail.ru");
        pstmt.executeUpdate();
      }
    }, "Вторая вставка с тем же email должна вызвать исключение");

    assertTrue(exception.getMessage().contains("UNIQUE constraint"),
        "Исключение должно быть связано с нарушением уникальности");
  }

  @Test
  void testSearchAds() throws SQLException {
    // Подготавливаем тестовые данные
    try (Statement stmt = testConn.createStatement()) {
      stmt.execute("INSERT INTO users (name, email) VALUES ('Тест Юзер', 'test@mail.ru')");
      stmt.execute("""
                INSERT INTO ads (title, description, user_id, price) 
                VALUES ('Продам MacBook', 'Отличный ноутбук', 1, 100000)
            """);
      stmt.execute("""
                INSERT INTO ads (title, description, user_id, price) 
                VALUES ('Куплю iPhone', 'Ищу телефон', 1, 50000)
            """);
    }

    // Тестируем поиск по заголовку
    String searchSQL = """
            SELECT * FROM ads 
            WHERE title LIKE ? OR description LIKE ?
        """;
    try (PreparedStatement pstmt = testConn.prepareStatement(searchSQL)) {
      pstmt.setString(1, "%MacBook%");
      pstmt.setString(2, "%MacBook%");
      ResultSet rs = pstmt.executeQuery();

      assertTrue(rs.next(), "Должен найтись MacBook");
      assertEquals("Продам MacBook", rs.getString("title"));
      assertFalse(rs.next(), "Должен быть только один результат");
    }
  }

  @Test
  void testForeignKeyConstraint() {
    // Тестируем foreign key constraint
    String insertAdSQL = """
            INSERT INTO ads (title, description, user_id, price) 
            VALUES (?, ?, ?, ?)
        """;

    SQLException exception = assertThrows(SQLException.class, () -> {
      try (PreparedStatement pstmt = testConn.prepareStatement(insertAdSQL)) {
        pstmt.setString(1, "Тестовое объявление");
        pstmt.setString(2, "Описание");
        pstmt.setInt(3, 999); // Несуществующий user_id
        pstmt.setDouble(4, 1000.0);
        pstmt.executeUpdate();
      }
    }, "Вставка объявления с несуществующим user_id должна вызвать исключение");

    assertTrue(exception.getMessage().contains("FOREIGN KEY") ||
            exception.getMessage().contains("constraint"),
        "Исключение должно быть связано с foreign key constraint");
  }
}