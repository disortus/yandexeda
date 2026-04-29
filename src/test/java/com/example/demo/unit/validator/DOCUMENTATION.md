# `unit/validator` — unit-тесты бизнес-валидации

Валидатор — это отдельное место для бизнес-правил, которые не являются простой синтаксической проверкой DTO. Например, `@Email` проверяет формат email, а `UserValidator` проверяет уникальность email в системе.

## Что тестирует `UserValidatorTest`

- если email уже существует, выбрасывается `ResourceAlreadyExistsException`,
- если email свободен, исключение не выбрасывается.

## Почему duplicate email — это `409 Conflict`

Повторная регистрация с уже занятым email — это не просто "плохой JSON" (`400`). JSON может быть идеально валидным. Проблема в конфликте с текущим состоянием системы: пользователь уже существует. Поэтому exception называется `ResourceAlreadyExistsException`, а HTTP-ответ должен быть `409 Conflict`.

## Почему репозиторий mock'ается

В unit-тесте валидатора нам не нужна реальная БД. Нам нужно проверить две ветки:

```java
existsByEmailIgnoreCase(email) == true
existsByEmailIgnoreCase(email) == false
```

Mockito позволяет задать эти ответы явно и быстро.

## Что здесь не проверяется

`UserValidatorTest` не проверяет:

- формат email,
- длину пароля,
- работу Hibernate unique constraint,
- HTTP status code.

Почему:

- формат email — ответственность DTO validation,
- unique constraint БД — ответственность integration/repository тестов,
- HTTP status — ответственность web/integration тестов,
- validator проверяет только бизнес-правило уникальности.

Такой фокус делает unit-тест маленьким и понятным.
