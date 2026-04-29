# `repository/example` — простой пример repository-слоя

`SimpleUserRepositoryExample` показывает идею repository как контракта для работы с данными:

- найти все записи,
- найти по id,
- сохранить запись.

## Почему пример не extends `JpaRepository`

Если наследоваться от `JpaRepository`, Spring Data может начать воспринимать пример как настоящий repository-контракт.

Для первого чтения достаточно увидеть идею:

```java
Optional<SimpleUserEntityExample> findById(Long id);
```

В основной версии эта идея превращается в:

```java
public interface UserRepository extends JpaRepository<User, Long>
```

и Spring Data JPA сам создаёт реализацию.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `UserRepository.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/config/example/DOCUMENTATION.md`.
