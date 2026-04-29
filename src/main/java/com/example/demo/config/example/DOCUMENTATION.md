# `config/example` — простой пример config-слоя

`SimpleAppSettingsExample` показывает самую простую идею конфигурации: настройки лучше хранить в одном объекте, а не разбрасывать строками по коду.

В основной версии проекта эту роль выполняют:

- `JwtProperties`,
- `SecurityProperties`,
- `SecurityConfig`,
- `OpenApiConfig`,
- `ClockConfig`.

## Почему пример без `@Configuration`

Если добавить `@Configuration` или `@ConfigurationProperties`, пример станет настоящим Spring bean и начнёт участвовать в runtime. Для учебного `example/` это не нужно.

Сначала полезно понять простую идею:

```java
new SimpleAppSettingsExample("demo", 8080, true)
```

А потом переходить к настоящей версии:

```java
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties { ... }
```

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md` и посмотри, как простая идея настроек превращается в полноценный Spring Boot config layer.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/middleware/example/DOCUMENTATION.md`.
