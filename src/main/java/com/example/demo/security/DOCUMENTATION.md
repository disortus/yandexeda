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
