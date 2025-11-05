package com.mipt.Portal.announcementContent.сlasses.comment;

import com.mipt.Portal.announcementContent.dto.Comment;

import java.sql.*;

public class ReadComment {
  public static Comment read(Long commentId) throws SQLException {
    Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/portal_db",
      "portal_user",
      "portal_password");

    PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM comments WHERE id = ?");
    pstmt.setLong(1, commentId);

    ResultSet rs = pstmt.executeQuery();
    rs.next();

    Comment comment = new Comment(
      rs.getLong("id"),
      "User " + rs.getLong("user_id"),
      rs.getString("content"),
      rs.getTimestamp("created_at").toLocalDateTime(),
      rs.getLong("ad_id")
    );

    rs.close();
    pstmt.close();
    conn.close();

    return comment;
  }
}