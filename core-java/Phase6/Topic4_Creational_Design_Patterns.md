# Phase 6, Topic 4: Creational Design Patterns

## Why this matters

Design patterns are proven, reusable solutions to common object-creation problems. Interviewers ask about them to see if you write maintainable, extensible code — not just code that works. Creational patterns specifically deal with how objects get created, hiding the complexity of instantiation logic from the client code.

## 1. Singleton Pattern

### Definition
Ensures a class has exactly one instance throughout the application, and provides a single global point of access to it.

### Real use case
Logger, database connection pool, configuration manager — things you only ever want one of.

```java
public class Singleton {
    private static Singleton instance;
    private Singleton() { }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### Problem with the above
This is not thread-safe. Two threads can both pass the null check simultaneously and create two instances.

### Fix 1 — synchronized method
```java
public static synchronized Singleton getInstance() {
    if (instance == null) instance = new Singleton();
    return instance;
}
```

### Fix 2 — Double-Checked Locking
```java
public class Singleton {
    private static volatile Singleton instance;
    private Singleton() { }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### Interview trap
`volatile` is required here. Without it, another thread could see a partially constructed object due to instruction reordering.

### Fix 3 — Best practice: Enum Singleton
```java
public enum Singleton {
    INSTANCE;

    public void doSomething() { }
}
```

Usage:
```java
Singleton.INSTANCE.doSomething();
```

This is thread-safe by JVM guarantee, and immune to reflection attacks and serialization issues that can break the other approaches.

## 2. Factory Pattern

### Definition
Defines a method for creating objects, but lets the method decide which class to instantiate — the client doesn’t need to know the exact class name.

### Real use case
Creating different `Shape` objects, different `PaymentMethod` implementations, different database drivers — anywhere you have a family of related classes selected by some condition.

```java
interface Shape { void draw(); }

class Circle implements Shape {
    public void draw() {
        System.out.println("Drawing Circle");
    }
}

class Square implements Shape {
    public void draw() {
        System.out.println("Drawing Square");
    }
}

class ShapeFactory {
    public static Shape getShape(String type) {
        return switch (type) {
            case "CIRCLE" -> new Circle();
            case "SQUARE" -> new Square();
            default -> throw new IllegalArgumentException("Unknown shape");
        };
    }
}
```

### Client code
```java
Shape s = ShapeFactory.getShape("CIRCLE");
s.draw();
```

### Why this is useful
The client is decoupled from concrete classes. If you add a `Triangle` class tomorrow, you only touch the factory — not every place `Shape` objects are created.

## 3. Builder Pattern

### Definition
Separates the construction of a complex object from its representation, allowing the same construction process to build different configurations step by step — especially useful when a class has many optional parameters.

### Real use case
Building objects like `Pizza`, `HttpRequest`, or `User` where some fields are required and many are optional — avoids the telescoping constructor problem.

```java
// The problem this solves — telescoping constructors, hard to read/use:
new User("Bharath", 25, null, null, "Coimbatore", null);
```

### The fix — Builder pattern
```java
class User {
    private final String name;
    private final int age;
    private final String email;
    private final String city;

    private User(Builder b) {
        this.name = b.name;
        this.age = b.age;
        this.email = b.email;
        this.city = b.city;
    }

    static class Builder {
        private String name;
        private int age;
        private String email;
        private String city;

        Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }

        Builder email(String email) {
            this.email = email;
            return this;
        }

        Builder city(String city) {
            this.city = city;
            return this;
        }

        User build() {
            return new User(this);
        }
    }
}
```

### Usage
```java
User u = new User.Builder("Bharath", 25)
        .email("b@mail.com")
        .city("Coimbatore")
        .build();
```

Note: Java’s `StringBuilder` and Lombok’s `@Builder` annotation are real-world applications of this exact pattern.

## 4. Prototype Pattern

### Definition
Creates new objects by copying (cloning) an existing object rather than creating from scratch — useful when object creation is expensive (for example, involving a database call or heavy computation).

```java
class Sheep implements Cloneable {
    String name;

    Sheep(String name) {
        this.name = name;
    }

    @Override
    public Sheep clone() {
        try {
            return (Sheep) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Usage
```java
Sheep original = new Sheep("Dolly");
Sheep clone = original.clone();
```

### Interview trap
Shallow vs deep copy: `super.clone()` does a shallow copy by default. If `Sheep` had a reference field like a `List<String>`, both the original and the clone would point to the same list. To get a true independent copy, you must manually clone nested mutable objects too.
