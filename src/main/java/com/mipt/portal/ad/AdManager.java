package com.mipt.portal.ad;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class AdManager implements IAdManager {

  @Override
  public Ad createAd(long userId) {
    Scanner scanner = null;
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

    System.out.println(
        "Объявление почти готово. Если хотите опубликовать, выведите 1. Если хотите оставить черновиком, выведите 2");
    type = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    String status = "Активно";
    if (type == 2) {
      status = "Черновик";
    }

    Ad ad = new Ad(title, description, category, condition, price, location, userId, status);
    // и тут в БД добавили

    System.out.println("Объявление добавлено!");
    System.out.println(ad.toString());

    return ad;
  }

  @Override
  public Ad editAd(Ad ad) {
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

    while (continueEditing) {
      System.out.println("\nЧто вы хотите изменить?");
      System.out.println("1. Заголовок");
      System.out.println("2. Описание");
      System.out.println("3. Категорию");
      System.out.println("4. Местоположение");
      System.out.println("5. Состояние товара");
      System.out.println("6. Цену");
      System.out.println("7. Статус объявления");
      System.out.println("0. Завершить редактирование");

      int choice = readIntInRange(scanner, "Ваш выбор: ", 0, 7);


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
          editLocation(scanner, ad);
          break;
        case 5:
          editCondition(scanner, ad);
          break;
        case 6:
          editPrice(scanner, ad);
          break;
        case 7:
          editStatus(scanner, ad);
          break;
        case 0:
          continueEditing = false;
          System.out.println("Редактирование завершено.");
          break;
      }

      if (choice > 0) {
        ad.setUpdatedAt(Instant.now());
      }

      if (continueEditing) {
        System.out.println("\nТекущие данные после изменений:");
        System.out.println(ad.toString());
      }
    }

    return ad;
  }


  private void editTitle(Scanner scanner, Ad ad) {
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

  private void editDescription(Scanner scanner, Ad ad) {
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

  private void editCategory(Scanner scanner, Ad ad) {
    System.out.println("Текущая категория: " + ad.getCategory().getDisplayName());
    System.out.println("Выберите новую категорию:");
    Category.displayCategories();
    int categoryChoice = readIntInRange(scanner, "Ваш выбор: ", 1, Category.values().length);
    Category newCategory = Category.getByNumber(categoryChoice);
    ad.setCategory(newCategory);
    System.out.println("Категория изменена на: " + newCategory.getDisplayName());
  }

  private void editLocation(Scanner scanner, Ad ad) {
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

  private void editCondition(Scanner scanner, Ad ad) {
    System.out.println("Текущее состояние: " + ad.getCondition().getDisplayName());
    System.out.println("Выберите новое состояние:");
    Condition.displayConditions();

    int conditionChoice = readIntInRange(scanner, "Ваш выбор: ", 1, Condition.values().length);
    Condition newCondition = Condition.getByNumber(conditionChoice);
    ad.setCondition(newCondition);
    System.out.println("Состояние изменено на: " + newCondition.getDisplayName());
  }

  private void editPrice(Scanner scanner, Ad ad) {
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

  private void editStatus(Scanner scanner, Ad ad) {
    System.out.println("Текущий статус: " + ad.getStatus());
    System.out.println("Выберите новый статус:");
    System.out.println("1. Активно");
    System.out.println("2. Черновик");

    int statusChoice = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    switch (statusChoice) {
      case 1:
        ad.setStatus("Активно");
        System.out.println("Статус изменен на: Активно");
        break;
      case 2:
        ad.setStatus("Черновик");
        System.out.println("Статус изменен на: Черновик");
        break;
    }
  }


  @Override
  public Ad deleteAd(long adId) {
    // Логика удаления объявления
    return null;
  }

  @Override
  public Ad getAd(long adId) {
    // Логика получения объявления
    return null;
  }

  @Override
  public List<Ad> getAds(long userId) {
    // Логика получения объявлений пользователя
    return null;
  }


  // Функция для безопасного ввода числа
  private int readInt(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      if (scanner.hasNextInt()) {
        return scanner.nextInt();
      } else {
        System.out.println("Ошибка: введите целое число!");
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
        System.out.println("Ошибка: число должно быть от " + min + " до " + max + "!");
      }
    }
  }
}