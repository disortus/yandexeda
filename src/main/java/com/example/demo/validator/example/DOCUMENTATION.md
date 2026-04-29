# `validator/example` — простой пример validator-слоя

`SimpleNameValidatorExample` показывает бизнес-проверку в самом простом виде:

```java
if (name == null || name.isBlank()) {
    throw new SimpleValidationExceptionExample("Name is required");
}
```

## Почему это не DTO validation

DTO validation отвечает на вопрос: "правильно ли заполнен request по форме?"

Validator layer отвечает на вопрос: "можно ли выполнить бизнес-действие?"

В примере проверка очень простая. В основной версии `UserValidator` проверяет уникальность email через repository.

## Что изучить после примера

После этого файла открой `../DOCUMENTATION.md`, затем `UserValidator.java`.

## Дальше читать

Следующий документ: `src/main/java/com/example/demo/utils/example/DOCUMENTATION.md`.
