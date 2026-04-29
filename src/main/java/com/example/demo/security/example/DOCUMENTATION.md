# `security/example` — простой пример security-слоя

`SimpleTokenCheckerExample` показывает самый первый шаг JWT-authentication:

1. прочитать `Authorization` header,
2. проверить префикс `Bearer `,
3. достать строку токена.

Это ещё не настоящая security-логика. Здесь нет:

- проверки подписи JWT,
- проверки issuer,
- проверки expiration,
- загрузки пользователя,
- заполнения `SecurityContext`.

В основной версии всё это делают:

- `JwtAuthenticationFilter`,
- `JwtService`,
- `AppUserDetailsService`,
- `SecurityConfig`.

## Почему пример такой маленький

Security быстро становится сложной. Поэтому полезно сначала увидеть один простой шаг, а потом изучать настоящий filter chain.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `JwtAuthenticationFilter.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/docs/example/DOCUMENTATION.md`.
