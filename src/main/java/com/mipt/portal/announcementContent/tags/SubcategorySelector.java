package com.mipt.portal.announcementContent.tags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.mipt.portal.database.DatabaseConnection;

public class SubcategorySelector {
  private Scanner scanner = new Scanner(System.in);

  public Map<String, Object> selectSubcategory(Map<String, Object> category, Long adId) {
    System.out.println("Выберите подкатегорию для: " + category.get("categoryName"));
    List<Map<String, Object>> subcategories = (List<Map<String, Object>>) category.get("subcategoryTags");

    for (int i = 0; i < subcategories.size(); i++) {
      System.out.println((i + 1) + ". " + subcategories.get(i).get("subcategoryName"));
    }

    while (true) {
      int choice = Integer.parseInt(scanner.nextLine());
      if (choice > 0 && choice <= subcategories.size()) {
        Map<String, Object> selectedSubcategory = subcategories.get(choice - 1);

        saveSubcategoryToDatabase(adId, selectedSubcategory);

        return selectedSubcategory;
      }
      System.out.println("Введите 1 из предложенных номеров");
    }
  }

  private void saveSubcategoryToDatabase(Long adId, Map<String, Object> subcategory) {
    String sql = "UPDATE ads SET subcategory = ? WHERE id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      String subcategoryName = (String) subcategory.get("subcategoryName");
      statement.setString(1, subcategoryName);
      statement.setLong(2, adId);
      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Ошибка при сохранении субкатегории в БД: " + e.getMessage());
    }
  }
}