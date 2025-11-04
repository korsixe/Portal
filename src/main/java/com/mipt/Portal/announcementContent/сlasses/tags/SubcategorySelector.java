package com.mipt.Portal.announcementContent.сlasses.tags;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SubcategorySelector {
  private Scanner scanner = new Scanner(System.in);

  public Map<String, Object> selectSubcategory(Map<String, Object> category) {
    System.out.println("Выберите подкатегорию для: " + category.get("categoryName"));
    List<Map<String, Object>> subcategories = (List<Map<String, Object>>) category.get("subcategoryTags");

    for (int i = 0; i < subcategories.size(); i++) {
      System.out.println((i + 1) + ". " + subcategories.get(i).get("subcategoryName"));
    }

    while (true) {
      int choice = Integer.parseInt(scanner.nextLine());
      if (choice > 0 && choice <= subcategories.size()) {
        return subcategories.get(choice - 1);
      }
      System.out.println("Введите 1 из предложенных номеров");
    }
  }
}
