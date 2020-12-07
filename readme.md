**Переменные окружения:**
Для запуска в dev режиме нужно указать dev профиль для Spring.
```
-Dspring.profiles.active=dev
```

**Переменные окружения:**
* JDBC_USERNAME: пользователь БД
  * Example: postgres
* JDBC_PASSWORD: пароль БД
  * Example: 123456
* JDBC_URL: URL для JDBC драйвера
  * Example: jdbc:postgresql://localhost:5432/postgres