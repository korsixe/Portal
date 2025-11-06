package com.mipt.portal.announcementContent.сlasses.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.*;

@Data
@AllArgsConstructor
public class DeleteComment {
  private Long commentId;

  public boolean deleteFromDatabase() throws SQLException {
    String sql = "DELETE FROM comments WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/portal_db",
      "portal_user",
      "portal_password");
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setLong(1, commentId);
      int affectedRows = pstmt.executeUpdate(); // ← ВЫПОЛНЯЕМ ЗАПРОС!

      return affectedRows > 0;
    }
  }
}