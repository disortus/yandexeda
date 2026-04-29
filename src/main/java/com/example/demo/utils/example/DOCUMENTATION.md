# `utils/example` — простой пример utils-слоя

`SimpleTextUtilsExample` показывает, какой код подходит для `utils`:

- маленький,
- stateless,
- без Spring-зависимостей,
- без доступа к БД,
- без бизнес-сценариев.

Метод:

```java
normalizeName(String value)
```

делает только одну вещь: безопасно обрезает пробелы.

В основной версии похожую роль выполняет `EmailUtils.normalize(...)`.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `EmailUtils.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/exception/example/DOCUMENTATION.md`.
