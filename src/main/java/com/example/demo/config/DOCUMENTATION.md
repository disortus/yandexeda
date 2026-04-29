# Config layer

## Назначение
Слой `config` содержит инфраструктурные настройки и бины, которые нужны приложению на уровне платформы.

Это место, где настраивают:

- источники времени
- swagger/openapi
- security/cors/cache/message converters
- интеграции с внешними системами

---

## Что реализовано в проекте

### `ClockConfig`

```java
@Configuration
public class ClockConfig {
    @Bean
    public Clock appClock() {
        return Clock.systemUTC();
    }
}
```

Зачем:

- сервисы получают время через DI
- легче писать тесты (можно подменить clock)
- единое поведение времени во всём приложении

### `OpenApiConfig`

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI demoOpenAPI() { ... }
}
```

Зачем:

- описывает API-документацию
- влияет на Swagger UI (`/swagger-ui.html`)

---

## Основные аннотации

### `@Configuration`
Класс конфигурации Spring. В нём обычно создают бины через `@Bean`.

### `@Bean`
Метод, чей результат становится managed-объектом в Spring context.

Пример: `Clock`, `ObjectMapper`, `RestTemplate`, `OpenAPI`.

---

## Когда класть код в `config`, а когда нет

Клади в `config`, если это:

- инфраструктура и wiring
- не бизнес-логика
- cross-cutting configuration

Не клади в `config`:

- операции с данными домена
- бизнес-валидацию
- логику use-case

---

## Best practices

1. Конфиги дели по ответственности: `DatabaseConfig`, `SecurityConfig`, `OpenApiConfig`.
2. Не перегружай один конфиг-класс всем подряд.
3. Используй profiles (`dev`, `test`, `prod`) и env-переменные.
4. Для параметров используй `application.yaml` + `@ConfigurationProperties` (когда настроек много).

---

## Дополнение: новые конфиги безопасности и env binding

После расширения проекта в `config` появились дополнительные инфраструктурные классы:

### `SecurityConfig`

Центральная настройка Spring Security:

- stateless политика,
- подключение JWT-фильтра,
- endpoint authorization rules,
- CORS, headers, 401/403 handlers.

### `JwtProperties`

Типобезопасная привязка параметров из `application.yaml`/env:

- `app.jwt.issuer`
- `app.jwt.secret`
- `app.jwt.access-token-minutes`

Плюс есть валидация (`@Validated`, `@NotBlank`, `@Min`) — это помогает упасть рано, если конфигурация неправильная.

### `SecurityProperties`

Хранит конфиг CORS списков:

- origins
- methods
- headers
- exposed headers

Смысл: изменения окружения (dev/prod) можно делать без перекомпиляции приложения.

---

## Почему `@ConfigurationProperties` лучше для групп настроек

Когда параметров много, лучше не использовать десятки `@Value`.

Преимущества `@ConfigurationProperties`:

1. группировка настроек в один класс,
2. проще читать и поддерживать,
3. удобнее валидировать,
4. лучше автокомплит в IDE,
5. проще писать документацию к настройкам.


---

## Дополнение: `ObjectMapper` и почему отдельный `JacksonConfig` здесь не нужен

В проекте security handlers используют `ObjectMapper`, чтобы возвращать JSON безопасно, а не склеивать строки руками.

Важно: отдельный `JacksonConfig` был убран, потому что Spring Boot уже создаёт production-ready `ObjectMapper` автоматически через auto-configuration.

Почему это лучше для текущего учебного проекта:

1. меньше инфраструктурного кода,
2. проще структура `config`,
3. меньше риск случайно переопределить Boot-настройки,
4. security handlers всё равно получают `ObjectMapper` через DI,
5. даты `LocalDateTime` продолжают сериализоваться корректно благодаря Boot/Jackson auto-configuration.

Когда отдельный Jackson-конфиг всё-таки нужен:

- нужно изменить глобальный формат дат,
- нужно добавить custom serializer/deserializer,
- нужно настроить naming strategy (`snake_case`, `kebab-case`),
- нужно включить/выключить конкретные Jackson features.

Best practice: не добавлять конфиг "на всякий случай". Добавляй его, когда есть реальное требование, которое не покрывается стандартной auto-configuration.

---

## Дополнение: почему проект переведён на стабильный Spring Boot

Изначально была RC-версия Spring Boot 4.1.0-RC1. Это привело к runtime-проблеме Swagger/OpenAPI: `/v3/api-docs` падал с `NoSuchMethodError`.

Для учебного проекта важнее стабильность и предсказуемость, поэтому теперь используется Spring Boot `3.4.5` и springdoc `2.8.6`. Это снижает риск несовместимостей и позволяет изучать Spring Boot, а не особенности release candidate зависимостей.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/security/DOCUMENTATION.md`.
