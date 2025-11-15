package com.mipt.portal.announcementContent.tags;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mipt.portal.database.DatabaseConnection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TagSelector {
  private Map<String, List<String>> tagValues;
  private Scanner scanner = new Scanner(System.in);

  public TagSelector() {
    Gson gson = new Gson();
    InputStream valuesStream = getClass().getClassLoader().getResourceAsStream("tagValues.json");
    tagValues = gson.fromJson(new InputStreamReader(valuesStream), new TypeToken<Map<String, List<String>>>(){}.getType());
  }

  public List<String> selectTags(Map<String, Object> subcategory) {
    List<String> tags = new ArrayList<>();
    List<Map<String, Object>> tagList = (List<Map<String, Object>>) subcategory.get("tags");

    for (Map<String, Object> tag : tagList) {
      String tagName = (String) tag.get("name");
      System.out.println("\n" + tagName + ":");

      List<String> values = tagValues.get(tagName);
      if (values != null && !values.isEmpty()) {
        for (int i = 0; i < values.size(); i++) {
          System.out.println((i + 1) + ". " + values.get(i));
        }

        while (true) {
          System.out.print("Выберите номер значения: ");
          try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= values.size()) {
              tags.add(tagName + ": " + values.get(choice - 1));
              break;
            } else {
              System.out.println("Неверный номер! Выберите от 1 до " + values.size());
            }
          } catch (NumberFormatException e) {
            System.out.println("Введите число!");
          }
        }
      } else {
        System.out.println("Нет доступных значений для этого тега");
      }
    }
    return tags;
  }

  public boolean saveTagsToDatabase(long adId, List<String> tags) {
    try {
      Connection connection = DatabaseConnection.getConnection();
      String sql = "UPDATE ads SET tags = ?::JSONB, tags_count = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        // Преобразуем список тегов в JSON
        if (tags != null && !tags.isEmpty()) {
          String tagsJson = "[\"" + String.join("\",\"", tags) + "\"]";
          statement.setString(1, tagsJson);
          statement.setInt(2, tags.size());
        } else {
          statement.setNull(1, java.sql.Types.VARCHAR);
          statement.setInt(2, 0);
        }

        statement.setLong(3, adId);

        int affectedRows = statement.executeUpdate();
        return affectedRows > 0;
      }
    } catch (SQLException e) {
      System.err.println("Ошибка при сохранении тегов в базу данных: " + e.getMessage());
      return false;
    }
  }
}