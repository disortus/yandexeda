# Repository layer

## Назначение
Repository-слой отвечает только за чтение/запись данных в БД.

Через Spring Data JPA ты чаще всего пишешь **интерфейс**, а реализацию генерирует Spring.

---

## Основные аннотации и механики

### `@Repository`
При наследовании от `JpaRepository` обычно можно не ставить `@Repository` вручную — Spring Data сам регистрирует бин.

### `JpaRepository<Entity, IdType>`
Даёт базовый CRUD:

- `findAll()`
- `findById(id)`
- `save(entity)`
- `deleteById(id)`
- `existsById(id)`

### Query Methods
Методы по имени, например:

- `existsByEmailIgnoreCase(String email)`

Spring строит SQL/HQL автоматически из названия метода.

---

## Что реализовано в проекте

`UserRepository extends JpaRepository<User, Long>`

Дополнительный метод:

- `existsByEmailIgnoreCase(String email)` — проверка уникальности email без учёта регистра.
- `findByEmailIgnoreCase(String email)` — поиск пользователя по email без учёта регистра для логина и `/users/me`.

---

## Когда писать `@Query`

Используй `@Query`, если:

1. query method становится слишком длинным и сложным
2. нужен join/агрегация/сложная фильтрация
3. нужна оптимизация конкретного SQL/HQL

---

## Best practices

1. Repository = только данные, без бизнес-правил.
2. Не смешивай repository и service роли.
3. Давай методам репозитория говорящие имена.
4. Сложные операции лучше оформляй в custom repository implementation.

---

## Почему repository возвращает `Optional<User>`

Метод:

```java
Optional<User> findByEmailIgnoreCase(String email);
```

явно говорит: пользователя может не быть.

Это лучше, чем возвращать `null`, потому что:

- caller вынужден обработать отсутствие значения,
- код становится читаемее,
- меньше риск `NullPointerException`.

В сервисе это выглядит так:

```java
repo.findByEmailIgnoreCase(email)
    .orElseThrow(() -> new ResourceNotFoundException(...));
```

Repository не решает, какой HTTP-статус вернуть. Он только сообщает факт: запись есть или записи нет. Решение об exception остаётся в service/auth слое.

## Почему нет `@Query`

Текущие запросы простые, поэтому query methods достаточно:

- `existsByEmailIgnoreCase`,
- `findByEmailIgnoreCase`.

`@Query` стоит добавлять тогда, когда имя метода становится нечитаемым или нужен явно контролируемый JPQL/SQL. Для учебного проекта важно сначала освоить query methods: это одна из сильных сторон Spring Data JPA.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/service/DOCUMENTATION.md`.
