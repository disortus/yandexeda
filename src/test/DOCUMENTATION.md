# Документация по тестам проекта (`src/test`)

Этот документ подробно объясняет, какие типы тестов есть в проекте, зачем они нужны, как они связаны между собой и как их правильно расширять.

---

## 1. Зачем делить тесты по типам

В хорошем Spring Boot проекте тесты не должны быть "одинаковыми".  
Разные типы тестов проверяют разные риски:

1. **Unit** — логика отдельных классов без контекста Spring,
2. **Web/Slice** — web-слой и контракт HTTP,
3. **Integration** — взаимодействие нескольких слоёв вместе,
4. **E2E** — почти пользовательский сценарий через настоящий HTTP-сервер,
5. **Context** — что приложение вообще корректно поднимается.

Именно комбинация этих тестов даёт уверенность, что система работает стабильно.

---

## 2. Структура тестов в этом проекте

### `java/com/example/demo/`

- `DemoApplicationTests` — базовая проверка, что Spring ApplicationContext стартует на профиле `test`.

### `java/com/example/demo/unit/`

- `unit/utils/EmailUtilsTest` — проверка нормализации email,
- `unit/service/AuthServiceTest` — регистрация/логин и выдача JWT,
- `unit/validator/UserValidatorTest` — бизнес-валидация уникальности email.

### `java/com/example/demo/web/`

- `web/AuthControllerWebMvcTest` — проверка HTTP-контракта auth контроллера: статус-коды, JSON, `@Valid`, формат ошибки.
- `web/UserControllerWebMvcTest` — проверка HTTP-контракта user контроллера: `GET /users/{id}`, `POST /users`, structured validation errors.

### `java/com/example/demo/integration/`

- `integration/AuthFlowIntegrationTest` — сквозной сценарий регистрация → вызов защищённой ручки `/users/me`,
- `integration/SecurityAuthorizationIntegrationTest` — проверка role-based доступа (401/403/200), malformed JWT и правил роли `COURIER`,
- `integration/UserRepositoryIntegrationTest` — интеграция репозитория с реальной тестовой БД SQLite.

### `java/com/example/demo/e2e/`

- `e2e/ApplicationE2ETest` — полноценный сценарий через настоящий web server на random port:
  - проверка OpenAPI `/v3/api-docs`,
  - регистрация customer,
  - вызов `/users/me`,
  - проверка 401/403,
  - логин admin,
  - создание courier,
  - проверка duplicate email как `409 Conflict`,
  - проверка structured validation errors.

---

## 3. Почему в тестах используется SQLite

Тестовый профиль (`test`) использует SQLite, чтобы:

1. не зависеть от готовности облачной PostgreSQL,
2. запускать тесты локально и в CI без внешних зависимостей,
3. держать тесты быстрыми и воспроизводимыми.

В проекте тестовая строка подключения настроена как in-memory SQLite:

```yaml
spring:
  datasource:
    url: jdbc:sqlite:file:demo-test?mode=memory&cache=shared
```

Почему это лучше, чем `./demo-test.db`:

- тесты не оставляют локальный бинарный файл после запуска,
- `git status` не загрязняется изменениями тестовой БД,
- каждый запуск проще воспринимать как чистый и воспроизводимый.

Важно понимать ограничение: SQLite — не PostgreSQL. Для учебного проекта это нормально, но в будущем для проверки PostgreSQL-специфичных SQL/DDL лучше добавить Testcontainers с настоящим PostgreSQL.

---

## 4. Что проверяет каждый тип тестов

### Unit

Проверяет бизнес-ветки и edge-cases быстро и изолированно:

- корректная генерация response,
- выброс нужных исключений,
- валидация входных данных,
- нормализация email,
- работа сервиса без Spring-контекста.

### Web

Проверяет:

- JSON-контракт,
- HTTP-коды,
- валидацию `@Valid`,
- формат ошибок через `GlobalExceptionHandler`,
- поведение контроллера без настоящей БД и полного сервера.

### Integration

Проверяет:

- связку слоёв (`controller + service + repository + security`),
- работу JWT в реальном Spring MVC-потоке,
- ролевые ограничения,
- корректность репозиторных запросов на тестовой БД,
- Spring DI и конфигурацию профиля `test`.

### E2E

Проверяет:

- что приложение стартует как настоящий HTTP-сервер,
- что клиент может пройти бизнес-сценарий через сеть (`HttpClient`),
- что security-фильтры, JSON-сериализация, error handlers, controllers и repositories работают вместе,
- что Swagger/OpenAPI endpoint реально доступен.

---

## 5. Как запускать тесты

Все тесты:

```bash
./gradlew test --no-daemon
```

Только один класс:

```bash
./gradlew test --tests com.example.demo.integration.AuthFlowIntegrationTest --no-daemon
```

Только e2e:

```bash
./gradlew test --tests com.example.demo.e2e.ApplicationE2ETest --no-daemon
```

---

## 6. Best practices для развития тестов

1. Для unit-тестов держать чистую изоляцию (Mockito, без Spring context).
2. Для integration-тестов проверять реальные бизнес-сценарии, а не внутренние детали.
3. Для e2e-тестов не пытаться проверить всё на свете: один e2e должен покрывать главный путь и несколько критичных негативных веток.
4. Имена тестов делать говорящими (`should...`, `...When...`).
5. На каждую новую фичу добавлять:
   - минимум один позитивный,
   - минимум один негативный сценарий.
6. Проверять не только status code, но и структуру ответа.
7. Не коммитить файлы тестовых БД (`*.db`, `*.sqlite`, `*.sqlite3`).
8. Если тест требует данных, создавай их в `@BeforeEach` или внутри теста явно.
9. Не делай тесты зависимыми от порядка запуска.
10. Не используй production secrets в тестах.

---

## 7. Что важно понимать про `@SpringBootTest`

`@SpringBootTest` поднимает почти весь Spring Boot context. Это мощно, но дороже по времени, чем unit-тесты.

Пример:

```java
@SpringBootTest
@ActiveProfiles("test")
class AuthFlowIntegrationTest {
}
```

Что здесь происходит:

- Spring ищет `@SpringBootApplication`,
- создаёт beans (`Controller`, `Service`, `Repository`, `SecurityFilterChain`),
- читает `application.yaml`,
- активирует profile `test`,
- подключает SQLite вместо PostgreSQL.

Для e2e используется:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

Это означает: "подними настоящий web server на случайном свободном порту".

---

## 8. Какие тесты стоит добавить дальше

Следующие шаги эволюции:

1. Тесты истечения JWT (`expired token`).
2. Тесты на malformed/invalid signature JWT.
3. Тесты конкурентных сценариев (duplicate registration race).
4. Контрактные тесты ошибок для всех основных endpoint'ов.
5. Отдельный профиль тестов на PostgreSQL/Testcontainers.
6. Тесты CORS preflight (`OPTIONS`) для frontend-origin.
7. Тесты OpenAPI на наличие всех expected paths (`/auth/register`, `/auth/login`, `/users`, `/users/me`).

---

## 10. Как выбрать тип теста для новой фичи

Если добавляешь новую ручку, не обязательно сразу писать огромный e2e. Выбери минимальный набор по риску:

| Что изменилось | Какой тест добавить |
| --- | --- |
| Чистая функция без Spring | unit |
| Метод сервиса с business branching | unit/service |
| Новый DTO validation rule | web test или controller validation test |
| Новое правило роли | integration security test |
| Новый repository query method | repository integration test |
| Новый главный пользовательский сценарий | e2e |
| Новый Swagger/OpenAPI contract | e2e или integration проверка `/v3/api-docs` |

Хорошая стратегия: много быстрых unit/web/integration тестов и немного e2e на самые важные маршруты.

## 11. Почему test profile должен быть явным

В тестах используется:

```java
@ActiveProfiles("test")
```

Это защищает от опасной ошибки: случайно запустить тесты на dev/prod базе.

Если забыть `@ActiveProfiles("test")`, Spring может взять профиль по умолчанию (`dev`) и попробовать подключиться к PostgreSQL. В лучшем случае тесты упадут, в худшем — начнут писать тестовые данные в не ту БД.

---

## 9. Итого

Текущий набор тестов покрывает основные "виды" проверок для учебного REST API:

- unit,
- web contract,
- integration,
- e2e,
- security/authorization,
- repository integration,
- context bootstrap.

Это уже хороший фундамент для изучения TDD/BDD-подходов и безопасного развития функциональности без страха сломать существующее поведение.
