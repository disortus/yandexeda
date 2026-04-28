# Demo Spring Boot (Учебный проект)

Этот проект — учебная заготовка, чтобы понять **как работает Spring Boot в реальном API** и как правильно раскладывать код по слоям:

- `controller`
- `dto`
- `entity`
- `repository`
- `service`
- `validator`
- `exception`
- `middleware`
- `config`
- `utils`

---

## 1) Что уже реализовано

### API
- `GET /users` — получить список пользователей
- `GET /users/{id}` — получить пользователя по id
- `POST /users` — создать пользователя

### Инфраструктура
- JPA/Hibernate + H2 (по умолчанию)
- Возможность переключиться на PostgreSQL через env-переменные
- Валидация входных данных (`spring-boot-starter-validation`)
- Swagger UI (`/swagger-ui.html`)
- Глобальная обработка ошибок (`@RestControllerAdvice`)
- Middleware-логирование запросов (`OncePerRequestFilter`)

---

## 2) Как запустить

### Запуск приложения
```bash
./gradlew bootRun
```

### Запуск тестов
```bash
./gradlew test --no-daemon
```

---

## 3) Подключение к БД

По умолчанию используется in-memory H2:

- URL: `jdbc:h2:mem:testdb`
- H2 console: `http://localhost:8080/h2-console`

Можно подключить PostgreSQL через переменные окружения:

```bash
DB_URL=jdbc:postgresql://localhost:5432/demo_db
DB_USER=postgres
DB_PASS=postgres
DB_DRIVER=org.postgresql.Driver
./gradlew bootRun
```

Дополнительные переменные пула соединений:

- `DB_POOL_MAX_SIZE` (по умолчанию `10`)
- `DB_POOL_MIN_IDLE` (по умолчанию `2`)

---

## 4) Быстрый тест API

### Создание пользователя
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

### Получение всех пользователей
```bash
curl http://localhost:8080/users
```

---

## 5) Где читать документацию по слоям

Подробные объяснения находятся рядом с кодом:

- `src/main/java/com/example/demo/DOCUMENTATION.md` — общая архитектура Spring Boot
- `src/main/java/com/example/demo/controller/DOCUMENTATION.md`
- `src/main/java/com/example/demo/dto/DOCUMENTATION.md`
- `src/main/java/com/example/demo/entity/DOCUMENTATION.md`
- `src/main/java/com/example/demo/repository/DOCUMENTATION.md`
- `src/main/java/com/example/demo/service/DOCUMENTATION.md`
- `src/main/java/com/example/demo/validator/DOCUMENTATION.md`
- `src/main/java/com/example/demo/exception/DOCUMENTATION.md`
- `src/main/java/com/example/demo/middleware/DOCUMENTATION.md`
- `src/main/java/com/example/demo/config/DOCUMENTATION.md`
- `src/main/java/com/example/demo/utils/DOCUMENTATION.md`

---

## 6) Что изучать в первую очередь

1. `Application.java` + `@SpringBootApplication`
2. `UserController` (вход в систему)
3. `UserService` (бизнес-логика)
4. `UserRepository` + `User` (доступ к БД)
5. `UserCreateRequest` и `@Valid`
6. `GlobalExceptionHandler`
7. `RequestLoggingFilter`

После этого переходи к `config` и `utils`

---

## 7) Большое обновление учебного проекта (JWT, роли, security, профили БД, тесты)

Ниже перечислено, что добавлено сверх базового CRUD-примера, чтобы проект был максимально полезен для изучения Spring Boot с нуля и до production-like подхода.

### 7.1 Аутентификация и роли

Добавлены auth endpoints:

- `POST /auth/register` — регистрация пользователя (роль по умолчанию `CUSTOMER`)
- `POST /auth/login` — вход и получение JWT

Роли:

- `CUSTOMER`
- `ADMIN`
- `COURIER`

Роль хранится в enum и в БД, а права доступа ограничиваются через Spring Security и `@PreAuthorize`.

### 7.2 Swagger / OpenAPI

Теперь API полноценно документировано:

- схемы request/response DTO,
- ошибки (`ApiErrorResponse`),
- security scheme `bearerAuth` для JWT,
- теги и описания всех ручек.

Swagger UI:

- `http://localhost:8080/swagger-ui.html`

Подробный гайд по Swagger:

- `./DOCUMENTATION.md`

### 7.3 Конфигурация через env

Добавлено:

- `.env.example` как шаблон,
- загрузка `.env` через `spring.config.import`,
- привязка параметров к `@ConfigurationProperties` (`JwtProperties`, `SecurityProperties`).

### 7.4 Профили БД

В `application.yaml` теперь разделение по профилям:

- `dev` → PostgreSQL
- `prod` → PostgreSQL (облако)
- `test` → SQLite

Это сделано, чтобы тесты не зависели от доступности облачной БД.

### 7.5 Security hardening

В проекте настроены:

- stateless JWT security,
- BCrypt password hashing,
- role-based authorization,
- CORS из конфигурации,
- security headers,
- единый JSON-ответ для 401/403.

Подробно по security:

- `src/main/java/com/example/demo/security/DOCUMENTATION.md`

### 7.6 Тестовая стратегия

Добавлены разные виды тестов:

- unit (service/validator/utils),
- web (контракт контроллера),
- integration (auth flow, authorization, repository),
- context load.

Подробно по тестам:

- `src/test/DOCUMENTATION.md`

---

## 8) Важные дополнительные документы

- `DOCUMENTATION.md` — подробная методика Swagger/OpenAPI
- `src/main/resources/DOCUMENTATION.md` — как устроен `application.yaml`, профили и env
- `src/main/java/com/example/demo/enums/DOCUMENTATION.md` — зачем enums и как их развивать
- `src/main/java/com/example/demo/security/DOCUMENTATION.md` — глубокое объяснение security-слоя

---

## 9) Примечание по старым разделам README

Если ты видишь в ранних разделах упоминание H2 как "по умолчанию" — ориентируйся на обновлённую конфигурацию:  
для тестов используется SQLite, а для dev/prod — PostgreSQL по профилям.
