# Portal
Проект "Portal" - проект для улучшения жизни студентов МФТИ

### Запуск БД с драйвером
``` 
wget https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```
```
docker-compose up -d
```
### Заполнения бд с нуля:
```
mvn compile exec:java -Dexec.mainClass="com.mipt.portal.database.TestData"
```
### Запуск фронта
```
mvn jetty:run
```
### Главная страница PORTAL: 
```
http://localhost:8080/portal/
```
#### Вход для модераторов:
```
http://localhost:8080/portal/moderator/login-moderator.jsp
```