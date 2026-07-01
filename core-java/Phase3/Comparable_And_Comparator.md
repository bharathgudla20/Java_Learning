# Java Phase 3 — Topic 6: Comparable & Comparator

## Why We Need Them

Java already knows how to sort values like:
- `Integer`
- `String`
- `Double`

But for your own classes such as `Employee`, Java has no built-in idea of how to order them.

Should an employee be sorted by:
- name?
- salary?
- age?

That is exactly why `Comparable` and `Comparator` exist.

They tell Java how to compare objects and decide their sorting order.

---

## 1. Comparable — Natural Ordering

`Comparable` is used when a class has one obvious default way to sort itself.

It is implemented inside the class itself.

### Syntax

```java
interface Comparable<T> {
    int compareTo(T other);
}
```

### Example

```java
class Employee implements Comparable<Employee> {
    String name;
    int salary;
    int age;

    Employee(String name, int salary, int age) {
        this.name = name;
        this.salary = salary;
        this.age = age;
    }

    @Override
    public int compareTo(Employee other) {
        return this.salary - other.salary;   // ascending order by salary
    }
}
```

### Usage

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Employee> emps = new ArrayList<>();
        emps.add(new Employee("Ravi", 50000, 30));
        emps.add(new Employee("Bharath", 75000, 25));
        emps.add(new Employee("Kumar", 40000, 35));

        Collections.sort(emps);   // uses compareTo()

        for (Employee e : emps) {
            System.out.println(e.name + " -> " + e.salary);
        }
    }
}
```

### Output

```java
Kumar -> 40000
Ravi -> 50000
Bharath -> 75000
```

### Rule for `compareTo()`

Return:
- negative → current object comes before the other object
- zero → both are equal
- positive → current object comes after the other object

### Easy shortcut for numbers

```java
return this.salary - other.salary;   // ascending
return other.salary - this.salary;   // descending
```

> For very large values, prefer `Integer.compare(a, b)` instead of subtraction to avoid overflow.

---

## 2. Comparator — External Sorting Logic

`Comparator` is used when you want multiple ways to sort the same class.

It is written outside the class.

### Syntax

```java
interface Comparator<T> {
    int compare(T o1, T o2);
}
```

### Example

```java
class Employee {
    String name;
    int salary;
    int age;

    Employee(String name, int salary, int age) {
        this.name = name;
        this.salary = salary;
        this.age = age;
    }
}
```

### Create different comparators

```java
Comparator<Employee> byName = (e1, e2) -> e1.name.compareTo(e2.name);
Comparator<Employee> bySalaryDesc = (e1, e2) -> e2.salary - e1.salary;
Comparator<Employee> byAge = (e1, e2) -> e1.age - e2.age;
```

### Usage

```java
List<Employee> emps = new ArrayList<>();
emps.add(new Employee("Ravi", 50000, 30));
emps.add(new Employee("Bharath", 75000, 25));
emps.add(new Employee("Kumar", 40000, 35));

Collections.sort(emps, byName);         // sort by name
Collections.sort(emps, bySalaryDesc);   // sort by salary descending
Collections.sort(emps, byAge);          // sort by age
```

---

## 3. Modern Java 8+ Style with Lambdas

Java 8 made comparator code much cleaner.

### Simple examples

```java
List<Employee> emps = new ArrayList<>();

emps.sort((e1, e2) -> e1.name.compareTo(e2.name));          // by name
emps.sort((e1, e2) -> e1.salary - e2.salary);                // by salary asc
emps.sort((e1, e2) -> e2.salary - e1.salary);                // by salary desc
```

### Cleaner with `Comparator.comparing()`

```java
emps.sort(Comparator.comparing(e -> e.name));
emps.sort(Comparator.comparingInt(e -> e.salary));
```

### Reverse order

```java
emps.sort(Comparator.comparingInt((Employee e) -> e.salary).reversed());
```

### Chained sorting

```java
emps.sort(Comparator.comparingInt((Employee e) -> e.salary)
                    .thenComparing(e -> e.name));
```

This means:
1. sort by salary
2. if salary is equal, sort by name

---

## 4. Comparable vs Comparator

| Feature | Comparable | Comparator |
|---|---|---|
| Location | Inside the class | Outside the class |
| Method | `compareTo(T other)` | `compare(T o1, T o2)` |
| Purpose | One natural/default ordering | Multiple custom sort orders |
| Use when | You own the class and want one default sort | You need flexibility or cannot change the class |

### Comparable example

```java
class Employee implements Comparable<Employee> {
    // one default ordering
}
```

### Comparator example

```java
Comparator<Employee> byName = (e1, e2) -> e1.name.compareTo(e2.name);
```

---

## 5. Return Value Rule

Both `compareTo()` and `compare()` follow the same rule:

- negative → first object comes before second
- zero → both objects are equal
- positive → first object comes after second

### For numbers

```java
return a - b;        // ascending
return b - a;        // descending
```

### For Strings

```java
return s1.compareTo(s2);   // ascending
return s2.compareTo(s1);   // descending
```

### Safe comparison methods

```java
Integer.compare(a, b);
Double.compare(a, b);
String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
```

These are safer than subtraction.

---

## 6. Important Notes

- `Comparable` is for a single natural ordering.
- `Comparator` is for multiple possible sort orders.
- Use `Comparator.comparing()` and `Comparator.comparingInt()` in modern Java.
- Avoid subtraction for very large numbers because of overflow.

---

## Quick Practice

Try sorting employees by:
- name (A → Z)
- name (Z → A)
- salary (low → high)
- salary (high → low)
- age (young → old)
- salary, then name

That is the core idea behind `Comparable` and `Comparator`.
