package com.mipt.portal.moderator;

import com.mipt.portal.moderator.Moderator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModeratorRepository {
    private final Connection connection;

    public ModeratorRepository(Connection connection) {
        this.connection = connection;
    }

    public Optional<Moderator> save(Moderator moderator) {
        String sql = "INSERT INTO moderators (email, name, password, permissions) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, moderator.getEmail());
            pstmt.setString(2, moderator.getName());
            pstmt.setString(3, moderator.getPassword());

            // Преобразуем список permissions в массив PostgreSQL
            Array permissionsArray = connection.createArrayOf("text",
                    moderator.getPermissions().toArray(new String[0]));
            pstmt.setArray(4, permissionsArray);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        moderator.setId(generatedKeys.getLong(1));
                        return Optional.of(moderator);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении модератора: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Moderator> findByEmail(String email) {
        String sql = "SELECT * FROM moderators WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToModerator(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске модератора: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Moderator> findById(long id) {
        String sql = "SELECT * FROM moderators WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToModerator(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске модератора: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Moderator> findAll() {
        List<Moderator> moderators = new ArrayList<>();
        String sql = "SELECT * FROM moderators";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                moderators.add(mapResultSetToModerator(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении модераторов: " + e.getMessage());
        }
        return moderators;
    }

    private Moderator mapResultSetToModerator(ResultSet rs) throws SQLException {
        Moderator moderator = new Moderator();
        moderator.setId(rs.getLong("id"));
        moderator.setEmail(rs.getString("email"));
        moderator.setName(rs.getString("name"));
        moderator.setPassword(rs.getString("password"));

        Array permissionsArray = rs.getArray("permissions");
        if (permissionsArray != null) {
            String[] permissions = (String[]) permissionsArray.getArray();
            for (String permission : permissions) {
                moderator.getPermissions().add(permission);
            }
        }

        return moderator;
    }






}
