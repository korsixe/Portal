package com.mipt.portal.announcement;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Boolean.TRUE;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Announcement {

  private long id;                     // id объявления
  private String title;                // Заголовок
  private String description;          // Описание
  private Category category;           // Категория - используем enum
  private String subcategory;          // Подкатегория
  private Condition condition;         // Состояние товара
  private int price;                   // Цена - если цена "-1" - договорная, если "0" - бесплатно, ">0" - цена
  private String location;             // Местоположение
  private long userId;                 // Храним id пользователя
  private AdvertisementStatus status;  // Статус объявления
  private Instant createdAt;           // дата создания объявления
  private Instant updatedAt;           // дата последнего обновления
  private Integer viewCount;           // счетчик просмотров
  private List<File> photos;           // ссылки на фото
  private List<String> tags;           // теги объявления
  private Integer tagsCount;           // количество тегов
  private Long messageId;              // комментраий от Модера, если не объявление не прошло проверку
  // Конструкторы

  public Announcement(Announcement other) {
    this.id = other.id;
    this.title = other.title;
    this.description = other.description;
    this.category = other.category;
    this.subcategory = other.subcategory;
    this.condition = other.condition;
    this.price = other.price;
    this.location = other.location;
    this.userId = other.userId;
    this.status = other.status;
    this.createdAt = other.createdAt;
    this.updatedAt = other.updatedAt;
    this.viewCount = other.viewCount;
    this.photos = other.photos != null ? new ArrayList<>(other.photos) : null;
    this.tags = other.tags != null ? new ArrayList<>(other.tags) : null;
    this.tagsCount = other.tagsCount;
    this.messageId = other.messageId;
  }

  public Announcement(String title, String description, Category category, Condition condition,
      int price, String location, long userId) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.condition = condition;
    this.price = price;
    this.location = location;
    this.userId = userId;
    this.status = AdvertisementStatus.DRAFT; // По умолчанию черновик
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.viewCount = 0;
    this.photos = new ArrayList<>();
    this.tags = new ArrayList<>();
    this.tagsCount = 0;
    this.messageId = null;
  }

  // Бизнес-методы для работы со статусами

  public void sendToModeration() {
    status = AdvertisementStatus.UNDER_MODERATION;
    updatedAt = Instant.now();
  }

  public void activate() {
    if (status == AdvertisementStatus.UNDER_MODERATION) {
      status = AdvertisementStatus.ACTIVE;
      updatedAt = Instant.now();
    } else {
      throw new IllegalStateException(
          "Можно активировать только из статуса модерации. Текущий статус: "
              + status.getDisplayName());
    }
  }

  public void archive() {
    if (status.canBeArchived()) {
      status = AdvertisementStatus.ARCHIVED;
      updatedAt = Instant.now();
    } else {
      throw new IllegalStateException(
          "Невозможно архивировать из статуса: " + status.getDisplayName());
    }
  }

  public void saveAsDraft() {
    if (status.canBeEdited()) {
      status = AdvertisementStatus.DRAFT;
      updatedAt = Instant.now();
    } else {
      throw new IllegalStateException(
          "Невозможно сохранить как черновик из статуса: " + status.getDisplayName());
    }
  }

  public void rejectModeration() {
    if (status == AdvertisementStatus.UNDER_MODERATION) {
      status = AdvertisementStatus.DRAFT;
      updatedAt = Instant.now();
    } else {
      throw new IllegalStateException(
          "Можно отклонить только из статуса модерации. Текущий статус: "
              + status.getDisplayName());
    }
  }

  public void delete() {
    status = AdvertisementStatus.DELETED;
    updatedAt = Instant.now();
  }

  // Методы для работы с тегами, жду объединения с другой частью проекта

  public void addTag(String tag) {
    if (tags == null) {
      tags = new ArrayList<>();
    }
    if (!tags.contains(tag)) {
      tags.add(tag);
      tagsCount = tags.size();
      updatedAt = Instant.now();
    }
  }

  public void removeTag(String tag) {
    if (tags != null) {
      tags.remove(tag);
      tagsCount = tags.size();
      updatedAt = Instant.now();
    }
  }

  public void clearTags() {
    if (tags != null) {
      tags.clear();
      tagsCount = 0;
      updatedAt = Instant.now();
    }
  }

  // Методы для работы с фото

  public void addPhoto(File photo) {
    if (photos == null) {
      photos = new ArrayList<>();
    }
    photos.add(photo);
    updatedAt = Instant.now();
  }

  public void removePhoto(File photo) {
    if (photos != null) {
      photos.remove(photo);
      updatedAt = Instant.now();
    }
  }

  public void clearPhotos() {
    if (photos != null) {
      photos.clear();
      updatedAt = Instant.now();
    }
  }

  public void incrementViewCount() {
    if (viewCount == null) {
      viewCount = 0;
    }
    this.viewCount++;
  }

  public boolean isActive() {
    return status.isActive();
  }

  public boolean isVisibleToPublic() {
    return status.isVisibleToPublic();
  }

  public boolean canBeEdited() {
    return true; // Всегда разрешено редактирование
  }

  public boolean isDraft() {
    return status.isDraft();
  }

  public boolean requiresModeration() {
    return status.isModerationRequired();
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        .withZone(ZoneId.systemDefault());

    return "Заголовок: " + title +
        ", Описание: " + (description != null ?
        description.substring(0, Math.min(description.length(), 100)) + "..." : "нет") +
        ", Категория: " + category.getDisplayName() +
        (subcategory != null ? ", Подкатегория: " + subcategory : "") +
        ", Состояние: " + condition.getDisplayName() +
        ", Цена: " + formatPrice() +
        ", Местоположение: " + location +
        ", Создатель: " + userId +
        ", Статус: " + status.getDisplayName() +
        ", Просмотров: " + viewCount +
        (tagsCount != null && tagsCount > 0 ? ", Тегов: " + tagsCount : "") +
        (photos != null ? ", Фото: " + photos.size() : "") +
        ", Создано: " + (createdAt != null ? formatter.format(createdAt) : "не указано") +
        ", Обновлено: " + (updatedAt != null ? formatter.format(updatedAt) : "не указано");
  }

  private String formatPrice() {
    if (price == -1) {
      return "договорная";
    } else if (price == 0) {
      return "бесплатно";
    } else {
      return String.format("%,d руб.", price);
    }
  }
}