# Exception layer

## Назначение
Слой exception нужен, чтобы сделать ошибки:

1. централизованными,
2. предсказуемыми,
3. удобными для API-клиента.

Без этого каждый контроллер возвращал бы ошибки по-своему.

---

## Что реализовано в проекте

- `ResourceNotFoundException` — ресурс не найден (`404`)
- `BusinessValidationException` — бизнес-правило нарушено (`400`)
- `GlobalExceptionHandler` — централизованный преобразователь исключений в JSON

---

## Главные аннотации и когда использовать

### `@RestControllerAdvice`
Глобальный обработчик для всех контроллеров.

Используй, когда нужно единообразно обрабатывать исключения и формировать API-ошибки.

### `@ExceptionHandler(SomeException.class)`
Метод обработает конкретный тип исключения.

Можно сделать несколько обработчиков под разные классы ошибок.

---

## Формат ошибки

В проекте используется `ApiErrorResponse`:

- `timestamp`
- `status`
- `error`
- `code`
- `message`
- `path`
- `fieldErrors`

Это делает frontend/клиентскую интеграцию стабильной.

`code` — это enum `ApiErrorCode`. Он нужен, чтобы клиент мог программно отличить, например, `REQUEST_VALIDATION` от `RESOURCE_ALREADY_EXISTS`, не парся текст сообщения.

`fieldErrors` — список ошибок конкретных полей. Для обычных ошибок список пустой, для `@Valid` ошибок там будут элементы вида:

```json
{
  "field": "email",
  "message": "Email must be valid"
}
```

---

## Валидационные ошибки

Ошибки от `@Valid` приходят как `MethodArgumentNotValidException`.

Они превращаются в читаемое сообщение через `ValidationErrorUtils`.

---

## Best practices

1. Разделяй исключения по бизнес-смыслу.
2. Возвращай корректные HTTP-коды (`400`, `404`, `409`, `500` и т.д.).
3. Не возвращай stacktrace клиенту в production.
4. Логируй технические детали на сервере, а клиенту отдавай безопасное сообщение.

---

## Дополнение: security ошибки (401/403)

После внедрения Spring Security часть ошибок обрабатывается не только через `GlobalExceptionHandler`, но и через специализированные security handlers:

- `RestAuthenticationEntryPoint` → `401 Unauthorized`
- `RestAccessDeniedHandler` → `403 Forbidden`

Зачем это нужно:

1. security-ошибки возникают в filter chain до контроллера,
2. `@RestControllerAdvice` их не всегда перехватит в нужной форме,
3. отдельные handlers гарантируют единый JSON-ответ.

Важно: формат ответа синхронизирован с общим контрактом `ApiErrorResponse` (status/code/message/path), чтобы клиенту было проще обрабатывать ошибки единообразно.


---

## Дополнение: актуальная структура error handling

После аудита проекта слой `exception` расширен и теперь содержит не только классы исключений, но и инфраструктуру формирования ошибок:

- `GlobalExceptionHandler` — главный `@RestControllerAdvice`, который ловит исключения из controller/service слоя.
- `ApiErrorResponseFactory` — единая фабрика для создания `ApiErrorResponse`.
- `ValidationErrorUtils` — mapper из `BindingResult` в список `FieldValidationError`.
- `ResourceAlreadyExistsException` — конфликт существующего ресурса (`409 Conflict`).

### Почему `ValidationErrorUtils` теперь не в `utils`

`ValidationErrorUtils` не является универсальной утилитой уровня "строки/даты/коллекции". Он знает про:

- Spring `BindingResult`,
- `FieldValidationError`,
- формат API-ошибки.

То есть он обслуживает конкретно exception/error слой. Поэтому архитектурно правильнее держать его рядом с `GlobalExceptionHandler`.

### Почему нужна `ApiErrorResponseFactory`

Без фабрики легко получить разные форматы ошибок:

- security handler собрал один JSON,
- controller advice собрал другой JSON,
- где-то забыли `fieldErrors`,
- где-то `code` строкой, а где-то enum.

Фабрика централизует создание ошибки и используется обычным exception handling и security handlers.

### Почему duplicate email — это `409 Conflict`

Если клиент отправил валидный JSON, но email уже занят, проблема не в синтаксисе запроса, а в конфликте с текущим состоянием системы. Поэтому используется:

- exception: `ResourceAlreadyExistsException`,
- HTTP status: `409 Conflict`,
- code: `RESOURCE_ALREADY_EXISTS`.

### Какие исключения сейчас обрабатываются

- `ResourceNotFoundException` → `404 RESOURCE_NOT_FOUND`
- `ResourceAlreadyExistsException` → `409 RESOURCE_ALREADY_EXISTS`
- `BusinessValidationException` → `400 BUSINESS_VALIDATION`
- `UnauthorizedException` → `401 AUTHENTICATION_FAILED`
- `AccessDeniedException` → `403 ACCESS_DENIED`
- `MethodArgumentNotValidException` → `400 REQUEST_VALIDATION` + `fieldErrors`
- `MethodArgumentTypeMismatchException` → `400 REQUEST_VALIDATION`
- `DataIntegrityViolationException` → `409 RESOURCE_ALREADY_EXISTS` как защита от race-condition на unique constraint
- `Exception` → `500 INTERNAL_ERROR`

### Почему не возвращаем stacktrace клиенту

Stacktrace может раскрыть:

- названия классов,
- внутреннюю структуру проекта,
- SQL/таблицы,
- версии библиотек,
- потенциальные уязвимости.

Поэтому stacktrace логируется на сервере, а клиент получает безопасное сообщение.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/middleware/DOCUMENTATION.md`.
