# `src/test/java/com/example/demo` — корень тестовой ветки

Эта папка повторяет главный Java package приложения (`com.example.demo`) и содержит тесты для учебного Spring Boot проекта. Важно, что тестовый код лежит отдельно от production-кода (`src/main/java`), но использует те же package names. Так Gradle и Spring понимают, что это тесты к основному приложению, а IDE удобно связывает классы.

## Что лежит на этом уровне

- `DemoApplicationTests` — самый базовый smoke/context тест.
- `unit/` — быстрые изолированные unit-тесты без Spring context.
- `web/` — web/contract тесты контроллеров через MockMvc.
- `integration/` — интеграционные тесты нескольких слоёв вместе.
- `e2e/` — end-to-end тесты через настоящий HTTP server.

## Почему есть `DemoApplicationTests`

`DemoApplicationTests` часто создаётся Spring Initializr автоматически. На первый взгляд он кажется "пустым", но он полезен:

```java
@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

Этот тест проверяет, что:

1. Spring Boot может найти `Application`.
2. Все beans создаются без конфликтов.
3. `application.yaml` корректен.
4. Профиль `test` действительно поднимает тестовую БД.
5. Security, JPA, controllers, services и exception handlers не ломают DI.

Если этот тест падает, часто проблема не в бизнес-логике, а в конфигурации: нет bean, неверный env, ошибка в `@ConfigurationProperties`, сломан datasource.

## Почему здесь лежит документация

Проект учебный, поэтому документация рядом с тестами помогает понять не только "что проверяется", но и "зачем это проверяется". В боевых проектах такая документация тоже полезна: она снижает порог входа для новых разработчиков.

## Как читать тестовую ветку

Рекомендуемый порядок:

1. `DemoApplicationTests` — убедись, что приложение вообще стартует.
2. `unit/` — посмотри быстрые проверки отдельных классов.
3. `web/` — посмотри, как проверяется HTTP/JSON contract без полного сервера.
4. `integration/` — посмотри связку Spring context, Security, JPA и SQLite.
5. `e2e/` — посмотри полный сценарий через настоящий HTTP server.

Такой порядок повторяет рост уверенности: от маленьких деталей к целому приложению.
