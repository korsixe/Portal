package com.mipt.portal;

import com.mipt.portal.announcement.AdvertisementStatus;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

  @Test
  void testAnnouncementCreation() {
    Announcement ad = new Announcement(
        "Test Title",
        "Test Description",
        Category.ELECTRONICS,
        Condition.USED,
        1000,
        "Test Location",
        1L
    );

    assertNotNull(ad);
    assertEquals("Test Title", ad.getTitle());
    assertEquals(Category.ELECTRONICS, ad.getCategory());
    assertEquals(AdvertisementStatus.DRAFT, ad.getStatus()); // По умолчанию черновик
  }

  @Test
  void testAdvertisementStatus() {
    assertEquals("Черновик", AdvertisementStatus.DRAFT.getDisplayName());
    assertEquals("Активно", AdvertisementStatus.ACTIVE.getDisplayName());
    assertEquals("На модерации", AdvertisementStatus.UNDER_MODERATION.getDisplayName());
    assertEquals("Архив", AdvertisementStatus.ARCHIVED.getDisplayName());
    assertEquals("Удалено", AdvertisementStatus.DELETED.getDisplayName());
  }

  @Test
  void testCategoryEnum() {
    assertEquals("Электроника", Category.ELECTRONICS.getDisplayName());
    assertEquals("Одежда", Category.CLOTHING.getDisplayName());
    assertEquals("Книги", Category.BOOKS.getDisplayName());
    assertNotNull(Category.getByNumber(1));
    assertEquals(Category.ELECTRONICS, Category.getByNumber(1));
    assertEquals(Category.OTHER, Category.getByNumber(11)); // Для неверного номера
  }

  @Test
  void testConditionEnum() {
    assertEquals("Новое", Condition.NEW.getDisplayName());
    assertEquals("б/у", Condition.USED.getDisplayName());
    assertNotNull(Condition.getByNumber(1));
    assertEquals(Condition.NEW, Condition.getByNumber(2));
  }

  @Test
  void testAnnouncementPriceFormatting() {
    Announcement ad1 = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, -1, "Loc", 1L);
    Announcement ad2 = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, 0, "Loc", 1L);
    Announcement ad3 = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, 500, "Loc", 1L);

    assertTrue(ad1.toString().contains("договорная"));
    assertTrue(ad2.toString().contains("бесплатно"));
    assertTrue(ad3.toString().contains("500 руб."));
  }

  @Test
  void testAnnouncementStatusWorkflow() {
    Announcement ad = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, 1000, "Loc", 1L);

    // Изначально черновик
    assertEquals(AdvertisementStatus.DRAFT, ad.getStatus());
    assertTrue(ad.isDraft());
    assertTrue(ad.canBeEdited());

    // Можно отправить на модерацию
    assertDoesNotThrow(() -> ad.sendToModeration());
    assertEquals(AdvertisementStatus.UNDER_MODERATION, ad.getStatus());
    assertTrue(ad.requiresModeration());

    // Можно активировать
    assertDoesNotThrow(() -> ad.activate());
    assertEquals(AdvertisementStatus.ACTIVE, ad.getStatus());
    assertTrue(ad.isActive());
    assertTrue(ad.isVisibleToPublic());

    // Можно архивировать
    assertDoesNotThrow(() -> ad.archive());
    assertEquals(AdvertisementStatus.ARCHIVED, ad.getStatus());
    assertTrue(ad.canBeEdited()); // Архив еще можно редактировать
  }

  @Test
  void testAnnouncementTags() {
    Announcement ad = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, 1000, "Loc", 1L);

    // Добавление тегов
    ad.addTag("тег1");
    ad.addTag("тег2");
    ad.addTag("тег3");

    assertTrue(ad.getTags().contains("тег1"));
    assertTrue(ad.getTags().contains("тег2"));
    assertTrue(ad.getTags().contains("тег3"));
    assertEquals(3, ad.getTagsCount());

    // Удаление тега
    ad.removeTag("тег2");
    assertFalse(ad.getTags().contains("тег2"));
    assertEquals(2, ad.getTagsCount());

    // Очистка тегов
    ad.clearTags();
    assertTrue(ad.getTags().isEmpty());
    assertEquals(0, ad.getTagsCount());
  }

  @Test
  void testAnnouncementViewCount() {
    Announcement ad = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, 1000, "Loc", 1L);

    assertEquals(0, ad.getViewCount());

    ad.incrementViewCount();
    assertEquals(1, ad.getViewCount());

    ad.incrementViewCount();
    ad.incrementViewCount();
    assertEquals(3, ad.getViewCount());
  }

  @Test
  void testAnnouncementTimestamps() {
    Announcement ad = new Announcement("Test", "Desc", Category.OTHER, Condition.USED, 1000, "Loc", 1L);

    assertNotNull(ad.getCreatedAt());
    assertNotNull(ad.getUpdatedAt());
    assertTrue(ad.getCreatedAt().equals(ad.getUpdatedAt()) ||
        ad.getCreatedAt().isBefore(ad.getUpdatedAt()));

    // При изменении статуса updatedAt должен обновиться
    Instant originalUpdatedAt = ad.getUpdatedAt();
    ad.sendToModeration();

    assertTrue(ad.getUpdatedAt().isAfter(originalUpdatedAt));
  }

  @Test
  void testAnnouncementCopyConstructor() {
    Announcement original = new Announcement("Original", "Desc", Category.ELECTRONICS, Condition.NEW, 5000, "Moscow", 1L);
    original.setSubcategory("Ноутбуки");
    original.addTag("техника");
    original.incrementViewCount();

    Announcement copy = new Announcement(original);

    assertEquals(original.getTitle(), copy.getTitle());
    assertEquals(original.getCategory(), copy.getCategory());
    assertEquals(original.getSubcategory(), copy.getSubcategory());
    assertEquals(original.getPrice(), copy.getPrice());
    assertEquals(original.getViewCount(), copy.getViewCount());
    assertEquals(original.getUserId(), copy.getUserId());
    assertNotNull(copy.getTags());
  }
}