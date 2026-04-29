# Security layer (`src/main/java/com/example/demo/security`)

Этот документ подробно объясняет, как в проекте реализована безопасность API: JWT-аутентификация, роли, обработка ошибок security и базовые hardening-меры.

---

## 1. Зачем отдельный security-слой

Security — это отдельная инфраструктурная подсистема, а не часть бизнес-логики.

Она отвечает за:

1. проверку, кто делает запрос (authentication),
2. проверку, что ему разрешено (authorization),
3. стандартизированную обработку security-ошибок,
4. минимизацию типовых уязвимостей (session fixation, слабые пароли, избыточные CORS и т.д.).

---

## 2. Компоненты security-слоя в этом проекте

### `JwtService`

Функции:

- генерирует access token,
- извлекает `subject` (email),
- извлекает `expiration`,
- валидирует токен.

Важно:

- используется HS256 и `secretKey`,
- есть проверка длины секрета (`>= 32 bytes`),
- в claims кладётся список ролей.

### `JwtAuthenticationFilter`

`OncePerRequestFilter`, который:

1. достаёт `Authorization: Bearer ...`,
2. валидирует токен через `JwtService`,
3. загружает пользователя через `UserDetailsService`,
4. кладёт Authentication в `SecurityContext`.

Если токен невалиден — контекст очищается, а запрос дальше обрабатывается как неаутентифицированный.

### `AppUserDetailsService`

Адаптер между доменной моделью `User` и Spring Security `UserDetails`.

- грузит пользователя по email,
- преобразует роль в `ROLE_*` authority.

### `RestAuthenticationEntryPoint`

Единый JSON-ответ для `401 Unauthorized`.

### `RestAccessDeniedHandler`

Единый JSON-ответ для `403 Forbidden`.

---

## 3. SecurityConfig: центральная политика безопасности

В `SecurityConfig` определены ключевые решения:

1. **stateless** режим (`SessionCreationPolicy.STATELESS`),
2. отключены form login и http basic,
3. подключён `JwtAuthenticationFilter`,
4. настроены CORS правила из properties,
5. настроены security headers,
6. прописаны правила доступа по ролям и endpoint'ам.

---

## 4. Матрица доступа (high-level)

- `/auth/register`, `/auth/login` — публичные,
- `/users/me` — любой аутентифицированный пользователь,
- `GET /users`, `GET /users/{id}` — `ADMIN` и `COURIER`,
- `POST /users` — только `ADMIN`.

Такой подход даёт понятную ролевую модель:

- `CUSTOMER` — базовый пользователь,
- `COURIER` — ограниченный staff-доступ на чтение,
- `ADMIN` — расширенные права.

---

## 5. Почему пароли хэшируются и как

Пароль никогда не хранится как plain text.

Используется:

- `BCryptPasswordEncoder(12)`

Это даёт:

1. соль внутри алгоритма,
2. адаптивную сложность,
3. защиту при утечке БД.

---

## 6. JWT best practices, которые применены

1. `issuer` у токена задан явно,
2. есть срок жизни токена (`exp`),
3. проверяется валидность и срок,
4. secret вынесен в env (`JWT_SECRET`),
5. subject = email пользователя.

Что можно добавить на следующем этапе:

- refresh tokens,
- revocation/blacklist,
- key rotation,
- аудит security-событий.

---

## 7. CORS и почему это важно

CORS-конфигурация вынесена в `SecurityProperties` и настраивается через env.

Это правильно, потому что:

- для dev можно разрешить localhost,
- для prod — только реальные домены фронта,
- не нужно менять код при смене окружения.

---

## 8. Security hardening, который есть в проекте

1. Stateless auth (без серверной сессии)
2. Password hashing (BCrypt)
3. Ограничение endpoint по ролям
4. Безопасные JSON-ответы на 401/403
5. Security headers (`X-Content-Type-Options`, frame deny, referrer policy)
6. CORS под контролем

---

## 9. Частые ошибки в security и как их избежать

1. **Секрет токена в git**  
   Никогда не хранить production secret в репозитории.

2. **Слишком длинный TTL**  
   Короткий access token + refresh flow безопаснее.

3. **`permitAll()` слишком широко**  
   Всегда явно ограничивать только нужные endpoint.

4. **Отсутствие role-based проверок в методах**  
   Использовать `@PreAuthorize` и/или строгий URL policy.

5. **Смешивание бизнес-логики и security-логики**  
   Security — отдельный слой.

---

## 10. Рекомендации для развития проекта

1. Добавить refresh tokens и logout/blacklist.
2. Перейти к централизованному ключ-хранилищу (Vault/KMS/Secrets Manager).
3. Добавить security audit logs.
4. Добавить интеграционные тесты негативных JWT-сценариев (expired, malformed, wrong signature).

---

## 11. Короткий итог

Текущая реализация security уже покрывает учебно-практический минимум правильно:

- JWT,
- роли,
- хэширование паролей,
- stateless policy,
- понятные 401/403 ответы,
- Swagger-friendly схема auth.

Это хорошая база, чтобы изучить Spring Security не "в вакууме", а на реальном API-потоке.


---

## 12. Дополнительные hardening-правки после аудита

После проверки реализации security были внесены дополнительные улучшения:

1. `JwtService` теперь требует совпадение issuer при парсинге токена (`requireIssuer`). Это защищает от принятия токенов, выпущенных другим сервисом/окружением.
2. `JwtAuthenticationFilter` обрабатывает `UsernameNotFoundException`. Если токен валиден криптографически, но пользователь уже удалён из БД, запрос должен стать неаутентифицированным, а не ломать filter chain.
3. `RestAuthenticationEntryPoint` и `RestAccessDeniedHandler` больше не склеивают JSON руками. Они используют `ObjectMapper` и `ApiErrorResponseFactory`.
4. CORS preflight (`OPTIONS /**`) явно разрешён, чтобы браузерные frontend-запросы корректно проходили предварительную проверку.
5. `SecurityProperties` валидируется через `@Validated` и `@NotEmpty`, чтобы приложение не стартовало с пустыми CORS списками.
6. `JwtProperties.secret` валидируется через `@Size(min = 32)`, чтобы слабый HS256 secret обнаруживался на старте.

---

## 13. Почему security-классы не надо переносить в `utils`

Вопрос "можно ли часть security закинуть в utils" полезный, потому что помогает понять архитектурные границы.

В `utils` стоит класть только маленькие stateless функции без знания о Spring Security. Например:

```java
EmailUtils.normalize(email)
```

Но эти классы не являются utils:

- `JwtAuthenticationFilter` — часть servlet/security filter chain,
- `JwtService` — сервис генерации/валидации токенов,
- `AppUserDetailsService` — адаптер между БД и Spring Security,
- `RestAuthenticationEntryPoint` — infrastructure handler для 401,
- `RestAccessDeniedHandler` — infrastructure handler для 403.

Они зависят от security-контекста, HTTP, токенов, пользователей и framework contracts. Поэтому пакет `security` — правильное место.

---

## 14. Почему security handlers используют `ObjectMapper`, но не требуют отдельного `JacksonConfig`

`RestAuthenticationEntryPoint` и `RestAccessDeniedHandler` получают `ObjectMapper` через конструктор:

```java
public RestAuthenticationEntryPoint(ObjectMapper objectMapper, ApiErrorResponseFactory errorResponseFactory)
```

Это правильный подход:

- handler не создаёт JSON вручную,
- формат 401/403 совпадает с `ApiErrorResponse`,
- Jackson безопасно экранирует строки,
- настройка сериализации остаётся централизованной в Spring Boot.

Отдельный `JacksonConfig` в текущем проекте не нужен, потому что Boot auto-configuration уже создаёт `ObjectMapper`. Добавлять свой конфиг стоит только при конкретном требовании: например, если нужен `snake_case`, custom serializer или особый формат дат.

## 15. Какие security-сценарии покрыты тестами

Текущие тесты проверяют:

- анонимный запрос к `/users` получает `401`,
- `CUSTOMER` получает `403` на staff endpoint,
- `COURIER` может читать `/users`,
- `COURIER` не может создавать пользователей,
- `ADMIN` может читать `/users`,
- malformed JWT превращается в единый JSON `401`,
- e2e-сценарий проходит регистрацию, логин admin, создание courier и ошибки `401/403/409`.

Это полезно для обучения: security лучше изучать не только по конфигу, но и по тестам, где видно реальное поведение системы.
