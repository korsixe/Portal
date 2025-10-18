import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Ad {

  private String title;                // Заголовок
  private String description;          // Описание
  private String category;             // Категория
  private String condition;            // Состояние
  private boolean negotiablePrice;     // Цена договорная
  private int price;                   // Цена
  private String location;             // Местоположение
  private int idUser;                  // Пока храним id user
  private String status;               // Активно, Архив, Черновик

  // Конструктор
  public Ad(String title, String description, String category, String condition,
      boolean negotiablePrice,
      int price, String location, int idUser, String status) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.condition = condition;
    this.negotiablePrice = negotiablePrice;
    this.price = price;
    this.location = location;
    this.idUser = idUser;
    this.status = status;
  }

  @Override
  public String toString() {
    return "Заголовок: " + title +
        ", Описание: " + description +
        ", Категория: " + category +
        ", Состояние: " + condition +
        ", Договорная цена? " + negotiablePrice +
        ", Цена: " + price + " руб." +
        ", Местоположение: " + location +
        ", Создатель " + idUser;
  }


  static void createNewAd(int idUser) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Введите заголовок объявления:");
    String title = scanner.nextLine();

    System.out.println("Введите описание объявления:");
    String description = scanner.nextLine();

    System.out.println("Введите категорию объявления:");
    String category = scanner.nextLine();

    System.out.println("Введите местоположение объявления:"); //Предварительно храним в строках
    String location = scanner.nextLine();

    List<String> chooseCondition = new ArrayList<>(Arrays.asList("б/y", "Новое", "Не работает"));
    System.out.println("Введите состояние объявления: ");
    for (int i = 0; i < chooseCondition.size(); ++i) {
      System.out.println(i + 1 + " " + chooseCondition.get(i));
    }
    int type = scanner.nextInt();

    while (type < 1 || chooseCondition.size() < type) {
      System.out.println("Введеный тип не корректен, попробуйте снова");
      System.out.println("Введите состояние объявления: ");
      for (int i = 0; i < chooseCondition.size(); ++i) {
        System.out.println(i + 1 + " " + chooseCondition.get(i));
      }

      type = scanner.nextInt();
    }
    String condition = chooseCondition.get(type - 1);

    boolean negotiablePrice = true;
    int price = 0;
    System.out.println("Цена договорная? Введите число: 1 - Да, 2 - Нет");
    type = scanner.nextInt();                     // Можно поломать, если вводить не число :(
    while (type != 1 && type != 2) {
      System.out.println("Вы написали неверное число.\n");
      System.out.println("Цена договорная? Введите число: 1 - Да, 2 - Нет");
      type = scanner.nextInt();
    }

    if (type == 2) {
      negotiablePrice = false;
      System.out.println("Введите стоимость товара, цена от 0 до 1000000000");
      price = scanner.nextInt();                     // Можно поломать, если вводить не число :(
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

    Ad ad = new Ad(title, description, category, condition, negotiablePrice, price, location,
        idUser, status);

    /* добавляем объявление в БД
    if (type == 1) {

    } else if (type == 2) {

    }
    */

    scanner.close();
    System.out.println("Объявление добавлено!");
    System.out.println(ad.toString());
  }

  public static void main(String[] args) {
    createNewAd(0);
  }
}
