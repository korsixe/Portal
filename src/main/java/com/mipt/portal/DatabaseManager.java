package com.mipt.portal;

import com.mipt.portal.IDatabaseManager;
import com.mipt.portal.service.User;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager implements IDatabaseManager, UserManager {

    private Connection connection;

    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createTables() {
        try {
            String sql = readSqlFile("sql/create_tables.sql");
            executeSql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertData() {
        try {
            String sql = readSqlFile("sql/insert_data.sql");
            executeSql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String sql = readSqlFile("sql/insert_data_ad.sql");
            executeSql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readSqlFile(String filename) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            Path path = Paths.get("~Portal/src/main/resources/sql/" + filename);
            if (Files.exists(path)) {
                return Files.readString(path);
            }

            throw new RuntimeException("Файл не найден: " + filename);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + filename, e);
        }
    }

    private void executeSql(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String[] sqlCommands = sql.split(";");
            for (String command : sqlCommands) {
                if (!command.trim().isEmpty()) {
                    statement.execute(command.trim());
                }
            }
        }
    }


    @Override
    public boolean addUser(User user) {
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
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении пользователя: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении пользователя: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении пользователя: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean updateUser(User user) {
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
    public boolean deleteUser(long id) {
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
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении пользователей: " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке email: " + e.getMessage());
        }
        return false;
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
        user.setAdList(new ArrayList<>()); // можно дополнить загрузкой объявлений

        return user;
    }

}