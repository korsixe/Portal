package com.mipt.portal.announcementContent.tags;

import lombok.RequiredArgsConstructor;
import java.sql.*;
import java.util.*;
import com.mipt.portal.database.DatabaseConnection;

@RequiredArgsConstructor
public class CategorySelector {

  public List<Map<String, Object>> getAllCategories() throws SQLException {
    List<Map<String, Object>> categories = new ArrayList<>();
    String sql = "SELECT id, name, is_service FROM categories WHERE parent_id IS NULL ORDER BY name";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Map<String, Object> category = new HashMap<>();
        category.put("id", rs.getLong("id"));
        category.put("name", rs.getString("name"));
        category.put("isService", rs.getBoolean("is_service"));
        categories.add(category);
      }
    }
    return categories;
  }
}