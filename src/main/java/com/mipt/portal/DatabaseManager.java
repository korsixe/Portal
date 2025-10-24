package com.mipt.portal;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseManager {

    // Уберите static из методов
    public void createTables() {
      try {
        String sql = readSqlFile("sql/create_tables.sql");
        // ваш код выполнения SQL
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void insertData() {
      try {
        String sql = readSqlFile("insert_data.sql");
        // ваш код выполнения SQL
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private String readSqlFile(String filename) {
      try {
        // Пробуем загрузить как ресурс из classpath
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

        if (inputStream != null) {
          return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Если не нашли как ресурс, пробуем файловую систему
        Path path = Paths.get("~Portal/src/main/resources/sql/" + filename);
        if (Files.exists(path)) {
          return Files.readString(path);
        }

        throw new RuntimeException("Файл не найден: " + filename);

      } catch (IOException e) {
        throw new RuntimeException("Ошибка чтения файла: " + filename, e);
      }
    }
}
