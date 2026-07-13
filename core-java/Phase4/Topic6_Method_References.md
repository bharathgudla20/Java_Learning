# Phase 4, Topic 6: Method References (`ClassName::methodName` Syntax)

Product-company interviewers look for this topic to check whether you write clean, highly idiomatic modern Java code. Method references do not add new execution logic; they are an ultra-clean form of syntactic sugar that makes existing lambda expressions shorter and easier to read.

---

## Part 1: What is a Method Reference? (In Simple Terms)

A method reference is a shorthand way to write a lambda expression that only calls an existing method.

Think of it like a speed-dial shortcut on your phone:

- Lambda Expression: you explicitly say, “take this parameter `x` and pass it into `System.out.println(x)`.”
- Method Reference: you simply say, “use the `println` method already defined on `System.out`.”

Instead of writing:

```java
(x) -> System.out.println(x)
```

you can write:

```java
System.out::println
```

---

## Part 2: The Four Structural Archetypes

To handle interview questions confidently, you must know the four common patterns of method references.

| Archetype Type | Lambda Equivalent | Method Reference Syntax | Example |
| :--- | :--- | :--- | :--- |
| 1. Static Method | `(x) -> Class.staticMethod(x)` | `Class::staticMethod` | `Integer::parseInt` |
| 2. Instance Method (Specific Object) | `(x) -> obj.instanceMethod(x)` | `obj::instanceMethod` | `System.out::println` |
| 3. Instance Method (Arbitrary Object) | `(obj, x) -> obj.instanceMethod(x)` | `ClassName::instanceMethod` | `String::toLowerCase` |
| 4. Constructor Reference | `() -> new ClassName()` | `ClassName::new` | `ArrayList::new` |

---

## Part 3: Code Implementation

Here is a practical example showing how to replace verbose lambda blocks with clean method references.

```java
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferenceMastery {
    public static void main(String[] args) {
        List<String> frameworkList = Arrays.asList("spring", "hibernate", "quarkus");

        // --- 1. Static Method Reference ---
        // Lambda: (str) -> Integer.parseInt(str)
        Function<String, Integer> parserLambda = str -> Integer.parseInt(str);

        // Method Reference:
        Function<String, Integer> parserRef = Integer::parseInt;
        System.out.println("Parsed Int: " + parserRef.apply("2026"));

        // --- 2. Instance Method of an Arbitrary Object ---
        // Lambda: (str) -> str.toUpperCase()
        // Method Reference: ClassName::methodName
        List<String> upperFrameworks = frameworkList.stream()
                .map(String::toUpperCase)
                .toList();
        System.out.println("Upper Case: " + upperFrameworks);

        // --- 3. Instance Method of a Specific Object ---
        // Lambda: (str) -> System.out.println(str)
        frameworkList.forEach(System.out::println);

        // --- 4. Constructor Reference ---
        // Lambda: () -> new ArrayList<String>()
        Supplier<List<String>> listSupplierLambda = () -> new ArrayList<>();

        // Method Reference:
        Supplier<List<String>> listSupplierRef = ArrayList::new;
        List<String> freshList = listSupplierRef.get();

        System.out.println("Fresh list created: " + freshList);
    }
}
```

---

## Part 4: Interview-Style Questions and Answers

### Q1: Do method references provide any performance advantage over lambda expressions?

**Answer:** No. They do not offer a runtime performance advantage. Method references are compiled to the same kind of functional call sites as lambdas, and the JVM handles them in the same optimized way. Their value is mainly readability and cleaner syntax.

### Q2: How does the compiler distinguish between an instance method reference on an arbitrary object and one on a specific object?

**Answer:** The compiler looks at the functional interface signature and the target type:

- For `object::method`, the target object is already fixed.
- For `ClassName::method`, the first parameter flowing through the pipeline is treated as the object on which the method is invoked.

### Q3: How do constructor references work when the constructor needs arguments?

**Answer:** Constructor references adapt based on the target functional interface. For example, `ArrayList::new` can match a `Supplier` for a no-argument constructor, or a `Function<Integer, List>` if a constructor accepting an initial capacity exists.

### Q4: Can we pass custom arguments directly inside a method reference?

**Answer:** No. The syntax `ClassName::methodName` only refers to a method by name. If you need to pass custom values or modify parameters, you must use a normal lambda expression.

---

## Final Takeaway

Method references are best used when a lambda expression is simply “call this existing method.” They make code more concise, more readable, and more idiomatic in modern Java.

Use them when:

- the lambda body is just a method call,
- the logic is already implemented elsewhere,
- and you want to write cleaner functional-style code.
