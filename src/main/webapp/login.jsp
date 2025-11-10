<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Вход в систему</title>
</head>
<body>
<h1>Вход в Portal</h1>

<form action="login" method="post">
    <div>
        <label>Email:</label>
        <input type="email" name="email" required>
    </div>
    <div>
        <label>Пароль:</label>
        <input type="password" name="password" required>
    </div>
    <button type="submit">Войти</button>
</form>

<p>Или <a href="index.jsp">вернуться на главную</a></p>
</body>
</html>