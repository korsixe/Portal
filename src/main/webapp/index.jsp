<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.announcement.AdsService" %>
<%@ page import="com.mipt.portal.announcement.Announcement" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Announcement Portal</title>
    <style>
      body { font-family: Arial, sans-serif; margin: 40px; }
      .header { background: #f0f0f0; padding: 20px; margin-bottom: 20px; }
      .btn { background: #007bff; color: white; padding: 10px 15px; text-decoration: none; border-radius: 4px; }
      .ads-list { margin-top: 20px; }
      .ad-item { border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; }
    </style>
</head>
<body>
<div class="header">
    <h1>Доска объявлений</h1>
    <a href="create-ad.jsp" class="btn">Создать объявление</a>
</div>

<div class="ads-list">
    <h2>Последние объявления</h2>
    <%
        // TODO: Получить список объявлений из AdsService
        // List<Announcement> ads = adsService.getRecentAds();
        // for (Announcement ad : ads) {
    %>
    <div class="ad-item">
        <h3>Заголовок объявления</h3>
        <p>Описание объявления...</p>
        <small>Категория: • Цена: • Дата: </small>
        <a href="ad-details.jsp?id=">Подробнее</a>
    </div>
    <%
        // }
    %>
</div>
</body>
</html>