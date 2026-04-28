# Middleware layer

## Что такое middleware
Middleware — это промежуточный слой, который обрабатывает запрос **до** и/или **после** контроллера.

В Spring Boot для этого чаще всего используют:

- `Filter`
- `HandlerInterceptor`

---

## Что использовать: Filter vs Interceptor

### Filter
Низкоуровневый уровень Servlet API:

- работает до Spring MVC handler mapping
- подходит для логирования, CORS, security pre-processing
- полезен для кросс-срезовой инфраструктурной логики

### Interceptor
Уровень Spring MVC:

- знает о handler/endpoint
- удобен для логики, завязанной на контроллеры

---

## Как реализовано в проекте

`RequestLoggingFilter`:

- наследуется от `OncePerRequestFilter`
- срабатывает один раз на запрос
- замеряет время обработки
- пишет в лог: HTTP метод, URI, статус, latency

Пример:
`POST /users -> 201 (18 ms)`

---

## Ключевая аннотация и класс

### `@Component`
Регистрирует фильтр как бин, чтобы Spring автоматически включил его в цепочку фильтров.

### `OncePerRequestFilter`
Гарантирует, что фильтр выполнится один раз на один запрос (удобно и безопасно для логики middleware).

---

## Best practices

1. Не помещай бизнес-логику в middleware.
2. Логи не должны содержать чувствительные данные (пароли/токены).
3. Для корреляции запросов добавляй trace-id.
4. Тяжёлые операции в filter/interceptor — плохая идея.
