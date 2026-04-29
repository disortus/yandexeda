# `dto/example` — простой пример DTO-слоя

DTO — это объект для передачи данных между API и кодом приложения.

В примере есть два record:

- `SimpleGreetingRequestExample` — входные данные,
- `SimpleGreetingResponseExample` — выходные данные.

## Почему здесь record

Record хорошо подходит для простых immutable DTO:

```java
public record SimpleGreetingResponseExample(String message) {
}
```

В основной версии проекта response DTO тоже сделаны record:

- `UserResponse`,
- `AuthResponse`,
- `ApiErrorResponse`,
- `FieldValidationError`.

Request DTO в основной версии сделаны классами с setters, чтобы новичку было проще увидеть, как Jackson заполняет объект.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `../auth/DOCUMENTATION.md`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/service/example/DOCUMENTATION.md`.
