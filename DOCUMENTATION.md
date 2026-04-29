# Подробная документация по Swagger / OpenAPI в проекте

Этот документ объясняет:

1. зачем нужна Swagger/OpenAPI документация,
2. как она реализована в этом проекте,
3. как правильно описывать схемы, ошибки и security,
4. как поддерживать документацию в актуальном состоянии.

---

## 1) Что такое OpenAPI и Swagger

### OpenAPI

OpenAPI — это стандарт машинно-читаемого описания REST API:

- endpoints,
- request/response схемы,
- коды ошибок,
- security-механизмы.

### Swagger UI

Swagger UI — это веб-интерфейс, который визуализирует OpenAPI и даёт возможность тестировать ручки прямо из браузера.

В проекте доступно:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 2) Зачем это нужно (особенно в учебном проекте)

1. **Понятный контракт API** для себя и других разработчиков.
2. **Быстрый онбординг**: новый человек может понять API без чтения всего кода.
3. **Тестирование вручную** через UI без Postman на старте.
4. **Снижение ошибок интеграции** между backend и frontend.
5. **Документация всегда рядом с кодом** и обновляется при изменениях.

---

## 3) Что уже настроено в проекте

### Зависимость

В `build.gradle` подключён:

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6'
```

### Конфиг

В `OpenApiConfig` добавлены:

- базовая информация о проекте,
- security scheme `bearerAuth` для JWT.

### Конфигурация путей

В `application.yaml`:

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    display-request-duration: true
    persist-authorization: true
  api-docs:
    path: /v3/api-docs
```

---

## 4) Как документируются endpoint'ы в коде

### На уровне контроллера

- `@Tag` — группировка endpoints,
- `@SecurityRequirement(name = "bearerAuth")` — требование JWT (для защищённых контроллеров).

### На уровне методов

- `@Operation` — summary + description,
- `@ApiResponses` / `@ApiResponse` — коды ответов и схемы ошибок,
- `@Parameter` — описание path/query параметров.

### На уровне DTO

- `@Schema` на классах и полях,
- примеры (`example`),
- описание ограничений.

---

## 5) Как документирована security

В OpenAPI добавлена схема:

- тип: HTTP bearer,
- формат: JWT,
- имя: `bearerAuth`.

Публичные endpoint'ы (`/auth/register`, `/auth/login`) помечены `@SecurityRequirements` (без токена).

Защищённые endpoint'ы (`/users/**`) требуют bearer token.

---

## 6) Что должно быть в качественной API-документации

Для каждой ручки желательно иметь:

1. краткое назначение (summary),
2. подробное описание бизнес-смысла,
3. входные параметры + примеры,
4. успешные ответы,
5. ошибки (400/401/403/404/409/500),
6. указание security-правил,
7. ограничения по ролям (если есть).

---

## 7) Как поддерживать Swagger в актуальном состоянии

Правило: **изменил API-контракт → сразу обнови аннотации**.

Checklist при изменениях:

1. изменился request DTO → обнови `@Schema` поля,
2. добавился новый error-case → добавь `@ApiResponse`,
3. поменялись роли доступа → обнови description/security,
4. добавился endpoint → зафиксируй `@Operation` и примеры.

---

## 8) Практические советы по качеству схем

1. Пиши user-oriented описания, а не только технические.
2. Добавляй реалистичные `example`.
3. Следи за единым стилем именований и терминов.
4. Не дублируй противоречивые описания в разных местах.
5. Для ошибок используй единый формат (`ApiErrorResponse`).

---

## 9) Как проверить, что документация не устарела

1. Запусти приложение: `./gradlew bootRun`
2. Открой `swagger-ui.html`
3. Прокликай все endpoints
4. Сверь:
   - названия/описания,
   - обязательные поля,
   - схемы ответов,
   - коды ошибок,
   - требования по авторизации.

Если в Swagger что-то не соответствует реальному поведению — это технический долг, который нужно закрыть сразу.

---

## 10) Мини-памятка по использованию Swagger UI в этом проекте

1. Зарегистрируй пользователя через `/auth/register`.
2. Скопируй `accessToken`.
3. Нажми **Authorize** и вставь `Bearer <token>`.
4. Тестируй защищённые `/users` endpoints с учётом роли.

---

## 11) Итого

Swagger/OpenAPI в этом проекте — не "декоративная штука", а часть архитектуры:

- фиксирует API-контракт,
- ускоряет разработку,
- снижает ошибки интеграции,
- помогает изучать Spring Boot осмысленно и системно.

Если держать документацию синхронной с кодом, это превращается в реальный рабочий инструмент команды.


---

## 12) Важный аудит совместимости Swagger

В ходе проверки была найдена критичная проблема: Swagger/OpenAPI был подключён, но `/v3/api-docs` возвращал `500 Internal Server Error`.

Причина была не в аннотациях контроллеров, а в несовместимости зависимостей:

- Spring Boot `4.1.0-RC1`,
- Spring Framework 7,
- springdoc-openapi `2.5.0`.

В stacktrace была ошибка:

```text
NoSuchMethodError: org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)
```

Что это означает для начинающего:

1. Код может компилироваться, но runtime endpoint всё равно может падать из-за binary incompatibility.
2. Swagger нужно проверять тестом или хотя бы ручным вызовом `/v3/api-docs`.
3. Для учебного проекта лучше использовать стабильные версии Spring Boot, а не RC/snapshot, если цель — изучать архитектуру, а не отлавливать несовместимости библиотек.

Поэтому проект переведён на:

```gradle
id 'org.springframework.boot' version '3.4.5'
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6'
```

И добавлен e2e-тест, который проверяет `/v3/api-docs` через настоящий HTTP server.

---

## 13) Как описывать ошибки в Swagger правильно

В проекте есть единый DTO ошибки:

- `ApiErrorResponse` — общий ответ ошибки,
- `FieldValidationError` — отдельная ошибка поля для `@Valid`,
- `ApiErrorCode` — enum машинно-читаемых кодов.

Для каждой ручки в `@ApiResponses` нужно указывать не только счастливый ответ, но и ошибки:

```java
@ApiResponse(
    responseCode = "400",
    description = "Ошибка валидации",
    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
)
```

Почему это важно:

1. frontend видит стабильный формат ошибки,
2. backend явно документирует edge-cases,
3. тесты могут проверять не только HTTP status, но и `code`, `message`, `fieldErrors`.

---

## 14) Мини-checklist для новой ручки

Когда добавляешь новый endpoint:

1. Создай request/response DTO.
2. Добавь Bean Validation аннотации (`@NotBlank`, `@Size`, `@Email`, `@Pattern`).
3. Добавь business validation в `validator` или `service`.
4. Добавь `@Operation` с понятным summary/description.
5. Добавь `@ApiResponses` для 200/201 и возможных 400/401/403/404/409.
6. Если ручка защищена — укажи `@SecurityRequirement(name = "bearerAuth")`.
7. Добавь unit/web/integration/e2e тест по необходимости.
8. Проверь `/v3/api-docs` и Swagger UI.

---

## 15) Подробно по главным Swagger-аннотациям

### `@Tag`

Ставит контроллер в логическую группу Swagger UI.

Пример:

```java
@Tag(name = "Users", description = "Операции с пользователями")
```

Хороший `name` должен быть коротким, а `description` должен объяснять границы группы. Если в проекте будет десять контроллеров, именно теги помогут не превратить Swagger UI в длинную неструктурированную страницу.

### `@Operation`

Описывает один endpoint.

```java
@Operation(
    summary = "Получить текущего пользователя",
    description = "Возвращает пользователя по email из JWT subject"
)
```

Рекомендации:

- `summary` пиши коротко,
- `description` пиши как пояснение для frontend/backend разработчика,
- не дублируй очевидное из URL, объясняй бизнес-смысл.

### `@ApiResponse`

Описывает один возможный HTTP-ответ.

```java
@ApiResponse(
    responseCode = "409",
    description = "Пользователь с таким email уже существует",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class)
    )
)
```

Почему важно указывать `content`:

1. Swagger покажет точную схему ответа,
2. клиент поймёт, какие поля ждать,
3. тесты и документация будут говорить на одном языке.

### `@Schema`

Описывает DTO или поле DTO.

```java
@Schema(description = "Email пользователя", example = "alice@example.com")
```

Для enum можно указывать допустимые значения:

```java
@Schema(allowableValues = { "CUSTOMER", "ADMIN", "COURIER" })
```

### `@SecurityRequirement`

Показывает, что endpoint требует JWT:

```java
@SecurityRequirement(name = "bearerAuth")
```

Публичные ручки (`/auth/register`, `/auth/login`) наоборот помечаются `@SecurityRequirements`, чтобы Swagger не считал их защищёнными.

## 16) Актуальные ручки и их Swagger-контракт

| Method | Path | Успешный ответ | Основные ошибки | Security |
| --- | --- | --- | --- | --- |
| `POST` | `/auth/register` | `201 AuthResponse` | `400`, `409` | публично |
| `POST` | `/auth/login` | `200 AuthResponse` | `400`, `401` | публично |
| `GET` | `/users/me` | `200 UserResponse` | `401`, `404` | JWT |
| `GET` | `/users` | `200 UserResponse[]` | `401`, `403` | JWT + `ADMIN/COURIER` |
| `GET` | `/users/{id}` | `200 UserResponse` | `401`, `403`, `404` | JWT + `ADMIN/COURIER` |
| `POST` | `/users` | `201 UserResponse` | `400`, `401`, `403`, `409` | JWT + `ADMIN` |

Эта таблица полезна как быстрый ручной чек перед изменением контроллера: если меняешь поведение endpoint, обнови и Swagger-аннотации, и тесты.

## 17) Как проверять OpenAPI автоматически

В проекте e2e-тест проверяет, что `/v3/api-docs` реально отдаёт `200` и содержит ожидаемые paths:

- `/auth/register`,
- `/auth/login`,
- `/users`,
- `/users/me`.

Это важно, потому что Swagger может быть "написан" в коде, но сломан на runtime из-за несовместимости зависимостей или ошибки конфигурации. Автотест ловит такую проблему раньше ручной проверки.

## 18) Частая ошибка: Swagger не заменяет тесты

Swagger отвечает на вопрос:

- какой контракт у API,
- какие поля у request/response,
- какие статусы возможны,
- как авторизоваться.

Тесты отвечают на другой вопрос:

- действительно ли код выполняет этот контракт,
- работает ли security,
- возвращаются ли нужные статусы,
- не сломался ли сценарий на реальном HTTP-сервере.

Для хорошего учебного проекта нужны оба инструмента: Swagger как живой справочник и тесты как проверка поведения.
