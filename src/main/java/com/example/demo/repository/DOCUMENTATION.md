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
