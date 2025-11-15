<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.mipt.portal.users.User" %>
<%
    // Проверяем, что это multipart запрос
    if (!ServletFileUpload.isMultipartContent(request)) {
        response.sendRedirect("create-ad.jsp?error=not_multipart");
        return;
    }

    // Настраиваем фабрику для загрузки файлов
    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(1024 * 1024); // 1MB threshold
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setFileSizeMax(1024 * 1024 * 5); // 5MB max per file
    upload.setSizeMax(1024 * 1024 * 20); // 20MB max total

    // Мапы для хранения данных формы
    Map<String, String> formFields = new HashMap<>();
    List<byte[]> photosList = new ArrayList<>();

    try {
        // Парсим multipart запрос
        List<FileItem> items = upload.parseRequest(request);

        for (FileItem item : items) {
            if (item.isFormField()) {
                // Обычные текстовые поля формы
                formFields.put(item.getFieldName(), item.getString("UTF-8"));
                System.out.println("Form field: " + item.getFieldName() + " = " + item.getString("UTF-8"));
            } else {
                // Файловые поля
                if ("photos".equals(item.getFieldName()) && item.getSize() > 0) {
                    byte[] fileData = item.get();
                    photosList.add(fileData);
                    System.out.println("Uploaded photo: " + item.getName() + ", size: " + fileData.length + " bytes");
                }
            }
        }

        // Валидация обязательных полей
        if (formFields.get("title") == null || formFields.get("title").trim().isEmpty()) {
            response.sendRedirect("create-ad.jsp?error=missing_title");
            return;
        }

        // Получаем пользователя из сессии
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        // Сохраняем в базу данных
        try (Connection conn = getConnection()) {
            // Обрабатываем цену
            int priceValue = -1; // по умолчанию договорная
            String priceType = formFields.get("priceType");
            if ("fixed".equals(priceType) && formFields.get("price") != null && !formFields.get("price").trim().isEmpty()) {
                priceValue = Integer.parseInt(formFields.get("price"));
            } else if ("free".equals(priceType)) {
                priceValue = 0;
            }

            String sql = "INSERT INTO ads (title, description, price, category, condition, location, " +
                    "subcategory, tags, photos, user_id, view_count, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, 0, NOW(), NOW())";

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Устанавливаем значения для каждого параметра
                stmt.setString(1, formFields.get("title"));
                stmt.setString(2, formFields.get("description"));
                stmt.setInt(3, priceValue);

                // Обрабатываем категорию (преобразуем строку в int)
                String categoryStr = formFields.get("category");
                int categoryValue = 0; // значение по умолчанию
                if (categoryStr != null && !categoryStr.isEmpty()) {
                    try {
                        categoryValue = Integer.parseInt(categoryStr);
                    } catch (NumberFormatException e) {
                        // Если не число, используем enum ordinal
                        try {
                            com.mipt.portal.announcement.Category category = com.mipt.portal.announcement.Category.valueOf(categoryStr);
                            categoryValue = category.ordinal();
                        } catch (IllegalArgumentException ex) {
                            categoryValue = 0;
                        }
                    }
                }
                stmt.setInt(4, categoryValue);

                // Обрабатываем состояние (преобразуем строку в int)
                String conditionStr = formFields.get("condition");
                int conditionValue = 0; // значение по умолчанию
                if (conditionStr != null && !conditionStr.isEmpty()) {
                    try {
                        conditionValue = Integer.parseInt(conditionStr);
                    } catch (NumberFormatException e) {
                        // Если не число, используем enum ordinal
                        try {
                            com.mipt.portal.announcement.Condition condition = com.mipt.portal.announcement.Condition.valueOf(conditionStr);
                            conditionValue = condition.ordinal();
                        } catch (IllegalArgumentException ex) {
                            conditionValue = 0;
                        }
                    }
                }
                stmt.setInt(5, conditionValue);

                stmt.setString(6, formFields.get("location"));
                stmt.setString(7, formFields.get("subcategory"));
                stmt.setString(8, formFields.get("tags"));

                // Сначала сохраняем объявление без фото (photos будет null)
                stmt.setArray(9, null);
                stmt.setLong(10, user.getId());

                // Выполняем запрос
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    // Получаем ID созданного объявления
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            Long adId = generatedKeys.getLong(1);
                            
                            // Теперь обновляем фото, если они есть
                            if (!photosList.isEmpty()) {
                                // PostgreSQL BYTEA[] требует специальной обработки
                                // Используем альтернативный подход с массивом через строку
                                try {
                                    // Создаем массив bytea через PostgreSQL функции
                                    // Используем подход с множественными параметрами
                                    StringBuilder sqlBuilder = new StringBuilder();
                                    sqlBuilder.append("UPDATE ads SET photos = ARRAY[");
                                    for (int i = 0; i < photosList.size(); i++) {
                                        if (i > 0) sqlBuilder.append(", ");
                                        sqlBuilder.append("?::bytea");
                                    }
                                    sqlBuilder.append("] WHERE id = ?");
                                    
                                    String updatePhotosSql = sqlBuilder.toString();
                                    try (PreparedStatement updatePhotosStmt = conn.prepareStatement(updatePhotosSql)) {
                                        // Устанавливаем каждый bytea параметр
                                        for (int i = 0; i < photosList.size(); i++) {
                                            updatePhotosStmt.setBytes(i + 1, photosList.get(i));
                                        }
                                        updatePhotosStmt.setLong(photosList.size() + 1, adId);
                                        int updateResult = updatePhotosStmt.executeUpdate();
                                        System.out.println("Saved " + photosList.size() + " photos to database for ad ID: " + adId + " (rows updated: " + updateResult + ")");
                                    }
                                } catch (SQLException e) {
                                    System.err.println("Error saving photos: " + e.getMessage());
                                    e.printStackTrace();
                                    // Продолжаем выполнение даже если фото не сохранились
                                }
                            }
                            
                            response.sendRedirect("ad-details.jsp?id=" + adId + "&success=true");
                        } else {
                            response.sendRedirect("home.jsp?success=true");
                        }
                    }
                } else {
                    response.sendRedirect("create-ad.jsp?error=db_insert_failed");
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("create-ad.jsp?error=general_error&message=" + e.getMessage());
    }
%>

<%!
    // Метод для получения соединения с БД
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5433/myproject",
                "myuser",
                "mypassword"
        );
    }
%>