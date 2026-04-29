# `integration` — интеграционные тесты

Интеграционный тест проверяет совместную работу нескольких слоёв приложения. В отличие от unit-теста, здесь поднимается Spring context, используется реальная тестовая БД SQLite и настоящая конфигурация security.

## Какие тесты есть

### `AuthFlowIntegrationTest`

Проверяет сценарий:

1. пользователь регистрируется через `/auth/register`,
2. получает JWT,
3. вызывает защищённую ручку `/users/me`,
4. Spring Security достаёт пользователя из токена.

### `SecurityAuthorizationIntegrationTest`

Проверяет роли и доступы:

- анонимный пользователь получает `401`,
- `CUSTOMER` не может читать `/users` и получает `403`,
- `COURIER` может читать `/users`,
- `COURIER` не может создавать пользователей через `POST /users`,
- `ADMIN` может читать `/users`,
- `CUSTOMER` не может создавать пользователя через `POST /users`,
- malformed JWT возвращает JSON-ошибку `AUTHENTICATION_FAILED`.

### `UserRepositoryIntegrationTest`

Проверяет JPA repository на реальной SQLite БД:

- `existsByEmailIgnoreCase`,
- `findByEmailIgnoreCase`.

## Почему используется `@ActiveProfiles("test")`

Без этого Spring взял бы профиль `dev` из `application.yaml` и попытался подключиться к PostgreSQL. В тестах нам нужна SQLite, поэтому каждый интеграционный тест явно включает test profile.

## Почему используется MockMvc, а не настоящий порт

Integration-тесты здесь проверяют Spring MVC pipeline внутри приложения без поднятия настоящего socket server. Это быстрее, чем e2e, но всё ещё проверяет filters, controllers, services и repositories вместе.

## Почему эти тесты используют реальный `PasswordEncoder`

В integration-тестах пользователи иногда создаются напрямую через `UserRepository`. Чтобы логин потом работал через настоящий `AuthService`, пароль нужно сохранять не plain text, а BCrypt-хэш:

```java
passwordEncoder.encode("Qwerty123!")
```

Это важная деталь: если сохранить `"Qwerty123!"` напрямую, `PasswordEncoder.matches(...)` вернёт `false`, и тест будет падать не из-за security-роли, а из-за неправильной подготовки данных.
