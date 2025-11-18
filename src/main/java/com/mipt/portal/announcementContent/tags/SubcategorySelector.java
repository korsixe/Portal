package com.mipt.portal.announcementContent.tags;

import lombok.RequiredArgsConstructor;
import java.sql.*;
import java.util.*;
import com.mipt.portal.database.DatabaseConnection;

@RequiredArgsConstructor
public class SubcategorySelector {

  public List<Map<String, Object>> getSubcategoriesByCategory(Long categoryId) throws SQLException {
    List<Map<String, Object>> subcategories = new ArrayList<>();
    String sql = "SELECT id, name FROM categories WHERE parent_id = ? ORDER BY name";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, categoryId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Map<String, Object> subcategory = new HashMap<>();
          subcategory.put("id", rs.getLong("id"));
          subcategory.put("name", rs.getString("name"));
          subcategories.add(subcategory);
        }
      }
    }
    return subcategories;
  }

  public boolean isServiceSubcategory(Long subcategoryId) throws SQLException {
    String sql = """
            SELECT c.is_service FROM categories sc 
            JOIN categories c ON sc.parent_id = c.id 
            WHERE sc.id = ?
            """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, subcategoryId);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() && rs.getBoolean("is_service");
      }
    }
  }
}