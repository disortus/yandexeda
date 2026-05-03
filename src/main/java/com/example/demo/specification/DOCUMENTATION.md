# 🔹 Что такое фильтры

Фильтры — это когда ты хочешь получать данные не просто findAll(), а:

/restaurant?city=Almaty&ratingFrom=4.0&ratingTo=5.0

---

# ❗️ Где должна быть логика фильтров

Controller → принимает фильтр DTO
Service → решает как фильтровать
Repository → выполняет запрос

---

# 🔹 1. DTO для фильтров

Фильтры — это просто DTO:

public record RestaurantFilterDto(
        String city,
        Double ratingFrom,
        Double ratingTo
) {
}

📌 Важно: все поля nullable → если null = фильтр не применяем

---

# 🔹 2. Controller

@GetMapping
public List<RestaurantResponseDto> getRestaurants(
        RestaurantFilterDto filter
) {
    return restaurantService.getRestaurants(filter);
}

Spring сам подставит query params в DTO:

GET /restaurants?city=Almaty&ratingFrom=4

---

# 🔹 3. Самый важный момент — Repository

Есть 3 способа:

### ❌ Плохой (не масштабируется)

findByCityAndRatingBetween(...)

Если фильтров 5 → методов станет 20+

---

### ✅ Правильный — Specification (JPA)

Тиммейт должен использовать:

JpaSpecificationExecutor

---

## Repository

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long>,
                JpaSpecificationExecutor<Restaurant> {
}

---

# 🔹 4. Specification (ядро фильтров)

Отдельный класс:

public class RestaurantSpecifications {

    public static Specification<Restaurant> withFilter(RestaurantFilterDto filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.city() != null) {
                predicates.add(cb.equal(root.get("city"), filter.city()));
            }

            if (filter.ratingFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("rating"), filter.ratingFrom()
                ));
            }

            if (filter.ratingTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("rating"), filter.ratingTo()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

📌 Да, это тот самый jakarta.persistence.criteria.Predicate

---

# 🔹 5. Service

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository repository;
    private final RestaurantMapper mapper;

    public List<RestaurantResponseDto> getRestaurants(RestaurantFilterDto filter) {

        Specification<Restaurant> spec =
                RestaurantSpecifications.withFilter(filter);

        return repository.findAll(spec)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }
}

---

# 🔹 Итоговая схема

Controller
  ↓
Filter DTO
  ↓
Service
  ↓
Specification (строит Predicate)
  ↓
Repository (JpaSpecificationExecutor)
  ↓
Database

---

# 🔹 Частые ошибки 

### ❌ 1. Пихать фильтры в Repository

findByCityAndRatingAndNameAnd...

→ взорвётся по количеству методов

---

### ❌ 2. Делать логику в Controller

Controller не должен думать.

---

### ❌ 3. Не проверять null

cb.equal(...) // если filter null → ошибка или лишний фильтр
# 🔷 ЧАСТЬ 1. Общая картина стека

Spring Boot  — каркас приложения (запуск, DI, автоконфиг)
JPA          — интерфейс (как работать с БД через объекты)
Hibernate    — реализация JPA (он реально делает SQL)
Jakarta      — пакет аннотаций (jakarta.persistence.*, jakarta.validation.*)

📌 Проще:

Ты пишешь Java код → JPA → Hibernate → SQL → Postgres

---

# 🔷 ЧАСТЬ 2. Как работать с Repo / DTO / Filters

---

## 🔹 1. Repository (JPA + Hibernate)

### Что это

Repository = слой доступа к БД

Ты НЕ пишешь SQL → Hibernate делает это за тебя.

---

## Минимум:

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long>,
                JpaSpecificationExecutor<Restaurant> {
}

---

## Что даёт JpaRepository:

findAll()
findById()
save()
deleteById()

---

## Что даёт JpaSpecificationExecutor:

findAll(Specification spec)

→ используется для фильтров

---

# 🔹 2. DTO (jakarta + чистый API слой)

DTO — это контракт API, не БД.

---

## Пример

public record RestaurantResponseDto(
        Long id,
        String name,
        String city,
        Double rating
) {}

public record RestaurantFilterDto(
        String city,
        Double ratingFrom,
        Double ratingTo
) {}

---

📌 Важно:

* DTO НЕ имеет аннотаций @Entity
* DTO НЕ связан с Hibernate
* DTO — это просто Java объект

---

## Валидация (jakarta.validation)

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantDto(
        @NotBlank String name,
        @NotBlank String city
) {}

Controller:

public ResponseEntity<?> create(@Valid @RequestBody CreateRestaurantDto dto)

---

# 🔹 3. Filters (JPA Criteria API / Specification)

Вот тут используется jakarta.persistence.criteria

---

## Как это работает

Ты НЕ пишешь SQL:

WHERE city = 'Almaty' AND rating >= 4

Ты собираешь это через Java:

---

## Specification

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class RestaurantSpecifications {

    public static Specification<Restaurant> withFilter(RestaurantFilterDto filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.city() != null) {
                predicates.add(cb.equal(root.get("city"), filter.city()));
            }

            if (filter.ratingFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("rating"), filter.ratingFrom()
                ));
            }

            if (filter.ratingTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("rating"), filter.ratingTo()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

---

## Что тут происходит

root  — таблица (Restaurant)
cb    — CriteriaBuilder (конструктор SQL)
Predicate — кусок WHERE

---

## Service

public List<RestaurantResponseDto> getAll(RestaurantFilterDto filter) {

    Specification<Restaurant> spec =
            RestaurantSpecifications.withFilter(filter);

    return repository.findAll(spec)
            .stream()
            .map(mapper::toDto)
            .toList();
}

---


Repository → выполняет запрос
DTO → общается с фронтом
Specification → строит WHERE
Hibernate → превращает всё в SQL

---

# ❗️ Частые ошибки

### ❌ Писать SQL руками

→ ломает архитектуру

---

### ❌ Возвращать Entity в Controller

→ утечка данных + ломается API

---

### ❌ Делать фильтры в Controller

→ мусорный код

---