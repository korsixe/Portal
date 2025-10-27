package com.mipt.portal;

import com.mipt.portal.announcement.Announcement;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class DatabaseIntegrationTest {

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  private Connection connection;
  private DatabaseManager dbManager;

  @BeforeEach
  void setUp() throws Exception {
    connection = DriverManager.getConnection(
        postgres.getJdbcUrl(),
        postgres.getUsername(),
        postgres.getPassword()
    );
    dbManager = new DatabaseManager(connection);

    dbManager.createTables();
  }

  @Test
  void testCreateTables() throws Exception {
    try (Statement stmt = connection.createStatement()) {
      var resultSet = stmt.executeQuery("SELECT COUNT(*) FROM users");
      assertTrue(resultSet.next());
    }
  }

  @Test
  void testInsertAndRetrieveUser() throws Exception {
    // Вставляем тестовые данные
    dbManager.insertData();

    // Ищем пользователя по email
    Long userId = dbManager.getUserIdByEmail("shabunina.ao@phystech.edu");

    assertNotNull(userId);
    assertTrue(userId > 0);
  }

  @Test
  void testSaveAndRetrieveAd() throws Exception {
    dbManager.insertData();

    Long userId = dbManager.getUserIdByEmail("shabunina.ao@phystech.edu");

    var ad = new Announcement("Test Ad", "Test Description",
        com.mipt.portal.announcement.Category.ELECTRONICS,
        com.mipt.portal.announcement.Condition.USED, 1000, "Test Location", userId);

    long adId = dbManager.saveAd(ad);
    assertTrue(adId > 0);

    var retrievedAd = dbManager.getAdById(adId);
    assertNotNull(retrievedAd);
    assertEquals("Test Ad", retrievedAd.getTitle());
    assertEquals(1000, retrievedAd.getPrice());
  }

  @AfterEach
  void tearDown() throws Exception {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}