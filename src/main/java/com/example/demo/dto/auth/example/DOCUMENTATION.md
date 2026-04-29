# `dto/auth/example` — простой пример auth DTO

Здесь лежат минимальные DTO для логина:

- `SimpleLoginRequestExample`,
- `SimpleTokenResponseExample`.

Они показывают базовую форму auth-контракта:

```json
{
  "email": "alice@example.com",
  "password": "Qwerty123!"
}
```

и ответ:

```json
{
  "accessToken": "...",
  "tokenType": "Bearer"
}
```

В основной версии ответ богаче: `AuthResponse` содержит ещё `expiresAt` и `user`, а request DTO имеют Bean Validation и Swagger schema.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/entity/example/DOCUMENTATION.md`.
