package com.mipt.portal.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class AdManager implements IAdManager {

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

  @Override
  public Ad createAd(String email) {
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

    List<String> chooseCondition = new ArrayList<>(Arrays.asList("б/у", "Новое", "Не работает"));
    System.out.println("Введите состояние объявления: ");
    for (int i = 0; i < chooseCondition.size(); ++i) {
      System.out.println((i + 1) + " " + chooseCondition.get(i));
    }

    int type = readIntInRange(scanner, "Ваш выбор: ", 1, chooseCondition.size());
    String condition = chooseCondition.get(type - 1);

    int price = -1;
    System.out.println("Цена договорная? Введите число: 1 - Да, 2 - Нет");
    type = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    if (type == 2) {
      price = readIntInRange(scanner, "Введите стоимость товара (от 0 до 1000000000): ", 0, 1000000000);
    }

    System.out.println("Объявление почти готово. Если хотите опубликовать, выведите 1. Если хотите оставить черновиком, выведите 2");
    type = readIntInRange(scanner, "Ваш выбор: ", 1, 2);

    String status = "Активно";
    if (type == 2) {
      status = "Черновик";
    }

    Ad ad = new Ad(title, description, category, condition, price, location, email, status);
    // и тут в БД добавили
    scanner.close();
    System.out.println("Объявление добавлено!");
    System.out.println(ad.toString());

    return ad;
  }

  @Override
  public Ad editAd(Ad ad) {
    // Логика редактирования объявления
    return ad;
  }

  @Override
  public void deleteAd(int adId) {
    // Логика удаления объявления
  }

  @Override
  public Ad getAd(int adId) {
    // Логика получения объявления
    return null;
  }
}