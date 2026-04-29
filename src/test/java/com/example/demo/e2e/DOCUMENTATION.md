# `e2e` — end-to-end тесты

E2E-тест проверяет приложение максимально близко к реальному использованию: Spring Boot поднимает настоящий web server на случайном порту, а тест отправляет HTTP-запросы обычным `HttpClient`.

## Что проверяет `ApplicationE2ETest`

Один большой пользовательский сценарий:

1. Swagger/OpenAPI доступен по `/v3/api-docs`.
2. OpenAPI содержит ключевые paths: `/auth/register`, `/auth/login`, `/users`, `/users/me`.
3. Customer регистрируется через `/auth/register`.
4. Customer вызывает `/users/me` с JWT.
5. Анонимный запрос к `/users` получает `401`.
6. Customer-запрос к `/users` получает `403`.
7. Admin логинится и создаёт courier через `POST /users`.
8. Admin читает список пользователей.
9. Duplicate registration возвращает `409 Conflict`.
10. Невалидная регистрация возвращает `400` и `fieldErrors`.

## Чем e2e отличается от integration

Integration-тесты используют MockMvc и не открывают настоящий порт. E2E-тест использует:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@LocalServerPort
```

Это значит, что тест проходит через реальный HTTP stack: сериализация JSON, network layer, servlet container, filters, controllers и exception handlers.

## Почему e2e-тестов не должно быть слишком много

E2E-тесты самые медленные и самые широкие. Их задача — проверить основные критические маршруты, а не каждую маленькую ветку. Большинство edge-cases лучше покрывать unit/web/integration тестами.

## Почему OpenAPI проверяется именно в e2e

Swagger/OpenAPI может сломаться не на компиляции, а на runtime: например, из-за несовместимости версии Spring Boot и springdoc. Поэтому проверка `/v3/api-docs` через настоящий HTTP server полезнее, чем просто наличие аннотаций в коде.

E2E здесь подтверждает:

- embedded server стартует,
- springdoc подключён,
- security не блокирует `/v3/api-docs`,
- JSON OpenAPI реально строится,
- в контракте есть ключевые endpoints.
