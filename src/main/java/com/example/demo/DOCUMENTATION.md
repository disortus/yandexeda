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
6. `UserService.create()` вызывает бизнес-валидатор (`UserValidator`)
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

- `spring.datasource.*` — параметры подключения
- `spring.jpa.*` — поведение JPA/Hibernate
- `hikari.*` — пул соединений

Проект работает с H2 по умолчанию, но легко переключается на PostgreSQL через env:

- `DB_URL`
- `DB_USER`
- `DB_PASS`
- `DB_DRIVER`

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

---

## 14. Что изучать дальше после этого проекта

1. Refresh token flow
2. Миграции БД через Flyway/Liquibase
3. Testcontainers с PostgreSQL
4. Observability (metrics/tracing)
5. CI pipeline с quality gates

Этот проект уже заложил архитектурную базу, на которой эти темы можно учить без резкого усложнения.
