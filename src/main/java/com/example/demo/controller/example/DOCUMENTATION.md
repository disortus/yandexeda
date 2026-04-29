# `controller/example` — простой пример controller-слоя

`SimpleGreetingControllerExample` показывает идею controller без Spring-аннотаций:

1. принять входные данные,
2. превратить их в request DTO,
3. вызвать service,
4. вернуть response DTO.

В настоящем контроллере проекта поверх этой идеи добавляются:

- `@RestController`,
- `@RequestMapping`,
- `@GetMapping` / `@PostMapping`,
- `@Valid`,
- Swagger-аннотации,
- security-аннотации вроде `@PreAuthorize`.

## Почему пример без `@RestController`

Если поставить `@RestController`, пример станет реальным endpoint. Здесь это не нужно: пример должен помогать читать архитектуру, а не менять API приложения.

Сравни:

```java
public SimpleGreetingResponseExample greet(String name)
```

и основную версию:

```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request)
```

Идея одинаковая: controller не делает бизнес-логику сам, а делегирует в service.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `AuthController` и `UserController`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/dto/example/DOCUMENTATION.md`.
