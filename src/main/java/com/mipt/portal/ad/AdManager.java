package com.mipt.portal.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class AdManager implements IAdManager {

  @Override
  public Ad createAd(String email) {
    Scanner scanner = null;
    try {
      // Устанавливаем правильную кодировку для Scanner
      scanner = new Scanner(new InputStreamReader(System.in, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      scanner = new Scanner(System.in); // fallback
    }

    System.out.println("Введите заголовок объявления:");
    String title = scanner.nextLine();

    System.out.println("Введите описание объявления:");
    String description = scanner.nextLine();

    // Выбор категории из enum
    System.out.println("Выберите категорию объявления:");
    Category.displayCategories();
    int categoryChoice = scanner.nextInt();
    scanner.nextLine(); // очистка буфера
    Category category = Category.getByNumber(categoryChoice);

    System.out.println("Введите местоположение объявления:");
    String location = scanner.nextLine();

    List<String> chooseCondition = new ArrayList<>(Arrays.asList("б/у", "Новое", "Не работает"));
    System.out.println("Введите состояние объявления: ");
    for (int i = 0; i < chooseCondition.size(); ++i) {
      System.out.println((i + 1) + " " + chooseCondition.get(i));
    }
    int type = scanner.nextInt();

    while (type < 1 || chooseCondition.size() < type) {
      System.out.println("Введеный тип не корректен, попробуйте снова");
      System.out.println("Введите состояние объявления: ");
      for (int i = 0; i < chooseCondition.size(); ++i) {
        System.out.println((i + 1) + " " + chooseCondition.get(i));
      }
      type = scanner.nextInt();
    }
    String condition = chooseCondition.get(type - 1);

    int price = -1;
    System.out.println("Цена договорная? Введите число: 1 - Да, 2 - Нет");
    type = scanner.nextInt();
    while (type != 1 && type != 2) {
      System.out.println("Вы написали неверное число.\n");
      System.out.println("Цена договорная? Введите число: 1 - Да, 2 - Нет");
      type = scanner.nextInt();
    }

    if (type == 2) {
      System.out.println("Введите стоимость товара, цена от 0 до 1000000000");
      price = scanner.nextInt();
      while (price < 0 || price > 1000000000) {
        System.out.println("Введите стоимость товара, цена от 0 до 1000000000");
        price = scanner.nextInt();
      }
    }

    System.out.println(
        "Объявление почти готово. Если хотите опубликовать, выведите 1. Если хотите оставить черновиком, выведите 2");
    type = scanner.nextInt();
    while (type != 1 && type != 2) {
      System.out.println("Вы написали неверное число.\n");
      System.out.println(
          "Объявление почти готово. Если хотите опубликовать, выведите 1. Если хотите оставить черновиком, выведите 2");
      type = scanner.nextInt();
    }

    String status = "Активно";
    if (type == 2) {
      status = "Черновик";
    }

    Ad ad = new Ad(title, description, category, condition, price, location, email, status);
    // а тут взяли и в БД добавили
    scanner.close();
    System.out.println("Объявление добавлено!");
    System.out.println(ad.toString());

    return ad;
  }

  @Override
  public Ad editAd(Ad ad) {
    return ad;
  }

  @Override
  public void deleteAd(int adId) {
  }

  @Override
  public Ad getAd(int adId) {
    return null;
  }
}