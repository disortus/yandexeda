# Controller layer

## Роль слоя
`controller` — это web-слой, который принимает HTTP-запросы, вызывает service и возвращает HTTP-ответ.

Контроллер должен быть максимально «тонким»: без сложной бизнес-логики и без доступа к БД напрямую.

---

## Основные аннотации и когда их использовать

### `@RestController`
Используй для REST API. Это комбинация `@Controller + @ResponseBody`:

- метод возвращает объект,
- Spring автоматически сериализует его в JSON.

### `@RequestMapping("/users")`
Общий префикс для всех endpoints контроллера.

### `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
Аннотации конкретных HTTP-методов.

### `@PathVariable`
Когда параметр идёт в пути URL (`/users/{id}`).

### `@RequestBody`
Когда нужно распарсить JSON body в Java-объект (DTO).

### `@Valid`
Запускает Bean Validation на входном DTO. Используется вместе с аннотациями в DTO (`@NotBlank`, `@Email` и т.д.).

### `ResponseEntity<T>`
Используй, когда нужно явно контролировать HTTP-статус и заголовки.

---

## Что должно быть в controller

1. Привязка URL и HTTP-методов
2. Приём входных DTO
3. Делегирование в service
4. Возврат response DTO / `ResponseEntity`

---

## Что НЕ должно быть в controller

- SQL-запросов
- вызовов `repository` напрямую
- ветвистой бизнес-логики
- тяжёлого маппинга и вычислений

---

## Как реализовано в этом проекте

`UserController`:

- `GET /users` -> `service.getAll()`
- `GET /users/{id}` -> `service.getById(id)`
- `GET /users/me` -> `service.getByEmail(principal.getName())`
- `POST /users` -> `service.createByAdmin(request)`

Для `POST` используется `@Valid @RequestBody UserCreateRequest` и возвращается `201 Created`.

---

## Практические советы

1. Вход/выход контроллера всегда держи через DTO.
2. Не возвращай Entity наружу в зрелом проекте.
3. Ошибки обрабатывай централизованно через `@RestControllerAdvice`.
4. Нейминг методов делай как бизнес-действия: `createUser`, `getUserById`, `updateUser`.

---

## Дополнение: AuthController и role-aware endpoints

Проект расширен вторым контроллером — `AuthController`:

- `POST /auth/register`
- `POST /auth/login`

### Что важно понимать

1. Эти endpoint'ы публичные (без JWT),
2. они возвращают `AuthResponse` с access token,
3. дальше клиент использует токен в `Authorization` заголовке.

Также в `UserController` теперь есть role-aware ограничения:

- часть endpoint'ов доступна только `ADMIN`,
- часть — `ADMIN` и `COURIER`,
- `GET /users/me` для любого аутентифицированного пользователя.

Это хороший пример, как controller слой остаётся "тонким", но при этом явно выражает security-контракт API.

---

## Подробно: как читать аннотации в контроллерах проекта

### `@Tag`

`@Tag` относится к Swagger/OpenAPI. Он группирует ручки в Swagger UI:

- `Authentication` — регистрация и логин,
- `Users` — операции с пользователями.

Это не влияет на runtime-логику приложения, но сильно влияет на читаемость API-документации.

### `@Operation`

`@Operation(summary = ..., description = ...)` описывает конкретную ручку:

- `summary` — короткое название,
- `description` — подробное объяснение бизнес-смысла.

Хорошее правило: `summary` отвечает на вопрос "что делает ручка", а `description` отвечает на вопрос "когда и зачем её использовать".

### `@ApiResponses` и `@ApiResponse`

Эти аннотации описывают возможные HTTP-ответы:

- успешные (`200`, `201`),
- ошибки входных данных (`400`),
- ошибки авторизации (`401`),
- ошибки прав (`403`),
- отсутствие ресурса (`404`),
- конфликт состояния (`409`).

В проекте для ошибок везде используется `ApiErrorResponse`, чтобы Swagger показывал один и тот же error contract.

### `@PreAuthorize`

`@PreAuthorize` — это method-level security. Например:

```java
@PreAuthorize("hasAnyRole('ADMIN','COURIER')")
```

Такой код читается прямо как правило доступа: метод можно вызвать только пользователю с ролью `ADMIN` или `COURIER`.

### `@Parameter(hidden = true)`

В `GET /users/me` параметр `Principal` приходит не из JSON и не из URL. Его подставляет Spring Security из текущего `Authentication`. Поэтому он скрыт из Swagger:

```java
public UserResponse getMe(@Parameter(hidden = true) Principal principal)
```

Если не скрыть такой параметр, Swagger может показать его как будто клиент должен сам отправлять `principal`, а это неверно.

---

## Актуальная таблица endpoints

| Method | Path | Controller | Доступ | Что возвращает |
| --- | --- | --- | --- | --- |
| `POST` | `/auth/register` | `AuthController` | публично | `AuthResponse` с JWT и пользователем |
| `POST` | `/auth/login` | `AuthController` | публично | `AuthResponse` с JWT и пользователем |
| `GET` | `/users/me` | `UserController` | любой authenticated | `UserResponse` текущего пользователя |
| `GET` | `/users` | `UserController` | `ADMIN`, `COURIER` | список `UserResponse` |
| `GET` | `/users/{id}` | `UserController` | `ADMIN`, `COURIER` | `UserResponse` по id |
| `POST` | `/users` | `UserController` | `ADMIN` | созданный `UserResponse` |
