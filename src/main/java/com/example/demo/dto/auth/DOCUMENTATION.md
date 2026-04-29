# `dto/auth` — DTO для аутентификации

Эта папка содержит DTO, которые относятся только к auth-сценариям: регистрации, логину и выдаче JWT.

## Какие классы здесь лежат

### `RegisterRequest`

Входной JSON для `POST /auth/register`.

Поля:

- `name` — имя пользователя,
- `email` — email, который станет логином и JWT subject,
- `password` — исходный пароль, который будет захэширован через BCrypt.

Важные аннотации:

- `@NotBlank` — поле обязательно и не должно быть пустым,
- `@Email` — базовая проверка email-формата,
- `@Size` — ограничение длины,
- `@Pattern` — требование сложности пароля,
- `@Schema` — описание поля для Swagger UI.

Регистрация всегда создаёт пользователя с ролью `CUSTOMER`. Клиент не может сам выбрать `ADMIN` или `COURIER` через публичную регистрацию.

### `LoginRequest`

Входной JSON для `POST /auth/login`.

Поля:

- `email`,
- `password`.

Здесь пароль не проверяется через сложный `@Pattern`, потому что пользователь может логиниться старым паролем, а политика сложности нужна на этапе создания/изменения пароля. На логине важно проверить, что пароль не пустой, а дальше сравнить его с BCrypt-хэшем.

### `AuthResponse`

Успешный ответ регистрации или логина.

Поля:

- `accessToken` — JWT access token,
- `tokenType` — сейчас `BEARER`,
- `expiresAt` — время окончания токена в UTC,
- `user` — безопасный `UserResponse` без `passwordHash`.

## Почему auth DTO не лежат в `security`

`security` — это infrastructure: фильтры, JWT-сервис, `UserDetailsService`, handlers 401/403.

DTO — это внешний API-контракт. Даже если DTO относятся к логину, они всё равно описывают JSON request/response, поэтому лежат в `dto/auth`.

## Как расширять эту папку

Если позже появятся refresh tokens, сюда логично добавить:

- `RefreshTokenRequest`,
- `RefreshTokenResponse`,
- `LogoutRequest` (если logout принимает тело),
- `ChangePasswordRequest`.

Но не стоит заранее добавлять пустые DTO "на будущее". Хорошая структура растёт вместе с реальными use-cases.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/entity/DOCUMENTATION.md`.
