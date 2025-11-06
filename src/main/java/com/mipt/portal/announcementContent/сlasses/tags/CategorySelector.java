package com.mipt.portal.announcementContent.сlasses.tags;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CategorySelector {
  private List<Map<String, Object>> categories;
  private Scanner scanner = new Scanner(System.in);

  public CategorySelector() {
    Gson gson = new Gson();
    InputStream tagsStream = getClass().getClassLoader().getResourceAsStream("subcategoriesObjects.json");
    Map<String, Object> response = gson.fromJson(new InputStreamReader(tagsStream), Map.class);
    categories = (List<Map<String, Object>>) response.get("categoryTags");
  }

  public Map<String, Object> selectCategory() {
    System.out.println("Выберите категорию:");
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i).get("categoryName"));
    }

    while (true) {
      int choice = Integer.parseInt(scanner.nextLine());
      if (choice > 0 && choice <= categories.size()) {
        return categories.get(choice - 1);
      }
      System.out.println("Введите 1 из номеров");
    }
  }
}
