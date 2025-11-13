package com.mipt.portal.users.repository;

import com.mipt.portal.database.DatabaseConnection;
import com.mipt.portal.users.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    public UserRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<User> save(User user) {
        String sql = "INSERT INTO users (email, name, password, address, study_program, course, rating, coins) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getStudyProgram());
            pstmt.setInt(6, user.getCourse());
            pstmt.setDouble(7, user.getRating());
            pstmt.setInt(8, user.getCoins());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                        return Optional.of(user);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении пользователя: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пользователя по email: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пользователя " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET email = ?, name = ?, password = ?, address = ?, study_program = ?, course = ?, rating = ?, coins = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getStudyProgram());
            pstmt.setInt(6, user.getCourse());
            pstmt.setDouble(7, user.getRating());
            pstmt.setInt(8, user.getCoins());
            pstmt.setLong(9, user.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении пользователя: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех пользователей: " + e.getMessage());
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setAddress(rs.getString("address"));
        user.setStudyProgram(rs.getString("study_program"));
        user.setCourse(rs.getInt("course"));
        user.setRating(rs.getDouble("rating"));
        user.setCoins(rs.getInt("coins"));
        user.setAdList(new ArrayList<>());

        return user;
    }
}
