package com.mipt.portal.announcement;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Scanner;
import java.io.*;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdsService implements IAdsService {

  private IAdsRepository dbManager;

  @Override
  public Announcement createAd(long userId) {
    Scanner scanner;
    try {
      scanner = new Scanner(new InputStreamReader(System.in, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      scanner = new Scanner(System.in);
    }

    System.out.println("Введите заголовок объявления:");
    String title = scanner.nextLine();

    System.out.println("Введите описание объявления:");
    String description = scanner.nextLine();

    // Выбор категории из enum
    System.out.println("Выберите категорию объявления:");
    Category.displayCategories();
    int categoryChoice = readIntInRange(scanner, "Ваш выбор: ", 1, Category.values().length);
    scanner.nextLine(); // очистка буфера
    Category category = Category.getByNumber(categoryChoice);

    System.out.println("Введите местоположение объявления:");
    String location = scanner.nextLine();

    System.out.println("Выберите состояние товара:");
    Condition.displayConditions();
    int conditionChoice = readIntInRange(scanner, "Ваш выбор: ", 1, Condition.values().length);
    Condition condition = Condition.getByNumber(conditionChoice);

    int price = -1;
    System.out.println("Цена договорная? Введите число: 1 - Да, 2 - Нет");
    int type = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    if (type == 2) {
      price = readIntInRange(scanner, "Введите стоимость товара (от 0 до 1000000000): ", 0,
          1000000000);
    }

    // Создаем объявление как черновик
    Announcement ad = new Announcement(title, description, category, condition, price, location,
        userId);

    // Предлагаем выбрать действие с объявлением
    System.out.println("\nВыберите действие с объявлением:");
    System.out.println("1. Опубликовать (отправить на модерацию)");
    System.out.println("2. Сохранить как черновик");

    int actionChoice = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    try {
      switch (actionChoice) {
        case 1:
          ad.sendToModeration();
          System.out.println("Объявление отправлено на модерацию!");
          break;
        case 2:
          System.out.println("Объявление сохранено как черновик!");
          break;
      }
    } catch (IllegalStateException e) {
      System.out.println("Ошибка: " + e.getMessage());
      System.out.println("Объявление сохранено как черновик.");
    }

    // Предлагаем добавить теги
    System.out.println("\nХотите добавить теги к объявлению? (1 - Да, 2 - Нет)");
    int addTagsChoice = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    if (addTagsChoice == 1) {
      //addTagsInteractive(scanner, ad); - Лиза Орлова
    }

    try {
      long adId = dbManager.saveAd(ad);
      ad.setId(adId); // ураа, у нас есть id нашего объявления
      System.out.println("✅ Объявление создано с ID: " + adId);
    } catch (SQLException e) {
      System.err.println("❌ Ошибка сохранения: " + e.getMessage());
    }

    System.out.println("\nОбъявление успешно создано!");
    System.out.println(ad.toString());

    return ad;
  }

  @Override
  public Announcement editAd(Announcement ad) throws SQLException {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new InputStreamReader(System.in, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      scanner = new Scanner(System.in);
    }

    System.out.println("\n=== Редактирование объявления ===");
    System.out.println("Текущие данные:");
    System.out.println(ad.toString());

    boolean continueEditing = true;
    Announcement originalAd = new Announcement(ad); // Сохраняем оригинальную версию

    while (continueEditing) {
      System.out.println("\nЧто вы хотите изменить?");
      System.out.println("1. Заголовок");
      System.out.println("2. Описание");
      System.out.println("3. Категорию");
      System.out.println("4. Подкатегорию");
      System.out.println("5. Местоположение");
      System.out.println("6. Состояние товара");
      System.out.println("7. Цену");
      System.out.println("8. Теги");
      System.out.println("9. Статус объявления");
      System.out.println("10. Сохранить изменения");
      System.out.println("0. Отменить изменения и выйти");

      int choice = readIntInRange(scanner, "Ваш выбор: ", 0, 10);

      switch (choice) {
        case 1:
          editTitle(scanner, ad);
          break;
        case 2:
          editDescription(scanner, ad);
          break;
        case 3:
          editCategory(scanner, ad);
          break;
        case 4:
          //editSubcategory(scanner, ad);
          break;
        case 5:
          editLocation(scanner, ad);
          break;
        case 6:
          editCondition(scanner, ad);
          break;
        case 7:
          editPrice(scanner, ad);
          break;
        case 8:
          //manageTags(scanner, ad);
          break;
        case 9:
          editStatus(scanner, ad);
          break;
        case 10:
          // Сохраняем изменения в БД
          ad.setUpdatedAt(Instant.now());
          dbManager.updateAd(ad);
          System.out.println("✅ Изменения сохранены успешно!");
          return ad;
        case 0:
          System.out.println("Редактирование отменено. Возврат к исходной версии.");
          return originalAd;
      }
      System.out.println("\nТекущие данные после изменений:");
      System.out.println(ad.toString());
    }

    return ad;
  }

  private void editTitle(Scanner scanner, Announcement ad) {
    System.out.println("Текущий заголовок: " + ad.getTitle());
    System.out.print("Введите новый заголовок: ");
    scanner.nextLine(); // очистка буфера
    String newTitle = scanner.nextLine();
    if (!newTitle.trim().isEmpty()) {
      ad.setTitle(newTitle);
      System.out.println("Заголовок обновлен.");
    } else {
      System.out.println("Заголовок не может быть пустым. Изменения не сохранены.");
    }
  }

  private void editDescription(Scanner scanner, Announcement ad) {
    System.out.println("Текущее описание: " + ad.getDescription());
    System.out.print("Введите новое описание: ");
    scanner.nextLine(); // очистка буфера
    String newDescription = scanner.nextLine();
    if (!newDescription.trim().isEmpty()) {
      ad.setDescription(newDescription);
      System.out.println("Описание обновлено.");
    } else {
      System.out.println("Описание не может быть пустым. Изменения не сохранены.");
    }
  }

  private void editCategory(Scanner scanner, Announcement ad) {
    System.out.println("Текущая категория: " + ad.getCategory().getDisplayName());
    System.out.println("Выберите новую категорию:");
    Category.displayCategories();
    int categoryChoice = readIntInRange(scanner, "Ваш выбор: ", 1, Category.values().length);
    Category newCategory = Category.getByNumber(categoryChoice);
    ad.setCategory(newCategory);
    System.out.println("Категория изменена на: " + newCategory.getDisplayName());
  }

  private void editLocation(Scanner scanner, Announcement ad) {
    System.out.println("Текущее местоположение: " + ad.getLocation());
    System.out.print("Введите новое местоположение: ");
    scanner.nextLine(); // очистка буфера
    String newLocation = scanner.nextLine();
    if (!newLocation.trim().isEmpty()) {
      ad.setLocation(newLocation);
      System.out.println("Местоположение обновлено.");
    } else {
      System.out.println("Местоположение не может быть пустым. Изменения не сохранены.");
    }
  }

  private void editCondition(Scanner scanner, Announcement ad) {
    System.out.println("Текущее состояние: " + ad.getCondition().getDisplayName());
    System.out.println("Выберите новое состояние:");
    Condition.displayConditions();

    int conditionChoice = readIntInRange(scanner, "Ваш выбор: ", 1, Condition.values().length);
    Condition newCondition = Condition.getByNumber(conditionChoice);
    ad.setCondition(newCondition);
    System.out.println("Состояние изменено на: " + newCondition.getDisplayName());
  }

  private void editPrice(Scanner scanner, Announcement ad) {
    System.out.println("Текущая цена: " + (ad.getPrice() == -1 ? "договорная" :
        ad.getPrice() == 0 ? "бесплатно" : ad.getPrice() + " руб."));

    System.out.println("Выберите тип цены:");
    System.out.println("1. Договорная");
    System.out.println("2. Бесплатно");
    System.out.println("3. Указать цену");

    int priceType = readIntInRange(scanner, "Ваш выбор: ", 1, 3);

    switch (priceType) {
      case 1:
        ad.setPrice(-1);
        System.out.println("Цена установлена: договорная");
        break;
      case 2:
        ad.setPrice(0);
        System.out.println("Цена установлена: бесплатно");
        break;
      case 3:
        int newPrice = readIntInRange(scanner, "Введите цену (от 1 до 1000000000): ", 1,
            1000000000);
        ad.setPrice(newPrice);
        System.out.println("Цена установлена: " + newPrice + " руб.");
        break;
    }
  }

  private void editStatus(Scanner scanner, Announcement ad) {
    System.out.println("Текущий статус: " + ad.getStatus().getDisplayName());
    System.out.println("Выберите действие со статусом:");

    // Показываем только доступные действия для текущего статуса
    if (ad.canBeEdited()) {
      if (ad.getStatus().canBeSentToModeration()) {
        System.out.println("1. Отправить на модерацию");
      }
      if (ad.getStatus().canBeArchived()) {
        System.out.println("2. Архивировать");
      }
      if (ad.getStatus().canBeRestored()) {
        System.out.println("3. Восстановить (сделать активным)");
      }
      System.out.println("4. Сохранить как черновик");
    }

    System.out.println("0. Отмена");

    int statusChoice = readIntInRange(scanner, "Ваш выбор: ", 0, 4);

    try {
      switch (statusChoice) {
        case 1:
          ad.sendToModeration();
          System.out.println("Объявление отправлено на модерацию!");
          break;
        case 2:
          ad.archive();
          System.out.println("Объявление архивировано!");
          break;
        case 3:
          ad.restore();
          System.out.println("Объявление восстановлено!");
          break;
        case 4:
          ad.saveAsDraft();
          System.out.println("Объявление сохранено как черновик!");
          break;
        case 0:
          System.out.println("Изменение статуса отменено.");
          break;
      }
    } catch (IllegalStateException e) {
      System.out.println("❌ Ошибка: " + e.getMessage());
    }
  }

  @Override
  public Announcement deleteAd(long adId) {
    try {
      Announcement ad = dbManager.getAdById(adId);
      if (ad == null) {
        System.out.println("❌ Объявление с ID " + adId + " не найдено");
        return null;
      }

      // Подтверждение удаления
      Scanner scanner = new Scanner(System.in);
      System.out.println("Вы действительно хотите удалить объявление?");
      System.out.println(ad.toString());
      System.out.print("Введите 'да' для подтверждения: ");
      String confirmation = scanner.nextLine();

      if ("да".equalsIgnoreCase(confirmation)) {
        boolean deleted = dbManager.deleteAd(adId);
        if (deleted) {
          //dbManager.removeAdFromUserList(ad.getUserId(), adId); - удаляем у юзера
          System.out.println("✅ Объявление успешно удалено");
          return ad;
        } else {
          System.out.println("❌ Не удалось удалить объявление");
          return null;
        }
      } else {
        System.out.println("❌ Удаление отменено");
        return null;
      }

    } catch (SQLException e) {
      System.err.println("❌ Ошибка при удалении объявления: " + e.getMessage());
      return null;
    }
  }

  @Override
  public Announcement getAd(long adId) {
    try {
      Announcement ad = dbManager.getAdById(adId);
      if (ad != null) {
        System.out.println("✅ Объявление найдено:");
        System.out.println(ad.toString());
        ad.incrementViewCount(); // увеличиваем просмотры
      } else {
        System.out.println("❌ Объявление с ID " + adId + " не найдено");
      }
      return ad;
    } catch (SQLException e) {
      System.err.println("❌ Ошибка при получении объявления: " + e.getMessage());
      return null;
    }
  }

  // Функция для безопасного ввода числа
  private int readInt(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      if (scanner.hasNextInt()) {
        return scanner.nextInt();
      } else {
        System.out.println("❌ Ошибка: введите целое число!");
        scanner.next(); // очищаем некорректный ввод
      }
    }
  }

  // Функция для безопасного ввода числа в диапазоне
  private int readIntInRange(Scanner scanner, String prompt, int min, int max) {
    while (true) {
      int value = readInt(scanner, prompt);
      if (value >= min && value <= max) {
        return value;
      } else {
        System.out.println("❌ Ошибка: число должно быть от " + min + " до " + max + "!");
      }
    }
  }
}