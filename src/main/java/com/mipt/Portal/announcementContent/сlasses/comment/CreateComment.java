package com.mipt.Portal.announcementContent.сlasses.comment;

import com.mipt.Portal.announcementContent.dto.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CreateComment {
  private Long advertisementId;
  private String author;
  private String text;
  private Long parentCommentId;

  public Comment saveToDatabase() throws SQLException {
    String sql = "INSERT INTO comments (ad_id, user_id, content) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/portal_db",
      "portal_user",
      "portal_password");
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      Long userId = getUserIdFromAd(advertisementId);

      pstmt.setLong(1, advertisementId);
      pstmt.setLong(2, userId);
      pstmt.setString(3, text);
      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      rs.next();
      Long commentId = rs.getLong(1);

      return new Comment(commentId, author, text, LocalDateTime.now(), advertisementId);
    }
  }

  private Long getUserIdFromAd(Long advertisementId) throws SQLException {
    String sql = "SELECT user_id FROM ads WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/portal_db",
      "portal_user",
      "portal_password");
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setLong(1, advertisementId);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return rs.getLong("user_id");
    }
  }
}