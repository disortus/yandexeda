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
- `POST /users` -> `service.create(request)`

Для `POST` используется `@Valid @RequestBody UserCreateRequest` и возвращается `201 Created`.

---

## Практические советы

1. Вход/выход контроллера всегда держи через DTO.
2. Не возвращай Entity наружу в зрелом проекте.
3. Ошибки обрабатывай централизованно через `@RestControllerAdvice`.
4. Нейминг методов делай как бизнес-действия: `createUser`, `getUserById`, `updateUser`.
