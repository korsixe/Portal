package com.mipt.portal.announcementContent.сlasses.comment;


import java.sql.*;

public class UpdateComment {
  public static void update(Long commentId, String newText) throws SQLException {
    Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/portal_db",
      "portal_user",
      "portal_password");

    PreparedStatement pstmt = conn.prepareStatement("UPDATE comments SET content = ? WHERE id = ?");
    pstmt.setString(1, newText);
    pstmt.setLong(2, commentId);

    pstmt.executeUpdate();

    pstmt.close();
    conn.close();
  }
}
