# Portal
Проект "Portal" - проект для улучшения жизни студентов МФТИ

# Компиляция и Запуск
```
mvn clean install
```
```
java -cp target/classes com.mipt.portal.testinguser.Application
```

# Запуск БД с драйвером
``` 
wget https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```
```
docker-compose up -d
```
```
mvn clean compile
```
для заполнения бд с нуля:
```
 java -cp "target/classes:postgresql-42.6.0.jar" com.mipt.portal.database.TestData
```
```
mvn jetty:run
```
Главная страница PORTAL: 
```
http://localhost:8080/portal/
```
Вход для модераторов:
```
http://localhost:8080/portal/moderator/login-moderator.jsp
```


```
sudo netstat -tulpn | grep :8080
```

```
sudo kill -9 "номер процесса"
```
