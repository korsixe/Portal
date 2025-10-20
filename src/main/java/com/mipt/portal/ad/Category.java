package com.mipt.portal.ad;

public enum Category {
  ELECTRONICS("Электроника"),
  CLOTHING("Одежда"),
  SPORTS("Спорт"),
  BOOKS("Книги"),
  FURNITURE("Мебель"),
  AUTOMOTIVE("Автотовары"),
  BEAUTY("Красота"),
  TOYS("Игрушки"),
  INSTRUMENTS("Инструменты"),
  OTHER("Другое");

  private final String displayName;

  Category(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static void displayCategories() {
    System.out.println("Доступные категории:");
    for (int i = 0; i < values().length; i++) {
      System.out.println((i + 1) + ". " + values()[i].getDisplayName());
    }
  }

  public static Category getByNumber(int number) {
    if (number > 0 && number <= values().length) {
      return values()[number - 1];
    }
    return OTHER;
  }
}