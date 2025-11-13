package com.mipt.portal.announcement;

import com.mipt.portal.users.service.UserService;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data

public class AdsService implements IAdsService {

  private AdsRepository adsRepository;
  private UserService userService;

  public AdsService() {
    try {
      this.adsRepository = new AdsRepository();
      this.userService = new UserService();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Announcement createAd(long userId, String title, String description, Category category,
      String subcategory, Condition condition, int price, String location, List<File> photos,
      List<String> tags, AdvertisementStatus action) throws SQLException {

    Announcement ad = new Announcement(title, description, category, condition, price, location, userId);
    ad.setSubcategory(subcategory);
    ad.setPhotos(photos);
    ad.setTags(tags);
    ad.setStatus(action);
    ad.setId(adsRepository.saveAd(ad));
    userService.addAnnouncementId(ad.getUserId(), ad.getId());
    return ad;
  }

  @Override
  public Long getUserIdByEmail(String email) throws SQLException {
    return adsRepository.getUserIdByEmail(email);
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
          adsRepository.updateAd(ad);
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
      Announcement ad = adsRepository.getAdById(adId);
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
        boolean deleted = adsRepository.deleteAd(adId);
        if (deleted) {
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
      Announcement ad = adsRepository.getAdById(adId);
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

  @Override
  public void sendToModeration(Announcement ad) throws SQLException {
    ad.sendToModeration();
    adsRepository.updateAd(ad);
  }

  @Override
  public void activate(Announcement ad) throws SQLException {
    ad.activate();
    adsRepository.updateAd(ad);
  }

  @Override
  public void archive(Announcement ad) throws SQLException {
    ad.archive();
    adsRepository.updateAd(ad);
  }

  @Override
  public void saveAsDraft(Announcement ad) throws SQLException {
    ad.saveAsDraft();
    adsRepository.updateAd(ad);
  }

  @Override
  public void rejectModeration(Announcement ad) throws SQLException {
    ad.rejectModeration();
    adsRepository.updateAd(ad);
  }

  @Override
  public void delete(Announcement ad) throws SQLException {
    ad.delete();
    adsRepository.updateAd(ad);
  }


  public List<Long> getModerAdIds() throws SQLException {
    return adsRepository.getModerAdIds();
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