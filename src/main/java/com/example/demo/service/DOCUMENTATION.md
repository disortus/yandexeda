# Service layer

## Назначение
`service` — слой бизнес-логики. Он соединяет web-слой (controller) и data-слой (repository).

Именно здесь принимаются бизнес-решения:

- что можно делать,
- в каком порядке,
- какие ошибки бросать,
- как маппить данные.

---

## Что должно быть в service

1. Бизнес-правила
2. Оркестрация вызовов repository/validator
3. Маппинг Entity → Response DTO
4. Нормализация данных

---

## Что реализовано в `UserService`

- `getAll()` — достаёт пользователей и превращает в `UserResponse`
- `getById(id)` — если нет пользователя, бросает `ResourceNotFoundException`
- `getByEmail(email)` — достаёт текущего пользователя по email из JWT subject
- `createByAdmin(request)`:
  1. вызывает `UserValidator`
  2. нормализует email через `EmailUtils`
  3. проставляет `createdAt` через `Clock` бин
  4. сохраняет сущность

---

## Почему используется `Clock`

Вместо `LocalDateTime.now()` напрямую внедряется `Clock` из config.

Плюсы:

1. легче тестировать время
2. централизованный контроль таймзоны/источника времени
3. меньше скрытых зависимостей

---

## Аннотации в service

### `@Service`
Маркер сервисного компонента:

- класс будет зарегистрирован как бин,
- участвует в DI,
- повышает читаемость архитектуры.

---

## Best practices

1. Контроллер тонкий, сервис содержательный.
2. Не складывай SQL в сервис — только через repository.
3. Исключения кидай осмысленные (`NotFound`, `BusinessValidation`, ...).
4. Дублируемую логику выноси в utils/validator.

---

## Что реализовано в `AuthService`

`AuthService` отвечает за use-cases аутентификации:

- `register(request)` — создаёт нового `CUSTOMER`, хэширует пароль и возвращает JWT,
- `login(request)` — проверяет пароль и возвращает JWT,
- `buildAuthResponse(user)` — собирает единый ответ авторизации.

Важно: `AuthService` не возвращает наружу `User` entity. Он формирует `AuthResponse`, внутри которого лежит безопасный `UserResponse` без `passwordHash`.

## Почему транзакции стоят в service-слое

В проекте используются:

```java
@Transactional
@Transactional(readOnly = true)
```

Транзакция описывает границу use-case:

- регистрация пользователя — одна write-транзакция,
- создание пользователя админом — одна write-транзакция,
- чтение профиля — read-only транзакция.

Почему не controller:

- controller отвечает за HTTP, а не за состояние БД,
- один use-case может использовать несколько repository,
- service лучше выражает бизнес-границу операции.

Почему не repository:

- repository слишком низкоуровневый,
- транзакция часто охватывает несколько repository-вызовов и бизнес-проверок.

## Как сервисы связаны с другими слоями

`AuthService`:

1. получает DTO из controller,
2. нормализует email через `EmailUtils`,
3. проверяет уникальность через `UserValidator`,
4. хэширует пароль через `PasswordEncoder`,
5. сохраняет пользователя через `UserRepository`,
6. генерирует JWT через `JwtService`,
7. возвращает response DTO.

`UserService`:

1. получает запрос из `UserController`,
2. читает или сохраняет `User` через `UserRepository`,
3. использует `UserValidator` для бизнес-проверок,
4. маппит entity в `UserResponse`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/validator/DOCUMENTATION.md`.
