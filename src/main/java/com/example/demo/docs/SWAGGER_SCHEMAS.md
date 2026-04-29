# Swagger / OpenAPI schemas в этом проекте

Этот документ подробно объясняет, что такое Swagger schemas, откуда они берутся в Spring Boot, как они связаны с DTO и почему часть endpoint может отображаться в OpenAPI даже без Swagger-аннотаций.

---

## 1. Что такое schema в OpenAPI

Schema — это описание формы JSON-объекта.

Например, если endpoint возвращает пользователя:

```json
{
  "id": 42,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "CUSTOMER",
  "createdAt": "2026-04-28T20:45:10"
}
```

то OpenAPI schema отвечает на вопросы:

- какие поля есть у объекта,
- какие типы у этих полей,
- какие поля обязательны,
- какие enum-значения допустимы,
- какие примеры можно показать в Swagger UI.

Schema — это контракт данных. Controller описывает URL и HTTP-метод, а schema описывает request/response body.

---

## 2. Откуда springdoc берёт schemas

Springdoc строит OpenAPI из нескольких источников:

1. Spring MVC annotations:
   - `@RestController`,
   - `@RequestMapping`,
   - `@GetMapping`,
   - `@PostMapping`,
   - `@RequestBody`,
   - `@PathVariable`.

2. Java-типы:
   - `record`,
   - обычные классы с getters/setters,
   - `List<UserResponse>`,
   - enum.

3. Bean Validation:
   - `@NotBlank`,
   - `@NotNull`,
   - `@Email`,
   - `@Size`,
   - `@Pattern`.

4. Swagger annotations:
   - `@Schema`,
   - `@Operation`,
   - `@ApiResponse`,
   - `@SecurityRequirement`.

Важно: Swagger-аннотации улучшают документацию, но не являются единственным источником схем.

---

## 3. Контроллер без Swagger-аннотаций: `StatusController`

В проект добавлен `StatusController`:

```java
@RestController
@RequestMapping("/status")
public class StatusController {
    @GetMapping
    public StatusResponse getStatus() {
        return new StatusResponse(applicationName, "UP");
    }
}
```

В нём нет импортов `io.swagger.v3.oas.annotations.*`.

Это значит:

- мы не пишем `@Operation`,
- мы не пишем `@ApiResponse`,
- мы не пишем `@Tag`,
- DTO `StatusResponse` не имеет `@Schema`.

Но springdoc всё равно видит endpoint, потому что он сканирует Spring MVC mappings. Поэтому `/status` появляется в `/v3/api-docs` с автоматически сгенерированным описанием.

### Что будет хуже без Swagger-аннотаций

Автоматическая схема обычно технически корректна, но менее понятна:

- summary может быть сгенерирован из имени метода,
- не будет подробного бизнес-описания,
- ошибки не будут описаны так явно,
- примеры полей могут отсутствовать,
- security-контекст может быть менее очевиден.

Такой контроллер полезен для обучения: он показывает разницу между "OpenAPI сгенерировался автоматически" и "API задокументирован качественно".

---

## 4. Текущие схемы проекта

### `RegisterRequest`

Request body для:

```http
POST /auth/register
```

Поля:

- `name` — обязательная строка, 2-50 символов,
- `email` — обязательная строка email-формата, максимум 120 символов,
- `password` — обязательная строка, 8-72 символа, с требованиями сложности.

Ключевые аннотации:

- `@NotBlank`,
- `@Email`,
- `@Size`,
- `@Pattern`,
- `@Schema`.

### `LoginRequest`

Request body для:

```http
POST /auth/login
```

Поля:

- `email`,
- `password`.

Для логина пароль не проверяется через сложный `@Pattern`, потому что сложность нужна при создании/изменении пароля. На логине главное — принять непустое значение и сравнить его с BCrypt hash.

### `AuthResponse`

Response body для успешных:

- `POST /auth/register`,
- `POST /auth/login`.

Поля:

- `accessToken` — JWT access token,
- `tokenType` — enum `BEARER`,
- `expiresAt` — дата/время окончания токена,
- `user` — вложенный `UserResponse`.

### `UserCreateRequest`

Request body для:

```http
POST /users
```

Поля:

- `name`,
- `email`,
- `password`,
- `role`.

В отличие от публичной регистрации, здесь роль передаётся явно. Endpoint доступен только `ADMIN`, поэтому админ может создать `CUSTOMER`, `COURIER` или `ADMIN`.

### `UserResponse`

Response body для пользовательских endpoint:

- `GET /users`,
- `GET /users/{id}`,
- `GET /users/me`,
- `POST /users`.

Поля:

- `id`,
- `name`,
- `email`,
- `role`,
- `createdAt`.

Важно: здесь нет `passwordHash`. Это не случайность, а security-правило: даже hash пароля нельзя отдавать наружу.

### `ApiErrorResponse`

Единый response body для ошибок.

Поля:

- `timestamp`,
- `status`,
- `error`,
- `code`,
- `message`,
- `path`,
- `fieldErrors`.

Эта схема используется и обычным `GlobalExceptionHandler`, и security handlers для `401/403`.

### `FieldValidationError`

Элемент массива `fieldErrors` внутри `ApiErrorResponse`.

Поля:

- `field`,
- `message`.

Нужен, чтобы frontend мог подсветить конкретные поля формы.

### `StatusResponse`

Response body для:

```http
GET /status
```

Поля:

- `application`,
- `status`.

Этот DTO специально оставлен без `@Schema`, чтобы показать автоматическую генерацию OpenAPI schema без Swagger-аннотаций.

---

## 5. Как `@Schema` улучшает DTO

Пример:

```java
@Schema(description = "Email пользователя", example = "alice@example.com")
@Email(message = "Email must be valid")
private String email;
```

Здесь две разные ответственности:

- `@Email` влияет на runtime-валидацию,
- `@Schema` влияет на документацию.

Без `@Schema` Swagger может понять, что поле строковое, но не знает хороший пример и человеческое описание.

---

## 6. Как описывать enum в схемах

Для ролей используется enum:

```java
public enum UserRole {
    CUSTOMER,
    ADMIN,
    COURIER
}
```

В OpenAPI это становится ограниченным набором строк. Это удобно для клиента:

- frontend видит допустимые значения,
- Swagger UI может показать список,
- меньше риск отправить `courier`, `Courier` или `DELIVERY`.

В DTO можно дополнительно указать:

```java
@Schema(
    description = "Роль пользователя",
    allowableValues = { "CUSTOMER", "ADMIN", "COURIER" }
)
```

---

## 7. Как описывать ошибки

Каждая ручка должна документировать не только успешный ответ, но и ошибки.

Пример:

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

Почему это важно:

- клиент знает формат ошибки заранее,
- тесты могут проверять `code` и `fieldErrors`,
- Swagger UI показывает не только happy path.

---

## 8. Как проверить schemas в браузере

1. Запусти приложение:

```bash
./gradlew bootRun
```

2. Открой:

```text
http://localhost:8080/swagger-ui.html
```

3. Или посмотри сырой OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

4. Найди блок:

```json
"components": {
  "schemas": {
    ...
  }
}
```

Именно там лежат DTO schemas.

---

## 9. Checklist для новой схемы

Когда добавляешь новый request/response DTO:

1. Назови DTO по сценарию: `CreateOrderRequest`, `OrderResponse`, `LoginRequest`.
2. Добавь Bean Validation для request DTO.
3. Добавь `@Schema` с описаниями и примерами, если DTO является частью публичного API.
4. Не добавляй чувствительные поля в response DTO.
5. Для enum проверь, что Swagger показывает допустимые значения.
6. Для ошибок используй `ApiErrorResponse`, а не новый случайный формат.
7. Добавь web/integration/e2e тест по уровню риска.
8. Проверь `/v3/api-docs`.

---

## 10. Когда можно не писать Swagger-аннотации

Иногда endpoint настолько простой, что можно начать без ручной Swagger-документации.

Например:

```http
GET /status
```

Но для бизнес-ручек лучше писать Swagger-аннотации сразу, потому что они являются частью API-контракта.

Правило для этого проекта:

- учебный технический endpoint можно оставить без Swagger-аннотаций,
- публичные бизнес endpoints должны иметь подробные Swagger annotations,
- request/response DTO бизнес endpoints должны иметь понятные schemas.

## Дальше читать

Следующий документ: `src/main/resources/DOCUMENTATION.md`.
