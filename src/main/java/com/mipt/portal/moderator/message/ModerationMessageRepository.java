package com.mipt.portal.moderator.message;

import com.mipt.portal.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModerationMessageRepository {

    private Connection connection;

    public ModerationMessageRepository() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
        CREATE TABLE IF NOT EXISTS moderation_messages (
            id BIGSERIAL PRIMARY KEY,
            ad_id BIGINT NOT NULL,
            moderator_email TEXT NOT NULL,
            action TEXT NOT NULL,
            reason TEXT,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (ad_id) REFERENCES ads(id) ON DELETE CASCADE
        )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Таблица moderation_messages гарантированно создана");
        } catch (SQLException e) {
            System.err.println("❌ Критическая ошибка при создании таблицы: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean saveModerationMessage(Long adId, String moderatorEmail, String action, String reason) {
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO moderation_messages (ad_id, moderator_email, action, reason) " +
                    "VALUES (?, ?, ?, ?)";

            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, adId);
            stmt.setString(2, moderatorEmail);
            stmt.setString(3, action);
            stmt.setString(4, reason);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при сохранении сообщения модератора: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    public List<ModerationMessage> getMessagesByAdId(Long adId) {
        List<ModerationMessage> messages = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM moderation_messages WHERE ad_id = ? ORDER BY created_at DESC";
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, adId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                ModerationMessage message = new ModerationMessage();
                message.setId(rs.getLong("id"));
                message.setAdId(rs.getLong("ad_id"));
                message.setModeratorEmail(rs.getString("moderator_email"));
                message.setAction(rs.getString("action"));
                message.setReason(rs.getString("reason"));
                message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                messages.add(message);
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при получении сообщений модератора: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, rs);
        }

        return messages;
    }

    public List<ModerationMessage> getMessagesByModerator(String moderatorEmail) {
        List<ModerationMessage> messages = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM moderation_messages WHERE moderator_email = ? ORDER BY created_at DESC";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, moderatorEmail);

            rs = stmt.executeQuery();

            while (rs.next()) {
                ModerationMessage message = new ModerationMessage();
                message.setId(rs.getLong("id"));
                message.setAdId(rs.getLong("ad_id"));
                message.setModeratorEmail(rs.getString("moderator_email"));
                message.setAction(rs.getString("action"));
                message.setReason(rs.getString("reason"));
                message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                messages.add(message);
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при получении сообщений модератора: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, rs);
        }

        return messages;
    }

    public boolean deleteMessage(Long messageId) {
        PreparedStatement stmt = null;

        try {
            String sql = "DELETE FROM moderation_messages WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, messageId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении сообщения модератора: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Проверяет существование таблицы moderation_messages
     */
    public boolean checkTableExists() {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "moderation_messages", null);
            return tables.next();
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при проверке существования таблицы: " + e.getMessage());
            return false;
        }
    }


    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            // Не закрываем connection здесь, так как он используется повторно
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}