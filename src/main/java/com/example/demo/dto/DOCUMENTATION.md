# DTO layer

## Что такое DTO
DTO (Data Transfer Object) — это объекты, которые описывают **контракт API**:

- что клиент отправляет в запросе,
- что сервер возвращает в ответе.

DTO не должен быть равен Entity 1:1. Это разные уровни:

- Entity — модель хранения в БД
- DTO — модель внешнего взаимодействия

---

## Какие DTO есть в проекте

1. `UserCreateRequest`
   - входной DTO для `POST /users`
2. `UserResponse`
   - выходной DTO в успешных ответах
3. `ApiErrorResponse`
   - единый формат ошибок
4. `FieldValidationError`
   - отдельная ошибка поля для `@Valid`
5. `StatusResponse`
   - простой ответ для `GET /status`, намеренно без `@Schema`
6. `auth/RegisterRequest`
   - входной DTO для `POST /auth/register`
7. `auth/LoginRequest`
   - входной DTO для `POST /auth/login`
8. `auth/AuthResponse`
   - успешный ответ регистрации/логина с JWT и пользователем

---

## Аннотации в DTO и зачем они нужны

### `@NotBlank`
Проверяет, что строка:

- не `null`
- не пустая
- не состоит только из пробелов

Используй для обязательных текстовых полей (имя, логин, и т.д.).

### `@Email`
Проверяет корректность email-формата. Подходит для базовой проверки перед бизнес-валидацией.

### `@Size(min = X, max = Y)`
Ограничивает длину строки/коллекции. Это важно и для UX, и для защиты от мусорных данных.

---

## Когда использовать `record`, а когда `class`

- `record` удобно для immutable response DTO (как `UserResponse`)
- `class` удобнее для request DTO, где Jackson заполняет поля через setters

Оба подхода валидны. В боевых проектах можно использовать record и для request, если настроено корректно.

---

## Best practices

1. Делай отдельные DTO для create/update/read.
2. Не прокидывай Entity в API наружу.
3. Валидацию ставь на DTO, а бизнес-правила — в validator/service.
4. Старайся, чтобы DTO были простыми и понятными.


---

## Дополнение: DTO ошибок

После аудита формат ошибок стал более строгим и полезным:

### `ApiErrorResponse`

Содержит:

- `timestamp` — время ошибки,
- `status` — HTTP status code,
- `error` — текстовый HTTP reason,
- `code` — enum `ApiErrorCode`, удобный для frontend/клиента,
- `message` — человекочитаемое сообщение,
- `path` — путь запроса,
- `fieldErrors` — список ошибок конкретных полей.

### `FieldValidationError`

Используется для ошибок `@Valid`:

```json
{
  "field": "email",
  "message": "Email must be valid"
}
```

Почему это лучше, чем одна строка:

1. frontend может подсветить конкретное поле формы,
2. тесты могут проверять конкретную ошибку,
3. Swagger показывает более точный error contract,
4. API становится предсказуемее.

### Почему `code` — enum, а не произвольная строка

Enum ограничивает набор допустимых кодов. Это снижает риск опечаток вроде `AUTH_FAILED`, `AUTHENTICATION_FAIL`, `AUTHENTIFICATION_FAILED` в разных местах проекта.

---

## Почему auth DTO вынесены в подпакет `dto/auth`

`RegisterRequest`, `LoginRequest` и `AuthResponse` относятся к одному API-модулю — аутентификации. Чтобы не смешивать все DTO в одной плоской папке, они лежат в подпакете:

```text
dto/
  UserCreateRequest.java
  UserResponse.java
  ApiErrorResponse.java
  FieldValidationError.java
  auth/
    RegisterRequest.java
    LoginRequest.java
    AuthResponse.java
```

Это маленький пример layered-modular структуры:

- верхний слой всё ещё `dto`,
- внутри есть небольшой модуль `auth`,
- структура не становится слишком тяжёлой.

## Почему request DTO сделаны классами, а response DTO record'ами

В проекте request DTO (`RegisterRequest`, `LoginRequest`, `UserCreateRequest`) сделаны обычными классами с getters/setters. Это понятнее для начинающего и хорошо работает с Jackson + Bean Validation.

Response DTO (`UserResponse`, `AuthResponse`, `ApiErrorResponse`, `FieldValidationError`) сделаны `record`, потому что:

- они immutable по смыслу,
- их удобно создавать в сервисе/handler,
- меньше шаблонного кода,
- поля record сразу читаются как контракт ответа.

Это не единственно возможный стиль, но для учебного проекта он хорошо показывает разницу между "входными изменяемыми объектами" и "готовыми ответами API".

## Как DTO связан со Swagger

Аннотация `@Schema` на DTO и полях помогает Swagger UI показывать:

- описание объекта,
- описание каждого поля,
- примеры значений,
- допустимые enum-значения.

Когда меняешь DTO, обязательно проверь:

1. обновились ли `@Schema(description = ...)`,
2. реалистичны ли `example`,
3. соответствуют ли `@Size`, `@Pattern`, `@Email` реальным бизнес-правилам,
4. не попало ли в response чувствительное поле вроде `passwordHash`.
