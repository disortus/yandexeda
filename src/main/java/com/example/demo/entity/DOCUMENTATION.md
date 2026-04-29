# Entity layer

## Назначение
Entity — это модель данных на уровне БД. Через JPA/Hibernate Entity превращается в таблицу.

В этом проекте entity-слой представлен классом `User`.

---

## Ключевые JPA-аннотации и когда использовать

### `@Entity`
Обозначает класс как JPA-сущность (то есть объект, который Hibernate умеет сохранять в БД).

### `@Table(name = "users")`
Позволяет явно указать имя таблицы.

Используй, если:
- имя таблицы отличается от имени класса,
- нужна явность и предсказуемость схемы.

### `@Id`
Primary key сущности.

### `@GeneratedValue(strategy = GenerationType.IDENTITY)`
Автогенерация id базой данных.

`IDENTITY` хорошо подходит для PostgreSQL с автоинкрементом; в тестах Hibernate адаптирует стратегию под SQLite-диалект.

### `@Column(...)`
Управляет колонкой БД:

- `nullable = false` — запрет `null`
- `unique = true` — уникальность
- `length = N` — длина VARCHAR

---

## Важные правила для Entity

1. Нужен пустой конструктор (JPA создаёт объекты через reflection).
2. Держи entity ближе к БД, а не к API.
3. Бизнес-правила сложнее «not null / unique» лучше проверять в service/validator.
4. Не смешивай с DTO: у них разные задачи.

---

## Что реализовано в `User`

- `id` — primary key
- `name` — обязательное поле длиной до 50
- `email` — обязательное уникальное поле
- `passwordHash` — BCrypt-хэш пароля, а не исходный пароль
- `role` — роль пользователя (`CUSTOMER`, `ADMIN`, `COURIER`) как `EnumType.STRING`
- `createdAt` — дата создания записи

---

## Почему в Entity есть `passwordHash`, но его нет в DTO ответа

`passwordHash` нужен приложению, чтобы проверять пароль при логине:

```java
passwordEncoder.matches(rawPassword, user.getPasswordHash())
```

Но это поле нельзя отдавать клиенту. Даже хэш пароля является чувствительной информацией: при утечке response/logs злоумышленник получает материал для offline brute-force атаки.

Поэтому:

- entity хранит `passwordHash`,
- `UserResponse` его не содержит,
- controller возвращает DTO, а не entity.

Это один из главных смыслов разделения `entity` и `dto`.

## Почему роль хранится как строка

В entity используется:

```java
@Enumerated(EnumType.STRING)
private UserRole role;
```

Так в БД сохраняется `CUSTOMER`, `ADMIN` или `COURIER`, а не числовой индекс enum.

Это безопаснее для развития проекта: если поменять порядок enum-констант, данные в БД не "переедут" на другую роль.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/enums/DOCUMENTATION.md`.
