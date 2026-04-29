# `middleware/example` — простой пример middleware-слоя

Middleware — это логика вокруг запроса: до controller, после controller или на обоих этапах.

В примере:

- `SimpleRequestLogExample` хранит данные о запросе,
- `SimpleRequestLoggerExample` форматирует строку лога.

Основная версия проекта делает то же самое на уровне servlet filter:

- создаёт `X-Request-Id`,
- замеряет время,
- логирует method/path/status/duration,
- не логирует body и секреты.

## Почему пример без `OncePerRequestFilter`

`OncePerRequestFilter` подключил бы пример к реальной filter chain. Для первого чтения проще увидеть обычные данные и форматирование.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `RequestLoggingFilter.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/security/example/DOCUMENTATION.md`.
