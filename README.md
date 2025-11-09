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
```
java -cp "target/classes:postgresql-42.6.0.jar" com.mipt.portal.Main
```

```
mvn tomcat7:run
```

```
http://localhost:8080/portal/
```

```
sudo netstat -tulpn | grep :8080
```