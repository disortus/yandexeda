# Demo Spring Boot (Учебный проект)

Этот проект — учебная заготовка, чтобы понять **как работает Spring Boot в реальном API** и как правильно раскладывать код по слоям:

- `controller`
- `docs`
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
- `POST /auth/register` — зарегистрировать нового `CUSTOMER` и получить JWT
- `POST /auth/login` — войти по email/password и получить JWT
- `GET /status` — публичный status endpoint без Swagger-аннотаций
- `GET /users/me` — получить текущего пользователя по JWT
- `GET /users` — получить список пользователей (`ADMIN`, `COURIER`)
- `GET /users/{id}` — получить пользователя по id (`ADMIN`, `COURIER`)
- `POST /users` — создать пользователя администратором (`ADMIN`)

### Инфраструктура
- JPA/Hibernate
- PostgreSQL для `dev` и `prod` профилей
- SQLite для `test` профиля (in-memory, без локального `demo-test.db` файла)
- Валидация входных данных (`spring-boot-starter-validation`)
- Swagger UI (`/swagger-ui.html`) и OpenAPI JSON (`/v3/api-docs`)
- Глобальная обработка ошибок (`@RestControllerAdvice`)
- Middleware-логирование запросов (`OncePerRequestFilter`)
- JWT authentication + role-based authorization

---

## 2) Как запустить

### Запуск приложения
```bash
./gradlew bootRun
```

По умолчанию активен профиль `dev`, поэтому для запуска приложения нужна локальная PostgreSQL или заполненный `.env` с `DEV_DB_URL`, `DEV_DB_USER`, `DEV_DB_PASS`. Автотесты PostgreSQL не требуют: они используют профиль `test` и SQLite.

### Запуск тестов
```bash
./gradlew test --no-daemon
```

---

## 3) Подключение к БД

Актуальная конфигурация теперь profile-based:

- `dev` → PostgreSQL для локальной разработки (`DEV_DB_URL`, `DEV_DB_USER`, `DEV_DB_PASS`),
- `prod` → PostgreSQL в облаке (`PROD_DB_URL`, `PROD_DB_USER`, `PROD_DB_PASS`),
- `test` → SQLite in-memory (`TEST_DB_URL=jdbc:sqlite:file:demo-test?mode=memory&cache=shared`).

Пример запуска dev-профиля:

```bash
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

Пример запуска prod-профиля:

```bash
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

Шаблон всех переменных находится в `.env.example`. Реальный `.env` хранится локально и не коммитится.

---

## 4) Быстрый тест API

### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","password":"Qwerty123!"}'
```

### Логин
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"Qwerty123!"}'
```

### Статус приложения
Эта ручка сделана без Swagger-аннотаций, чтобы показать автоматическую генерацию OpenAPI по Spring MVC mapping:

```bash
curl http://localhost:8080/status
```

### Получение текущего пользователя
```bash
curl http://localhost:8080/users/me \
  -H "Authorization: Bearer <accessToken>"
```

### Получение всех пользователей
Доступно только ролям `ADMIN` и `COURIER`:

```bash
curl http://localhost:8080/users \
  -H "Authorization: Bearer <accessToken>"
```

---

## 5) Где читать документацию по слоям

Подробные объяснения находятся рядом с кодом:

- `src/main/java/com/example/demo/DOCUMENTATION.md` — общая архитектура Spring Boot
- `src/main/java/com/example/demo/controller/DOCUMENTATION.md`
- `src/main/java/com/example/demo/docs/SWAGGER_SCHEMAS.md` — подробный разбор Swagger/OpenAPI schemas
- `src/main/java/com/example/demo/dto/DOCUMENTATION.md`
- `src/main/java/com/example/demo/dto/auth/DOCUMENTATION.md`
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

Дополнительно обязательно посмотри:

8. `AuthController` и `AuthService` — регистрация, логин, JWT
9. `SecurityConfig` и `JwtAuthenticationFilter` — как запрос становится authenticated
10. `ApplicationE2ETest` — как проверить реальный HTTP-сценарий целиком

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
- context load,
- e2e через настоящий HTTP server (`ApplicationE2ETest`).

Подробно по тестам:

- `src/test/DOCUMENTATION.md`

---

## 8) Важные дополнительные документы

- `DOCUMENTATION.md` — подробная методика Swagger/OpenAPI
- `src/main/resources/DOCUMENTATION.md` — как устроен `application.yaml`, профили и env
- `src/main/java/com/example/demo/enums/DOCUMENTATION.md` — зачем enums и как их развивать
- `src/main/java/com/example/demo/security/DOCUMENTATION.md` — глубокое объяснение security-слоя

---

## 9) Историческое примечание по БД

В ранних учебных версиях Spring Boot примеров часто используют H2 как встроенную тестовую БД. В этом проекте актуальная конфигурация другая и ближе к твоей задаче:

- для автотестов используется SQLite in-memory,
- для `dev` используется PostgreSQL,
- для `prod` используется PostgreSQL в облаке,
- переключение происходит через Spring profiles и env-переменные.


---

## 10) Что исправлено после аудита предыдущей реализации

При проверке проекта были найдены несколько важных проблем:

1. **Проект не компилировался** после изменения `ApiErrorResponse`: record уже ожидал `fieldErrors` и `ApiErrorCode`, а `GlobalExceptionHandler` создавал старый формат ошибки.
2. **Swagger фактически отдавал 500**: `springdoc-openapi 2.5.0` был несовместим со Spring Boot `4.1.0-RC1` / Spring Framework 7 (`NoSuchMethodError` внутри `/v3/api-docs`). Для учебного проекта это плохая база, поэтому проект переведён на стабильный Spring Boot `3.4.5` и springdoc `2.8.6`.
3. **`ValidationErrorUtils` лежал в `utils`**, хотя он обслуживает exception/error контракт. Теперь он находится в `exception`, рядом с `GlobalExceptionHandler` и `ApiErrorResponseFactory`.
4. **Security handlers вручную склеивали JSON строками**. Это небезопасно и легко ломает формат. Теперь `RestAuthenticationEntryPoint` и `RestAccessDeniedHandler` используют `ObjectMapper` и общую `ApiErrorResponseFactory`.
5. **Duplicate email был `400`, хотя Swagger обещал `409`**. Теперь используется `ResourceAlreadyExistsException` и код `RESOURCE_ALREADY_EXISTS`.
6. **Тестовая SQLite БД создавала локальный бинарный файл**. Теперь тесты используют in-memory SQLite, а `*.db`, `*.sqlite`, `*.sqlite3` добавлены в `.gitignore`.
7. **Не было e2e-теста**. Теперь добавлен `ApplicationE2ETest`, который проверяет реальный HTTP-сценарий и OpenAPI endpoint.
8. **Был лишний отдельный `JacksonConfig`**. Он убран: Spring Boot уже создаёт корректный `ObjectMapper`, а security handlers получают его через DI. Так структура стала проще и ближе к Boot best practice.
9. **Prod-профиль мог бы унаследовать учебный JWT secret по умолчанию**. Теперь в `prod` `JWT_SECRET` и параметры облачной БД обязательны: приложение должно падать на старте, если production-секреты не заданы.

---

## 11) Актуальная архитектурная идея

Проект остаётся слоисто-модульным:

- `controller` принимает HTTP и ничего не знает о JPA,
- `dto` описывает внешний контракт,
- `service` содержит use-cases и транзакционные границы,
- `validator` содержит бизнес-проверки,
- `repository` работает с БД,
- `exception` формирует единый error contract,
- `security` отвечает за authentication/authorization,
- `config` создаёт инфраструктурные beans,
- `utils` хранит только нейтральные чистые helper-функции.

Security не надо массово переносить в `utils`: JWT-фильтр, `UserDetailsService`, entry point и access denied handler — это не утилиты, а часть security-инфраструктуры. В `utils` уместны только маленькие stateless функции вроде `EmailUtils.normalize`.
