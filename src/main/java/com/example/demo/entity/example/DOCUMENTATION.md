# `entity/example` — простой пример entity-слоя

`SimpleUserEntityExample` показывает идею entity как объекта, который хранит состояние пользователя:

- `id`,
- `name`,
- `email`,
- `role`.

## Почему пример без `@Entity`

Если добавить `@Entity`, Hibernate начнёт считать пример настоящей таблицей. Для учебного примера это лишнее.

Сначала важно понять обычный Java-объект с полями. Потом в основной версии к нему добавляются JPA-аннотации:

- `@Entity`,
- `@Table`,
- `@Id`,
- `@GeneratedValue`,
- `@Column`,
- `@Enumerated`.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `User.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/enums/example/DOCUMENTATION.md`.
