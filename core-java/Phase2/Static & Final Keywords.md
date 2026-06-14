# Phase 2 Topic 7 — static & final Keywords

## Part 1: The `static` Keyword (Class-level Memory)

#### What is it? (In Simple Terms)

Normally, when you create objects from a class, every single object gets its own copy of the variables.
`static` changes that. It means **"belonging to the Class itself, rather than to any individual object."**

Think of a TCS project team:

- Every employee has their own **Laptop** (Non-static: changes from person to person).
- Every employee shares the same **Conference Room** (Static: there is only one copy, and everyone uses the exact same room).

If anyone changes something in the static space, it changes for everyone. Because it belongs to the class, **you don't even need to create an object to use it.**

#### Code Example:

```java
class Employee {
    String empName;       // Instance variable (Each object gets its own name)
    static String company = "TCS"; // Static variable (Shared by ALL objects)

    // Static Method
    static void changeCompany(String newCompany) {
        company = newCompany; // Can access static variables directly
        // empName = "Bharath"; // COMPILE ERROR! Static methods can't see instance variables.
    }
}
```

## Part 2: The `final` Keyword (Immutability / Restriction)

#### What is it? (In Simple Terms)

`final` means **"locked or unchangeable."** It is a restriction keyword. Depending on where you apply it, it locks different things:

1. **Final Variable:** Turns the variable into a **constant**. Once you give it a value, you can never change it. (e.g., `final double PI = 3.14159;`)
2. **Final Method:** Prevents **Method Overriding**. A child class can inherit it, but cannot rewrite it.
3. **Final Class:** Prevents **Inheritance**. No other class can extend a final class. (e.g., Java’s built-in `String` class is final).

## Part 3: Combining `static final` (Constants)

When you see `public static final String COMPANY_NAME = "TCS";`, you are creating a true global constant.

- `static` means it loads once into memory.
- `final` means no one can ever modify its value.

## Part 4: TCS to Product-Company Level Interview Questions

### Q1: Why can a `static` method NOT access a non-static (instance) variable or method directly?

**Your Answer:**

"Static methods are loaded into memory when the class template is loaded by the JVM, long before any objects are created using the `new` keyword. Since instance variables only come to life when an object is instantiated on the heap, a static method doesn't know *which* object's variable it should look at. Therefore, it results in a compile-time error."

### Q2: What happens if you make a reference variable `final`? Can you still change the data inside the object? (Tricky Trap Question!)

```java
final Employee emp = new Employee();
emp.empName = "Bharath"; // Will this compile?
```

**Your Answer:**

"Yes, it will compile perfectly. Making a reference variable `final` means you cannot change the **address/reference** it points to (you cannot do `emp = new Employee();` again). However, the internal state or variables *inside* that object can still be modified freely."

### Q3: Can you declare a `static` block inside a class? What is its purpose?

**Your Answer:**

"Yes. A static block is a block of code enclosed in `static { ... }` that executes exactly once when the class is first loaded into memory by the ClassLoader, even before the `main` method or constructors run. It is primarily used to initialize complex static variables or set up system environments."

Example:

```java
class Config {
    static String environment;

    static {
        environment = System.getenv("APP_ENV");
        if (environment == null) {
            environment = "development";
        }
        System.out.println("Config loaded for: " + environment);
    }
}
```

### Q4: If a class contains only `private` constructors, can we inherit from it? How does it compare to a `final` class?

**Your Answer:**

"No, we cannot inherit from it. When a child class extends a parent, the child's constructor must call `super()`. If the parent's constructor is private, the child cannot access it, making inheritance impossible. While both prevent inheritance, a `final` class explicitly communicates this constraint to the compiler and developers, whereas a class with private constructors is typically used for utility classes or the Singleton Pattern."

### Q5: Is `final` the same as `finally` and `finalize`?

**Your Answer:**

"No, they are completely unrelated despite sounding similar:

- **`final`** is a keyword used as a modifier to restrict variables, methods, or classes.
- **`finally`** is a block used in exception handling (`try-catch-finally`) that always executes regardless of whether an exception is thrown or caught.
- **`finalize()`** is a deprecated method in the `Object` class that the Garbage Collector used to invoke right before destroying an object to release resources."
