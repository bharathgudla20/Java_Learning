# Phase 4, Topic 2: Functional Interfaces (@FunctionalInterface, Predicate, Function, Consumer, Supplier, and BiFunction)

Product-company interviewers love this topic because it represents the built-in plumbing of modern Java. Instead of forcing you to create new interfaces from scratch, Java provides a pre-baked suite of functional interfaces in the `java.util.function` package to handle almost any data-processing contract.

---

## Part 1: What is a Functional Interface? (In Simple Terms)

As we discovered in Topic 1, a Functional Interface is simply an interface with **exactly one abstract (unimplemented) method**. It can have as many `default` or `static` methods as it wants, but there can only be **one** main abstract method contract.

To make things formal, Java introduces the optional annotation `@FunctionalInterface`. If you put this on top of an interface and accidentally try to add a second abstract method, the compiler will instantly catch it and scream at you.

Instead of writing custom interfaces for every single task, Java 8 provides 4 Core Built-In Functional Interfaces that you must memorize for interviews. Think of them as a factory line with distinct inputs and outputs.

---

## Part 2: The Core 4 Built-In Functional Interfaces

| Interface Name | Abstract Method Signature | Simple Definition (What it does) | Input Type | Output Type |
| :--- | :--- | :--- | :--- | :--- |
| **`Predicate<T>`** | `boolean test(T t)` | **A Filter/Condition Checker** (Takes an item, returns true or false) | `T` (Any Object) | `boolean` |
| **`Function<T, R>`** | `R apply(T t)` | **A Transformer** (Takes an input, changes it, returns a new type) | `T` (Input Type) | `R` (Result Type) |
| **`Consumer<T>`** | `void accept(T t)` | **A Worker/End-Point** (Consumes an item, prints or saves it, returns nothing) | `T` (Any Object) | `void` (Nothing) |
| **`Supplier<T>`** | `T get()` | **A Producer** (Takes absolutely nothing, but constructs and returns a new item) | None | `T` (Any Object) |

---

## Part 3: Code Implementation (The Core 4 + BiFunction)

Here is how a senior developer leverages these built-in tools cleanly using Lambda expressions:

```java
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.BiFunction;

public class FunctionalInterfaceMastery {
    public static void main(String[] args) {

        // 1. Predicate: Checks if a string has more than 5 characters
        Predicate<String> isLongName = (name) -> name.length() > 5;
        System.out.println("Is 'Bharath' a long name? " + isLongName.test("Bharath")); // true

        // 2. Function: Takes a String, transforms it into an Integer (its length)
        Function<String, Integer> getStringLength = (str) -> str.length();
        System.out.println("Length of 'TCS': " + getStringLength.apply("TCS")); // 3

        // 3. Consumer: Takes a String and prints it (Returns nothing)
        Consumer<String> logger = (msg) -> System.out.println("LOG: " + msg);
        logger.accept("Navigating to Phase 4 Topic 2"); // Prints the log line

        // 4. Supplier: Takes nothing, generates a fresh system value or object
        Supplier<Double> getRandomValue = () -> Math.random();
        System.out.println("Random Number: " + getRandomValue.get());

        // BONUS. BiFunction: Takes TWO inputs and returns ONE output
        // Takes two Integers, adds them together, and returns an Integer
        BiFunction<Integer, Integer, Integer> addNumbers = (a, b) -> a + b;
        System.out.println("Sum of 10 + 20 = " + addNumbers.apply(10, 20)); // 30
    }
}
```

## Part 4: TCS to Product-Company Level Interview Questions

### Q1: Why should we use the `@FunctionalInterface` annotation if it is optional?
**Your Answer:** "While the Java compiler can identify a functional interface implicitly by counting its abstract methods, explicitly applying the `@FunctionalInterface` annotation serves two major production purposes:

1. **Design Communication:** It acts as a strict contract to other developers that this interface is explicitly designed to be targeted by Lambda Expressions.
2. **Compile-Time Safeguard:** If a team member later modifies the codebase and attempts to introduce a second abstract method into this interface, the compiler will catch the contract breach immediately and halt compilation, preventing breaking changes across existing lambdas."

### Q2: What are 'Primitive Functional Interfaces' (like `IntPredicate`, `LongFunction`), and why did Java create them? (High-Priority Performance Question!)
**Your Answer:** "Standard generic functional interfaces like `Predicate<T>` can only accept object reference types. If you pass a primitive `int` into a `Predicate<Integer>`, Java forces **Autoboxing** to convert the `int` to an `Integer` wrapper object, which wastes memory heap space when processing millions of data records. To eliminate this runtime boxing overhead, Java 8 provides specialized primitive functional interfaces like **`IntPredicate`**, **`DoubleConsumer`**, and **`LongFunction`**. These accept raw primitive values directly on the stack without any boxing operations, significantly accelerating processing throughput."

### Q3: Can you chain multiple Functional Interfaces together? If yes, how?
**Your Answer:** "Yes, Java's built-in functional interfaces provide default methods explicitly designed for composition:

- `Predicate` can be chained using **`.and()`**, **`.or()`**, and **`.negate()`** (e.g., `isEven.and(isGreaterThanTen).test(num)`).
- `Function` can be chained using **`.andThen()`** or **`.compose()`** to pipe the output of one mathematical or structural calculation directly into the input parameter of another."

### Q4: What is the difference between `Function` and `BiFunction`? How do you scale past two parameters?
**Your Answer:** "The core difference is input capacity: `Function<T, R>` accepts exactly one input argument and returns a result, while `BiFunction<T, U, R>` accepts exactly two distinct input arguments and returns a single result. If an enterprise design requirements scale past two parameters (e.g., needing a *TriFunction*), Java does not provide a built-in option. We scale past this by creating a custom functional interface definition with three parameters, or by bundling those multiple arguments together inside a single **Data Transfer Object (DTO)** container class."
