# Phase 4 Topic 4 — Stream API Advanced

## 1) FlatMap

`map()` wraps each element.

`flatMap()` flattens the result into one stream.

Think of it this way:
- `map()` gives you a list of gift bags.
- `flatMap()` opens every bag and puts all gifts into one pile.

### Example: `map()` vs `flatMap()`

```java
List<List<String>> nested = List.of(
    List.of("Bharath", "Ravi"),
    List.of("Kumar", "Arun"),
    List.of("Bala")
);

// map() -> Stream of Streams (nested)
List<Stream<String>> mapped = nested.stream()
    .map(list -> list.stream())
    .collect(Collectors.toList());

// flatMap() -> one flat stream
List<String> flat = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());

System.out.println(flat); // [Bharath, Ravi, Kumar, Arun, Bala]
```

### Real-world example

```java
class Student {
    String name;
    List<String> subjects;

    Student(String name, List<String> subjects) {
        this.name = name;
        this.subjects = subjects;
    }
}

List<Student> students = List.of(
    new Student("Bharath", List.of("Java", "Python", "SQL")),
    new Student("Ravi", List.of("Java", "AWS")),
    new Student("Kumar", List.of("React", "Node"))
);

List<String> allSubjects = students.stream()
    .flatMap(s -> s.subjects.stream())
    .distinct()
    .sorted()
    .collect(Collectors.toList());

System.out.println(allSubjects);
// [AWS, Java, Node, Python, React, SQL]
```

### Rule to remember

Use `flatMap()` when your mapping operation returns a `Stream` or a `Collection` and you want one flattened result.

---

## 2) Grouping By — `groupingBy()`

`Collectors.groupingBy()` groups elements like SQL `GROUP BY`.

It returns a `Map<Key, List<Value>>`.

### Example

```java
class Employee {
    String name;
    String dept;
    int salary;

    Employee(String name, String dept, int salary) {
        this.name = name;
        this.dept = dept;
        this.salary = salary;
    }
}

List<Employee> emps = List.of(
    new Employee("Bharath", "IT", 75000),
    new Employee("Ravi", "IT", 60000),
    new Employee("Kumar", "HR", 45000),
    new Employee("Arun", "Finance", 80000),
    new Employee("Bala", "IT", 55000),
    new Employee("Priya", "HR", 50000)
);

Map<String, List<Employee>> byDept = emps.stream()
    .collect(Collectors.groupingBy(e -> e.dept));

System.out.println(byDept);
```

### Group and count

```java
Map<String, Long> countByDept = emps.stream()
    .collect(Collectors.groupingBy(
        e -> e.dept,
        Collectors.counting()
    ));

System.out.println(countByDept);
```

### Group and calculate average salary

```java
Map<String, Double> avgSalaryByDept = emps.stream()
    .collect(Collectors.groupingBy(
        e -> e.dept,
        Collectors.averagingInt(e -> e.salary)
    ));

System.out.println(avgSalaryByDept);
```

### Key point

- `groupingBy(classifier)` → groups into lists
- `groupingBy(classifier, downstream)` → processes each group further

---

## 3) Partitioning By — `partitioningBy()`

`Collectors.partitioningBy()` splits the stream into exactly two groups: `true` and `false`.

It is useful for binary decisions such as:
- pass/fail
- active/inactive
- high/low salary

### Example

```java
Map<Boolean, List<Employee>> partitioned = emps.stream()
    .collect(Collectors.partitioningBy(e -> e.salary > 60000));

System.out.println(partitioned);
```

### Partition and count

```java
Map<Boolean, Long> passFailCount = emps.stream()
    .collect(Collectors.partitioningBy(
        e -> e.salary > 60000,
        Collectors.counting()
    ));

System.out.println(passFailCount);
```

### Rule to remember

Use `groupingBy()` when you can have many groups.
Use `partitioningBy()` when you want exactly two groups.

---

## 4) `toMap()`

`Collectors.toMap()` converts a stream into a `Map`.

### Example

```java
Map<String, Integer> nameSalaryMap = emps.stream()
    .collect(Collectors.toMap(
        e -> e.name,
        e -> e.salary
    ));

System.out.println(nameSalaryMap);
```

### Important note

If duplicate keys appear, `toMap()` throws an exception.

Use a merge function to handle duplicates:

```java
Map<String, Integer> salaryByDept = emps.stream()
    .collect(Collectors.toMap(
        e -> e.dept,
        e -> e.salary,
        (existing, newer) -> existing + newer
    ));

System.out.println(salaryByDept);
```

---

## 5) `Collectors.joining()`

`Collectors.joining()` joins stream elements into one string.

### Example

```java
List<String> names = List.of("Bharath", "Ravi", "Kumar", "Arun");

String joined = names.stream()
    .collect(Collectors.joining(", "));

System.out.println(joined);
```

### With prefix and suffix

```java
String result = names.stream()
    .collect(Collectors.joining(", ", "[", "]"));

System.out.println(result);
```

---

## 6) Parallel Streams

Parallel streams use multiple CPU cores to process data faster.

### Example

```java
List<Integer> bigList = IntStream.rangeClosed(1, 10_000_000)
    .boxed()
    .collect(Collectors.toList());

long sum1 = bigList.stream()
    .mapToLong(Integer::longValue)
    .sum();

long sum2 = bigList.parallelStream()
    .mapToLong(Integer::longValue)
    .sum();
```

### When to use parallel streams

Use them when:
- the dataset is large
- operations are CPU-heavy
- tasks are independent

Avoid them when:
- the collection is small
- the code has side effects
- order matters a lot
- you are doing I/O-bound work

### Important caution

Parallel streams can cause race conditions if shared mutable state is used.

```java
List<Integer> result = new ArrayList<>();
IntStream.range(1, 100)
    .parallel()
    .forEach(result::add); // unsafe
```

Use `collect()` instead for safe parallel processing.

---

## Summary

- `flatMap()` flattens nested streams into one stream.
- `groupingBy()` groups elements by a key.
- `partitioningBy()` splits elements into two groups.
- `toMap()` converts a stream into a map.
- `joining()` builds a single string from many values.
- Parallel streams can improve performance for large, independent workloads.
