/*
package com.mipt.portal;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseManagerTest {

  private DatabaseManager dbManager;
  private Connection connection;

  @BeforeAll
  void setUpAll() throws SQLException {
    dbManager = DatabaseManager.getInstance();
    connection = dbManager.getConnection();

    // Очищаем таблицы перед началом тестов
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DELETE FROM ads");
      stmt.execute("DELETE FROM users");
    }
  }

  @AfterAll
  void tearDownAll() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @BeforeEach
  void setUp() throws SQLException {
    // Очищаем данные перед каждым тестом
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DELETE FROM ads");
      stmt.execute("DELETE FROM users");
    }
  }

  @Test
  void testCreateUser() throws SQLException {
    // Подготовка
    String email = "test@mipt.ru";
    String name = "Test User";
    String password = "password123";

    // Выполнение
    long userId = dbManager.createUser(email, name, password, "Moscow", "Computer Science", 2);

    // Проверка
    assertTrue(userId > 0);

    // Проверяем что пользователь действительно создан
    try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
      stmt.setLong(1, userId);
      ResultSet rs = stmt.executeQuery();

      assertTrue(rs.next());
      assertEquals(email, rs.getString("email"));
      assertEquals(name, rs.getString("name"));
      assertEquals("Moscow", rs.getString("address"));
      assertEquals("Computer Science", rs.getString("study_program"));
      assertEquals(2, rs.getInt("course"));
      assertEquals(0.0, rs.getDouble("rating"));
      assertEquals(0, rs.getInt("coins"));
    }
  }

  @Test
  void testCreateUserWithDuplicateEmail() throws SQLException {
    // Подготовка
    String email = "duplicate@mipt.ru";
    dbManager.createUser(email, "User 1", "pass1", null, null, null);

    // Выполнение и проверка
    assertThrows(SQLException.class, () -> {
      dbManager.createUser(email, "User 2", "pass2", null, null, null);
    });
  }

  @Test
  void testGetUserById() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("getuser@mipt.ru", "Get User", "password", "Address", "Physics", 3);

    // Выполнение
    User user = dbManager.getUserById(userId);

    // Проверка
    assertNotNull(user);
    assertEquals(userId, user.getId());
    assertEquals("getuser@mipt.ru", user.getEmail());
    assertEquals("Get User", user.getName());
    assertEquals("Address", user.getAddress());
    assertEquals("Physics", user.getStudyProgram());
    assertEquals(3, user.getCourse());
  }


  @Test
  void testCreateAd() throws SQLException {
    long userId = dbManager.createUser("adcreator@mipt.ru", "Ad Creator", "pass", null, null, null);

    long adId = dbManager.createAd(
        "Test Ad",
        "Test Description",
        1,  // ELECTRONICS
        0,  // USED
        1000,
        "Moscow",
        userId,
        "active",
        new byte[0]  // пустой массив для фото
    );

    assertTrue(adId > 0);

    try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ads WHERE id = ?")) {
      stmt.setLong(1, adId);
      ResultSet rs = stmt.executeQuery();

      assertTrue(rs.next());
      assertEquals("Test Ad", rs.getString("title"));
      assertEquals("Test Description", rs.getString("description"));
      assertEquals(1, rs.getInt("category"));
      assertEquals(0, rs.getInt("condition"));
      assertEquals(1000, rs.getInt("price"));
      assertEquals("Moscow", rs.getString("location"));
      assertEquals(userId, rs.getLong("user_id"));
      assertEquals("active", rs.getString("status"));
      assertEquals(0, rs.getInt("view_count"));
    }
  }

  @Test
  void testCreateAdWithInvalidUser() {
    // Пытаемся создать объявление для несуществующего пользователя
    assertThrows(SQLException.class, () -> {
      dbManager.createAd(
          "Invalid Ad",
          "Description",
          0, 0, 100, "Location",
          999999L,  // несуществующий ID
          "active",
          new byte[0]
      );
    });
  }

  @Test
  void testGetAdById() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("adgetter@mipt.ru", "Ad Getter", "pass", null, null, null);
    long adId = dbManager.createAd("Get Ad", "Description", 2, 1, 500, "SPB", userId, "active", new byte[0]);

    // Выполнение
    Ad ad = dbManager.getAdById(adId);

    // Проверка
    assertNotNull(ad);
    assertEquals(adId, ad.getId());
    assertEquals("Get Ad", ad.getTitle());
    assertEquals("Description", ad.getDescription());
    assertEquals(2, ad.getCategory());
    assertEquals(1, ad.getCondition());
    assertEquals(500, ad.getPrice());
    assertEquals("SPB", ad.getLocation());
    assertEquals(userId, ad.getUserId());
    assertEquals("active", ad.getStatus());
    assertEquals(0, ad.getViewCount());
  }

  @Test
  void testGetAdsByUserId() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("multiad@mipt.ru", "Multi Ad", "pass", null, null, null);

    // Создаем несколько объявлений
    dbManager.createAd("Ad 1", "Desc 1", 0, 0, 100, "Loc 1", userId, "active", new byte[0]);
    dbManager.createAd("Ad 2", "Desc 2", 1, 1, 200, "Loc 2", userId, "active", new byte[0]);
    dbManager.createAd("Ad 3", "Desc 3", 2, 0, 300, "Loc 3", userId, "draft", new byte[0]);

    // Выполнение
    List<Ad> userAds = dbManager.getAdsByUserId(userId);

    // Проверка
    assertNotNull(userAds);
    assertEquals(3, userAds.size());

    // Проверяем что все объявления принадлежат правильному пользователю
    for (Ad ad : userAds) {
      assertEquals(userId, ad.getUserId());
    }
  }

  @Test
  void testUpdateAd() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("updater@mipt.ru", "Updater", "pass", null, null, null);
    long adId = dbManager.createAd("Old Title", "Old Desc", 0, 0, 100, "Old Loc", userId, "active", new byte[0]);

    // Выполнение - обновляем объявление
    boolean updated = dbManager.updateAd(adId, "New Title", "New Desc", 1, 1, 200, "New Loc", "archived");

    // Проверка
    assertTrue(updated);

    // Проверяем изменения в базе
    Ad updatedAd = dbManager.getAdById(adId);
    assertNotNull(updatedAd);
    assertEquals("New Title", updatedAd.getTitle());
    assertEquals("New Desc", updatedAd.getDescription());
    assertEquals(1, updatedAd.getCategory());
    assertEquals(1, updatedAd.getCondition());
    assertEquals(200, updatedAd.getPrice());
    assertEquals("New Loc", updatedAd.getLocation());
    assertEquals("archived", updatedAd.getStatus());
  }

  @Test
  void testDeleteAd() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("deleter@mipt.ru", "Deleter", "pass", null, null, null);
    long adId = dbManager.createAd("To Delete", "Desc", 0, 0, 100, "Loc", userId, "active", new byte[0]);

    // Проверяем что объявление существует
    assertNotNull(dbManager.getAdById(adId));

    // Выполнение - удаляем объявление
    boolean deleted = dbManager.deleteAd(adId);

    // Проверка
    assertTrue(deleted);
    assertNull(dbManager.getAdById(adId));
  }

  @Test
  void testIncrementAdViewCount() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("viewer@mipt.ru", "Viewer", "pass", null, null, null);
    long adId = dbManager.createAd("View Ad", "Desc", 0, 0, 100, "Loc", userId, "active", new byte[0]);

    // Начальное состояние
    Ad initialAd = dbManager.getAdById(adId);
    assertEquals(0, initialAd.getViewCount());

    // Выполнение - увеличиваем счетчик просмотров
    boolean incremented = dbManager.incrementAdViewCount(adId);

    // Проверка
    assertTrue(incremented);

    Ad updatedAd = dbManager.getAdById(adId);
    assertEquals(1, updatedAd.getViewCount());
  }

  @Test
  void testUpdateUserRating() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("rating@mipt.ru", "Rating User", "pass", null, null, null);

    // Выполнение - обновляем рейтинг
    boolean updated = dbManager.updateUserRating(userId, 4.5);

    // Проверка
    assertTrue(updated);

    User user = dbManager.getUserById(userId);
    assertEquals(4.5, user.getRating());
  }

  @Test
  void testUpdateUserCoins() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("coins@mipt.ru", "Coins User", "pass", null, null, null);

    // Выполнение - обновляем монеты
    boolean updated = dbManager.updateUserCoins(userId, 150);

    // Проверка
    assertTrue(updated);

    User user = dbManager.getUserById(userId);
    assertEquals(150, user.getCoins());
  }

  @Test
  void testSearchAdsByTitle() throws SQLException {
    // Подготовка
    long userId = dbManager.createUser("searcher@mipt.ru", "Searcher", "pass", null, null, null);

    dbManager.createAd("iPhone for sale", "Good phone", 0, 0, 10000, "Moscow", userId, "active", new byte[0]);
    dbManager.createAd("MacBook Pro", "Laptop", 0, 1, 50000, "SPB", userId, "active", new byte[0]);
    dbManager.createAd("Samsung Phone", "Android", 0, 0, 8000, "Moscow", userId, "active", new byte[0]);
    dbManager.createAd("Clothes", "T-shirt", 1, 0, 500, "Moscow", userId, "active", new byte[0]);

    // Выполнение - ищем по ключевому слову
    List<Ad> phoneAds = dbManager.searchAdsByTitle("phone");

    // Проверка
    assertNotNull(phoneAds);
    assertEquals(2, phoneAds.size()); // iPhone и Samsung Phone

    for (Ad ad : phoneAds) {
      assertTrue(ad.getTitle().toLowerCase().contains("phone"));
    }
  }
}

 */