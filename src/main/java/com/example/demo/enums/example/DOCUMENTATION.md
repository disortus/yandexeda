# `enums/example` — простой пример enum-слоя

`SimpleRoleExample` показывает идею enum:

```java
READER,
MANAGER
```

Enum полезен, когда есть фиксированный набор значений. Он лучше строк, потому что компилятор защищает от опечаток.

## Как читать пример

```java
public boolean canReadUsers() {
    return this == MANAGER;
}
```

Метод показывает, что enum может хранить не только значения, но и простую связанную логику.

В основной версии проекта `UserRole` хранит роли:

- `CUSTOMER`,
- `ADMIN`,
- `COURIER`.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `UserRole.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/repository/example/DOCUMENTATION.md`.
