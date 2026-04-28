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
