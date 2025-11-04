package com.mipt.portal;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.sql.*;

public class DatabaseManager implements IDatabaseManager {

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
}