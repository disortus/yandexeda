# Spring Boot: общее устройство проекта

Этот файл — обзор **как работает Spring Boot в целом** и как в этом проекте распределены роли между слоями.

---

## 1. Что такое Spring Boot

Spring Boot — это надстройка над Spring Framework, которая:

1. Автоматически настраивает приложение (AutoConfiguration)
2. Поднимает встроенный сервер (Tomcat/Jetty/Undertow)
3. Даёт «стартеры» зависимостей (`spring-boot-starter-web`, `data-jpa`, ...)
4. Упрощает запуск и deployment

Главная идея: ты пишешь бизнес-код, а инфраструктурную рутину Boot берёт на себя.

---

## 2. Как стартует приложение

Точка входа: `Application.java`.

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Что делает `@SpringBootApplication`

Это «комбо»-аннотация из трёх:

- `@Configuration` — класс содержит бин(ы) конфигурации
- `@EnableAutoConfiguration` — включает автоконфиг Boot
- `@ComponentScan` — сканирует пакеты и регистрирует `@Component`, `@Service`, `@Repository`, `@Controller`, `@RestController`

Именно поэтому важно, чтобы `Application` лежал в корневом пакете (`com.example.demo`) — тогда сканируются все подпакеты.

---

## 3. Как работает запрос (request lifecycle)

На примере `POST /users`:

1. Запрос приходит в встроенный Tomcat
2. Проходит через middleware (`RequestLoggingFilter`)
3. Попадает в `UserController`
4. JSON маппится в `UserCreateRequest`
5. `@Valid` запускает аннотационную валидацию DTO
6. `UserService.createByAdmin()` вызывает бизнес-валидатор (`UserValidator`)
7. `UserRepository` сохраняет `User` в БД
8. Ответ маппится в `UserResponse` и отправляется клиенту
9. Если произошла ошибка — `GlobalExceptionHandler` формирует единый JSON ошибки

---

## 4. Dependency Injection (внедрение зависимостей)

Spring создаёт объекты (бины) и связывает их между собой через конструкторы.

Пример:

- `UserController` зависит от `UserService`
- `UserService` зависит от `UserRepository`, `UserValidator`, `Clock`
- `UserValidator` зависит от `UserRepository`

### Почему конструкторная инъекция — best practice

1. Зависимости явные
2. Поля можно делать `final`
3. Проще тестировать
4. Нельзя создать объект в «полу-валидном» состоянии

---

## 5. Пакеты проекта и ответственность

- `controller/` — HTTP API (вход)
- `docs/` — учебные markdown-документы по сквозным темам вроде Swagger schemas
- `dto/` — контракты request/response
- `entity/` — модель хранения (JPA)
- `repository/` — доступ к БД
- `service/` — бизнес-логика
- `validator/` — бизнес-валидация
- `exception/` — централизованная обработка ошибок
- `middleware/` — общая обработка HTTP-запросов
- `config/` — инфраструктурные бины и конфигурации
- `utils/` — технические утилиты без состояния

---

## 6. Подключение к БД

Настройки в `application.yaml`:

- `spring.datasource.*` — параметры подключения,
- `spring.jpa.*` — поведение JPA/Hibernate,
- `hikari.*` — пул соединений.

Актуальная модель подключения profile-based:

- `dev` — PostgreSQL для локальной разработки (`DEV_DB_URL`, `DEV_DB_USER`, `DEV_DB_PASS`),
- `prod` — PostgreSQL для облака (`PROD_DB_URL`, `PROD_DB_USER`, `PROD_DB_PASS`),
- `test` — SQLite in-memory (`TEST_DB_URL`).

Это важная учебная идея: код приложения один и тот же, а окружение меняется через profile/env. Поэтому не нужно менять Java-классы, чтобы переключиться с тестовой SQLite на dev/prod PostgreSQL.

---

## 7. Какие аннотации изучить в первую очередь

### Core / DI
- `@SpringBootApplication`
- `@Component`, `@Service`, `@Repository`, `@Configuration`
- `@Bean`

### Web
- `@RestController`
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@RequestBody`, `@PathVariable`, `@Valid`

### Validation
- `@NotBlank`, `@Email`, `@Size`

### JPA
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`

### Errors
- `@RestControllerAdvice`, `@ExceptionHandler`

### Middleware
- `OncePerRequestFilter`

---

## 8. Как лучше учиться по этому проекту

1. Прочитай `README.md`
2. Прочитай этот файл (`demo/DOCUMENTATION.md`)
3. Пройди слой за слоем (controller → service → repository → entity)
4. Отдельно разберись с валидацией и exception handling
5. Запусти Swagger и руками отправь запросы
6. Попробуй добавить новый use-case (например, `PUT /users/{id}`)

---

## 9. Большое расширение проекта: JWT + роли + security + профили

Над базовым CRUD примером в проект добавлены production-like элементы, чтобы изучение Spring Boot было более реалистичным:

1. JWT аутентификация (`/auth/register`, `/auth/login`)
2. Ролевая модель (`CUSTOMER`, `ADMIN`, `COURIER`)
3. Ограничение доступа по ролям (`@PreAuthorize` + URL policy)
4. Stateless security chain
5. Единые JSON-ответы на 401/403
6. Хеширование пароля BCrypt

Это переводит проект из уровня "просто CRUD" в уровень "учебный мини-сервис с безопасностью".

---

## 10. Расширенный request lifecycle (с auth)

Теперь поток запроса для защищённых endpoint выглядит так:

1. Клиент отправляет `Authorization: Bearer <jwt>`
2. Запрос проходит filter chain
3. `JwtAuthenticationFilter` валидирует токен
4. `AppUserDetailsService` загружает пользователя и authority
5. В `SecurityContext` устанавливается Authentication
6. Правила авторизации проверяют роль
7. Контроллер вызывает сервис
8. Ошибки обрабатываются через `GlobalExceptionHandler` и security handlers

---

## 11. Конфигурация окружений (dev/prod/test)

Проект теперь использует profile-based конфигурацию:

- `dev` — PostgreSQL для локальной разработки
- `prod` — PostgreSQL для облака
- `test` — SQLite для автотестов

Плюс:

- `.env.example` как шаблон,
- `spring.config.import` для загрузки `.env`,
- значения по умолчанию в `${ENV:default}` формате.

Это позволяет запускать один и тот же код в разных окружениях без изменения Java-классов.

---

## 12. Тестовая архитектура

В проекте используются несколько видов тестов одновременно:

1. Unit tests
2. Web tests
3. Integration tests
4. Security authorization tests
5. Repository integration tests
6. Context load smoke test

Это формирует "пирамиду тестирования" и помогает безопасно развивать функциональность.

Подробный гайд: `src/test/DOCUMENTATION.md`.

---

## 13. Где смотреть подробные дополнительные гайды

- `DOCUMENTATION.md` (в корне) — подробный Swagger/OpenAPI guide
- `src/main/resources/DOCUMENTATION.md` — подробный разбор `application.yaml`
- `src/main/java/com/example/demo/security/DOCUMENTATION.md` — deep dive по security
- `src/main/java/com/example/demo/enums/DOCUMENTATION.md` — зачем enums и как их расширять
- `src/main/java/com/example/demo/docs/SWAGGER_SCHEMAS.md` — подробное объяснение Swagger/OpenAPI schemas

---

## 14. Что изучать дальше после этого проекта

1. Refresh token flow
2. Миграции БД через Flyway/Liquibase
3. Testcontainers с PostgreSQL
4. Observability (metrics/tracing)
5. CI pipeline с quality gates

Этот проект уже заложил архитектурную базу, на которой эти темы можно учить без резкого усложнения.


---

## 15. Актуализация архитектуры после аудита

После проверки проекта были исправлены архитектурные несостыковки:

1. `ValidationErrorUtils` перенесён из `utils` в `exception`, потому что он относится к error contract.
2. Добавлена `ApiErrorResponseFactory`, чтобы обычные ошибки и security-ошибки создавались одинаково.
3. Сервисы получили `@Transactional` / `@Transactional(readOnly = true)`, потому что транзакционная граница должна быть на service/use-case уровне.
4. Тестовая БД SQLite стала in-memory, чтобы не создавать бинарные файлы в репозитории.
5. Добавлен e2e-тест, который проверяет приложение через настоящий HTTP server.
6. Swagger/OpenAPI теперь проверяется тестом `/v3/api-docs`, а зависимости приведены к совместимым стабильным версиям.

Это усиливает слоисто-модульную архитектуру: каждый пакет отвечает за свою область и не превращается в "свалку".

---

## 16. Почему структура оставлена слоистой, а не перепакована в feature-пакеты

В Spring Boot часто встречаются два подхода к структуре:

1. **Layer-first**: `controller`, `service`, `repository`, `dto`, `entity`.
2. **Feature-first**: `user`, `auth`, `order`, а внутри каждого feature свои controller/service/repository.

Для большого проекта feature-first часто удобнее: модуль `order` лежит целиком в одном месте. Но для учебного проекта с одной основной сущностью `User` layer-first проще:

- начинающий сразу видит классические Spring-слои,
- легче понять request lifecycle сверху вниз,
- документация по каждому слою лежит рядом с кодом,
- меньше вложенности и меньше прыжков между пакетами.

Поэтому текущая структура — осознанный компромисс: она слоистая, но модульная по ответственности. Каждый пакет имеет чёткую границу:

- `security` не смешивается с `utils`,
- `docs` содержит только учебные markdown-файлы, а не runtime-логику,
- `exception` держит error contract,
- `validator` держит бизнес-проверки,
- `config` держит infrastructure wiring,
- `dto/auth` отделяет auth DTO от пользовательских DTO.

Когда в проекте появятся новые домены, например `orders`, `deliveries`, `payments`, тогда можно перейти к feature-first или hybrid-структуре. Сейчас такой переезд сделал бы учебный пример тяжелее без практической пользы.

## 17. Признаки, что класс лежит в правильном пакете

Задай себе вопросы:

| Вопрос | Пакет |
| --- | --- |
| Класс принимает HTTP-запрос? | `controller` |
| Класс описывает JSON request/response? | `dto` |
| Класс хранится в БД через JPA? | `entity` |
| Класс вызывает БД? | `repository` |
| Класс реализует use-case? | `service` |
| Класс проверяет бизнес-правило? | `validator` |
| Класс формирует/ловит ошибки API? | `exception` |
| Класс связан с JWT, ролями или Spring Security? | `security` |
| Класс создаёт инфраструктурный bean? | `config` |
| Файл объясняет сквозную учебную тему? | `docs` |
| Класс маленький, stateless и не знает о Spring/JPA/Security? | `utils` |

Если класс подходит сразу под несколько строк, это сигнал внимательно посмотреть на ответственность. Например, `ValidationErrorUtils` выглядит как "утилита", но знает о `BindingResult` и `FieldValidationError`, поэтому правильно лежит в `exception`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/controller/example/DOCUMENTATION.md`.
