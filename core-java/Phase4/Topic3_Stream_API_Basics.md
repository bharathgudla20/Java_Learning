# Phase 4, Topic 3: Stream API Basics

By this point in modern Java, you are expected to think in terms of data pipelines rather than manual loops. The Stream API is one of the most important Java 8+ features because it lets you process collections in a clean, functional, and readable way.

---

## Part 1: What is the Stream API?

The Stream API allows you to process collections using a pipeline style:

- Source → Intermediate Operations → Terminal Operation

Think of it like a factory assembly line:

- Raw materials enter the line (your collection)
- Each station performs an operation (filter, map, sort)
- The final product comes out (result)

Before streams, developers wrote long loops with `if` conditions, temporary variables, and manual collection logic. Streams let you express the what instead of the how.

### Old way vs Stream way

```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

List<String> names = List.of("Bharath", "Ravi", "Kumar", "Arun", "Bala");

// OLD WAY
List<String> result = new ArrayList<>();
for (String name : names) {
    if (name.startsWith("B")) {
        result.add(name.toUpperCase());
    }
}

// STREAM WAY
List<String> resultStream = names.stream()
    .filter(name -> name.startsWith("B"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());

System.out.println(resultStream); // [BHARATH, BALA]
```

### Important point

Streams do not modify the original collection. They create a new result.

```java
List<String> names = List.of("Bharath", "Ravi", "Kumar");
List<String> updated = names.stream()
    .map(String::toUpperCase)
    .toList();

System.out.println(names);   // [Bharath, Ravi, Kumar]
System.out.println(updated); // [BHARATH, RAVI, KUMAR]
```

---

## Part 2: The 3-Part Pipeline

A stream pipeline has three main parts:

1. Source
2. Intermediate operations
3. Terminal operation

### Example pipeline

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

List<Integer> result = nums.stream()
    .filter(n -> n % 2 == 0)   // intermediate
    .map(n -> n * 2)           // intermediate
    .toList();                // terminal

System.out.println(result); // [4, 8, 12, 16, 20]
```

### Lazy evaluation

Intermediate operations like `filter()` and `map()` are lazy. They do not run until a terminal operation is invoked.

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

nums.stream()
    .filter(n -> {
        System.out.println("filtering: " + n);
        return n % 2 == 0;
    })
    .map(n -> n * 2);

// No output yet: no terminal operation is called.
```

Once a terminal operation is added, the pipeline executes:

```java
List<Integer> result = nums.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .toList();

System.out.println(result); // [4, 8, 12, 16, 20]
```

This is efficient because Java can stop early when possible.

---

## Part 3: Intermediate Operations

Intermediate operations return a new stream and are chainable.

### Common ones

- `filter()` → keeps only matching elements
- `map()` → transforms each element
- `sorted()` → sorts elements
- `distinct()` → removes duplicates
- `limit()` → keeps first N elements
- `skip()` → skips first N elements
- `flatMap()` → flattens nested structures
- `peek()` → debug/inspect each element

### Examples

```java
List<String> names = List.of("Bharath", "Ravi", "Kumar", "Arun", "Bala");

List<String> filtered = names.stream()
    .filter(name -> name.startsWith("B"))
    .toList();

List<String> transformed = names.stream()
    .map(String::toUpperCase)
    .toList();

List<String> sorted = names.stream()
    .sorted()
    .toList();
```

---

## Part 4: Terminal Operations

Terminal operations produce a final result and end the stream.

### Common terminal operations

- `collect()` → gather output into a list/set/map
- `forEach()` → iterate and do something
- `count()` → count elements
- `reduce()` → combine elements into one result
- `min()` / `max()` → find smallest/largest
- `findFirst()` / `findAny()` → retrieve one matching element
- `anyMatch()` / `allMatch()` / `noneMatch()` → check conditions

### Examples

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

long count = nums.stream().filter(n -> n % 2 == 0).count();
System.out.println(count); // 2

int sum = nums.stream().reduce(0, Integer::sum);
System.out.println(sum); // 15

boolean anyEven = nums.stream().anyMatch(n -> n % 2 == 0);
System.out.println(anyEven); // true
```

---

## Part 5: Creating Streams

Streams can be created from many sources.

```java
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

List<String> names = List.of("Bharath", "Ravi", "Kumar");
Stream<String> s1 = names.stream();

String[] arr = {"Bharath", "Ravi"};
Stream<String> s2 = Arrays.stream(arr);

Stream<String> s3 = Stream.of("Bharath", "Ravi", "Kumar");

IntStream s4 = IntStream.range(1, 6);      // 1 2 3 4 5
IntStream s5 = IntStream.rangeClosed(1, 5); // 1 2 3 4 5

Stream<Integer> s6 = Stream.iterate(0, n -> n + 2).limit(5); // 0 2 4 6 8
Stream<Double> s7 = Stream.generate(Math::random).limit(3);

Stream<String> s8 = Arrays.stream("Bharath,Ravi,Kumar".split(","));
```

---

## Part 6: Interview-Level Summary

### Key points to remember

- Stream API helps process collections functionally.
- Streams are lazy until a terminal operation is called.
- Intermediate operations are chainable and return new streams.
- Terminal operations produce the final result.
- Streams do not modify the original data source.

### Short interview answer

A Stream API is a functional way to process collections using a pipeline of operations such as filter, map, and collect. It is lazy, chainable, and does not modify the original collection.

---

## Part 7: TCS to Product-Company Level Interview Questions

### Q1: What is the core difference between Intermediate and Terminal operations in the Stream API?
**Your Answer:** "The core difference lies in their return types and execution timing. An **Intermediate operation** transforms a stream into another stream and is completely **lazy**, meaning it performs no actual processing when called. A **Terminal operation** returns a non-stream result (like a List, Primitive, or void) and triggers the actual traversal and processing of the underlying data. Without a terminal operation at the end of the pipeline, a stream pipeline does absolutely nothing."

### Q2: What does it mean that Streams are 'Short-Circuiting'? Give examples.
**Your Answer:** "Short-circuiting means that the stream pipeline does not need to process the entire collection to produce a final result. It can terminate early as soon as its condition is satisfied.

- **`anyMatch(Predicate)`** is a short-circuiting terminal operation because the moment it encounters an element that evaluates to `true`, it immediately stops processing the remaining elements and returns `true`.
- **`findFirst()`** and **`limit(n)`** are also short-circuiting operations because they cut off further evaluation once their specific quotas are filled, saving massive amounts of processing time on large datasets."

### Q3: Why is it bad practice to modify a local variable outside a Stream pipeline from within a lambda block?
**Your Answer:** "Modifying a local variable outside a stream pipeline from inside a lambda expression violates the functional programming paradigm and will trigger a compile-time error. Java forces local variables captured by lambdas to be **final or effectively final** to preserve thread safety. If multiple elements in a stream are being processed concurrently (such as in a `parallelStream()`), attempting to mutate a single shared state external variable would cause race conditions, data corruption, and unpredictable side effects."

### Q4: Can you reuse a Stream once a terminal operation has been executed?
**Your Answer:** "No. A Stream can only be operated upon **exactly once**. The moment a terminal operation (like `collect()`, `reduce()`, or `forEach()`) is invoked, the underlying stream pipeline is closed and marked as consumed. If you attempt to reuse that same stream reference to perform a secondary operation, Java will instantly throw an **`IllegalStateException`** stating that the stream has already been operated upon or closed."
