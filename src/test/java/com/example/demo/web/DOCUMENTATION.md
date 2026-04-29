# `web` — web/contract тесты

Web-тесты проверяют HTTP-контракт контроллера: какой status code возвращается, какой JSON приходит, работает ли `@Valid`, совпадает ли формат ошибок с `ApiErrorResponse`.

## Что тестирует `AuthControllerWebMvcTest`

- `POST /auth/register` возвращает `201 Created` и JSON с token/user,
- невалидный payload возвращает `400 Bad Request`,
- `GlobalExceptionHandler` формирует единый error response.

## Что тестирует `UserControllerWebMvcTest`

- `GET /users/{id}` возвращает `UserResponse`,
- `POST /users` возвращает `201 Created`,
- невалидный payload для `POST /users` возвращает `400 REQUEST_VALIDATION`,
- `fieldErrors` приходит как массив, а не как случайная строка.

## Что тестирует `StatusControllerWebMvcTest`

- `GET /status` возвращает `200 OK`,
- JSON содержит `application`,
- JSON содержит `status = UP`.

Этот контроллер специально сделан без Swagger-аннотаций. Web-тест здесь показывает, что обычный Spring MVC контракт работает независимо от того, написали мы OpenAPI-аннотации или нет.

## Почему используется standalone MockMvc

В тесте применяется:

```java
MockMvcBuilders.standaloneSetup(new AuthController(authService))
```

Это быстрее, чем полный `@SpringBootTest`, потому что мы вручную создаём только controller и controller advice. `AuthService` mock'ается, поэтому web-тест не зависит от БД, JWT и password hashing.

## Почему вручную передаётся `GlobalExceptionHandler`

Standalone MockMvc не поднимает весь Spring context, поэтому `@RestControllerAdvice` сам не подключится. Мы добавляем его явно:

```java
.setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseFactory(Clock.systemUTC())))
```

Так тест проверяет реальный формат ошибок, но остаётся быстрым.

## Что web-тесты здесь не проверяют

Standalone `MockMvc` не поднимает полноценный Spring Security filter chain и method security. Поэтому `@PreAuthorize` в этих тестах не является главным предметом проверки.

Ролевой доступ проверяется в `integration/SecurityAuthorizationIntegrationTest`, где поднимается Spring context и подключается `springSecurity()`.

Так тесты не дублируют друг друга:

- web проверяет HTTP/JSON/validation контракт,
- integration проверяет security и связку слоёв.
