# Phase 4, Topic 5: Optional Class

Optional is a container type introduced in Java 8 to represent a value that may be present or absent in a safer and more explicit way.

It was designed to reduce the frequent problem of `NullPointerException` caused by returning `null` from methods.

---

## Why Optional Exists

Before Optional, methods often returned `null` when no result was found.

```java
String name = findEmployeeByName("Bharath");
System.out.println(name.toUpperCase()); // CRASH if name is null
```

The old defensive style looked like this:

```java
String name = findEmployeeByName("Bharath");
if (name != null) {
    System.out.println(name.toUpperCase());
} else {
    System.out.println("Employee not found");
}
```

Optional makes the possibility of “no value” explicit.

```java
Optional<String> name = findEmployeeByName("Bharath");
String result = name.orElse("Employee not found");
System.out.println(result.toUpperCase());
```

> Optional is mainly meant for method return types when a result may legitimately be absent.

---

## Creating Optional Objects

```java
import java.util.Optional;

// 1. Optional.of() -> wraps a non-null value
Optional<String> opt1 = Optional.of("Bharath");
// Optional<String> opt2 = Optional.of(null); // throws NullPointerException

// 2. Optional.ofNullable() -> safe for possible null values
Optional<String> opt3 = Optional.ofNullable("Bharath");
Optional<String> opt4 = Optional.ofNullable(null); // empty Optional

// 3. Optional.empty() -> explicitly empty
Optional<String> opt5 = Optional.empty();
```

### Checking presence

```java
System.out.println(opt1.isPresent()); // true
System.out.println(opt5.isPresent()); // false
System.out.println(opt1.isEmpty());   // false
System.out.println(opt5.isEmpty());   // true
```

> Prefer `Optional.ofNullable()` when the value may be `null`.

---

## Common Optional Methods

### `isPresent()` and `isEmpty()`

```java
Optional<String> present = Optional.of("Bharath");
Optional<String> empty = Optional.empty();

System.out.println(present.isPresent()); // true
System.out.println(empty.isPresent());   // false
System.out.println(present.isEmpty());   // false
System.out.println(empty.isEmpty());     // true
```

### `get()` — avoid this

```java
Optional<String> present = Optional.of("Bharath");
System.out.println(present.get()); // Bharath

Optional<String> empty = Optional.empty();
// empty.get(); // throws NoSuchElementException
```

`get()` should be avoided because it throws an exception if the Optional is empty.

---

### `orElse()` — return default if empty

```java
Optional<String> present = Optional.of("Bharath");
Optional<String> empty = Optional.empty();

System.out.println(present.orElse("Unknown")); // Bharath
System.out.println(empty.orElse("Unknown"));   // Unknown
```

Use it for simple or constant defaults.

---

### `orElseGet()` — lazy default

```java
Optional<String> present = Optional.of("Bharath");
Optional<String> empty = Optional.empty();

System.out.println(present.orElseGet(() -> "Unknown"));
System.out.println(empty.orElseGet(() -> "Unknown"));
```

This is better when the default value is expensive to compute.

---

### `orElseThrow()` — throw custom exception

```java
Optional<String> empty = Optional.empty();

// empty.orElseThrow(); // throws NoSuchElementException

String name = Optional.of("Bharath")
    .orElseThrow(() -> new RuntimeException("Name not found"));
```

---

### `ifPresent()` — run action only when present

```java
Optional<String> present = Optional.of("Bharath");
present.ifPresent(name -> System.out.println(name.toUpperCase()));

Optional<String> empty = Optional.empty();
empty.ifPresent(name -> System.out.println(name));
```

### `ifPresentOrElse()` — Java 9+

```java
Optional<String> name = Optional.empty();

name.ifPresentOrElse(
    value -> System.out.println("Found: " + value),
    () -> System.out.println("Not found")
);
```

---

## `map()` — transform the value inside Optional

```java
Optional<String> name = Optional.of("  bharath  ");

Optional<String> upper = name.map(String::trim)
    .map(String::toUpperCase);

System.out.println(upper); // Optional[BHARATH]
```

It is also useful for null-safe chaining:

```java
Optional<String> city = Optional.ofNullable(employee)
    .map(e -> e.getAddress())
    .map(a -> a.getCity())
    .map(String::toUpperCase);
```

If the Optional is empty, the chain simply stops safely.

---

## `filter()` — keep the value only if a condition matches

```java
Optional<String> name = Optional.of("Bharath");

Optional<String> longName = name.filter(n -> n.length() > 5);
System.out.println(longName); // Optional[Bharath]

Optional<String> zName = name.filter(n -> n.startsWith("Z"));
System.out.println(zName); // Optional.empty
```

---

## `flatMap()` — use when transformation returns Optional

```java
class Employee {
    Optional<Address> getAddress() {
        return Optional.of(new Address("Bengaluru"));
    }
}

class Address {
    private final String city;

    Address(String city) {
        this.city = city;
    }

    Optional<String> getCity() {
        return Optional.of(city);
    }
}
```

```java
Optional<Employee> emp = Optional.of(new Employee());

Optional<String> city = emp.flatMap(Employee::getAddress)
    .flatMap(Address::getCity)
    .map(String::toUpperCase);

System.out.println(city); // Optional[BENGALURU]
```

Use `flatMap()` when the transformation itself returns an `Optional`.

---

## `orElse()` vs `orElseGet()`

```java
Optional.of("Bharath").orElse(heavyComputation());
Optional.of("Bharath").orElseGet(() -> heavyComputation());
```

### Key difference

- `orElse()` always evaluates the default argument.
- `orElseGet()` evaluates the supplier only when the Optional is empty.

Use `orElseGet()` for expensive work such as database or API calls.

---

## Optional with Stream API

```java
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

List<String> names = List.of("Bharath", "Ravi", "Kumar");

Optional<String> first = names.stream()
    .filter(n -> n.startsWith("B"))
    .findFirst();

System.out.println(first); // Optional[Bharath]
```

### Optional stream conversion (Java 9+)

```java
List<Optional<String>> optionals = List.of(
    Optional.of("Bharath"),
    Optional.empty(),
    Optional.of("Ravi")
);

List<String> present = optionals.stream()
    .flatMap(Optional::stream)
    .collect(Collectors.toList());

System.out.println(present); // [Bharath, Ravi]
```

### `or()` — alternate Optional if empty

```java
Optional<String> result = Optional.<String>empty()
    .or(() -> Optional.of("Default"));

System.out.println(result); // Optional[Default]
```

---

## What Not to Do with Optional

```java
// ❌ Do not use Optional as a field type
class Employee {
    Optional<String> name;
}

// ❌ Do not use Optional as a method parameter
void process(Optional<String> name) { }

// ❌ Do not use get() without checking
Optional<String> opt = Optional.empty();
// opt.get();

// ❌ Do not return Optional<List> when an empty list is better
// Optional<List<Employee>> findAll() -> bad
// List<Employee> findAll() -> good
```

### Good use cases

- Use Optional as a method return type
- Use it when a value may be absent
- Use it to make null-handling explicit

---

## Interview Questions and Answers

### Q1: What is the purpose of Optional?
To represent a value that may be absent in a clear and safe way, reducing `NullPointerException` issues.

### Q2: What is the difference between `orElse()` and `orElseGet()`?
`orElse()` always evaluates the default value, while `orElseGet()` evaluates the supplier only if the Optional is empty.

### Q3: What does `get()` throw on empty Optional?
`NoSuchElementException`.

### Q4: What is the difference between `map()` and `flatMap()`?
Use `map()` when the transformation returns a normal value. Use `flatMap()` when the transformation returns another `Optional`.

### Q5: When should you not use Optional?
Do not use it as a field type, as a method parameter, or for collection return types.

---

## Summary

- Optional represents a value that may be present or empty.
- Use `Optional.ofNullable()` for safe creation.
- Avoid `get()` unless you are sure the value exists.
- Prefer `orElse()` for simple defaults and `orElseGet()` for expensive ones.
- Use `map()` for simple transformation and `flatMap()` for nested Optional values.
- Optional is best used as a method return type, not for fields or parameters.
