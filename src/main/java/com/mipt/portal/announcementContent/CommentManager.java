package com.mipt.portal.announcementContent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentManager {

  private Long advertisementId;
  private String text;

  public Comment create() throws SQLException {
    String sql = "INSERT INTO comments (ad_id, user_id, user_name, content) VALUES (?, ?, ?, ?)";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      Long userId = getUserIdFromAd(advertisementId);
      String author = getAuthorFromUserId(userId);

      pstmt.setLong(1, advertisementId);
      pstmt.setLong(2, userId);
      pstmt.setString(3, author);  // ← Добавляем user_name
      pstmt.setString(4, text);
      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      rs.next();
      Long generatedCommentId = rs.getLong(1);

      return new Comment(generatedCommentId, author, text, LocalDateTime.now(), advertisementId);
    }
  }

  public static Comment read(Long commentId) throws SQLException {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM comments WHERE id = ?")) {

      pstmt.setLong(1, commentId);
      ResultSet rs = pstmt.executeQuery();
      rs.next();

      String author = rs.getString("user_name");

      return new Comment(
          rs.getLong("id"),
          author,
          rs.getString("content"),
          rs.getTimestamp("created_at").toLocalDateTime(),
          rs.getLong("ad_id")
      );
    }
  }


  public static void update(Long commentId, String newText) throws SQLException {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "UPDATE comments SET content = ? WHERE id = ?")) {

      pstmt.setString(1, newText);
      pstmt.setLong(2, commentId);
      pstmt.executeUpdate();
    }
  }


  public static boolean delete(Long commentId) throws SQLException {
    String sql = "DELETE FROM comments WHERE id = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setLong(1, commentId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    }
  }


  private Long getUserIdFromAd(Long advertisementId) throws SQLException {
    String sql = "SELECT user_id FROM ads WHERE id = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setLong(1, advertisementId);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return rs.getLong("user_id");
    }
  }


  private static String getAuthorFromUserId(Long userId) throws SQLException {
    String sql = "SELECT name FROM users WHERE id = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setLong(1, userId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("name");
      }
      return "Unknown User";
    }
  }


  private static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5433/myproject",
        "myuser",
        "mypassword"
    );
  }
}