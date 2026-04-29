# `service/example` — простой пример service-слоя

`SimpleGreetingServiceExample` показывает главную идею service:

1. принять request DTO,
2. подготовить данные,
3. вызвать validator,
4. выполнить бизнес-действие,
5. вернуть response DTO.

В примере нет Spring, транзакций и БД. Это сделано специально: сначала нужно увидеть use-case как обычный Java-метод.

## Как пример связан с основной версией

Пример:

```java
return new SimpleGreetingResponseExample("Hello, " + name);
```

Основной проект:

```java
User saved = userRepository.save(user);
return toResponse(saved);
```

Идея одна: service не занимается HTTP, а выполняет сценарий приложения.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `AuthService` и `UserService`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/validator/example/DOCUMENTATION.md`.
