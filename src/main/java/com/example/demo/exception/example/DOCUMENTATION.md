# `exception/example` — простой пример exception-слоя

Здесь показана базовая идея error handling:

1. код бросает понятное исключение,
2. отдельный mapper превращает исключение в error response,
3. клиент получает предсказуемый формат.

В примере:

- `SimpleValidationExceptionExample` — простое исключение,
- `SimpleErrorResponseExample` — простой DTO ошибки,
- `SimpleExceptionMapperExample` — преобразователь exception -> response.

В основной версии проекта эта идея реализована через:

- `GlobalExceptionHandler`,
- `ApiErrorResponseFactory`,
- `ApiErrorResponse`,
- `FieldValidationError`,
- `ApiErrorCode`.

## Почему пример без `@RestControllerAdvice`

`@RestControllerAdvice` сделал бы пример настоящей частью обработки ошибок приложения. Здесь пример должен быть безопасным и изолированным.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `GlobalExceptionHandler`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/entity/example/DOCUMENTATION.md`.
