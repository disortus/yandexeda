# `unit/service` — unit-тесты сервисного слоя

Сервисный слой содержит бизнес-сценарии приложения. В этом проекте `AuthServiceTest` проверяет регистрацию и логин без настоящей БД, без HTTP и без Spring context.

## Что тестирует `AuthServiceTest`

- регистрация создаёт пользователя с ролью `CUSTOMER`,
- email нормализуется (`Alice@Example.com` → `alice@example.com`),
- пароль не сохраняется как plain text, а передаётся в `PasswordEncoder`,
- возвращается JWT access token,
- логин возвращает токен при правильном пароле,
- неправильный пароль приводит к `UnauthorizedException`.

## Какие инструменты используются

- `@ExtendWith(MockitoExtension.class)` — включает Mockito для JUnit 5.
- `@Mock` — создаёт mock-зависимости (`UserRepository`, `PasswordEncoder`, `JwtService`).
- `@InjectMocks` — создаёт тестируемый сервис и внедряет в него mock-зависимости.
- `when(...).thenReturn(...)` — задаёт поведение mock-объекта.
- `verify(...)` и `ArgumentCaptor` — проверяют, что сервис сохранил правильного пользователя.

## Почему это unit, а не integration

Тест не проверяет Spring DI, JPA или HTTP. Он проверяет именно алгоритм `AuthService`: как сервис обрабатывает request, вызывает зависимости и формирует response.

## Почему используется `ArgumentCaptor`

При регистрации важно проверить не только итоговый `AuthResponse`, но и то, какого пользователя сервис попытался сохранить.

`ArgumentCaptor<User>` позволяет посмотреть объект, переданный в:

```java
userRepository.save(user)
```

Так тест проверяет важные детали:

- email стал lowercase,
- роль стала `CUSTOMER`,
- пароль заменён на hash,
- сервис не сохранил исходный пароль.

## Почему `Clock` mock'ается

`AuthService` использует `Clock`, чтобы выставить `createdAt`. В unit-тесте clock можно зафиксировать:

```java
when(clock.instant()).thenReturn(Instant.parse("2026-04-28T20:00:00Z"));
when(clock.getZone()).thenReturn(ZoneOffset.UTC);
```

Так тест не зависит от текущего времени на машине и остаётся детерминированным.

## Дальше читать

Следующий документ: `src/test/java/com/example/demo/web/DOCUMENTATION.md`.
