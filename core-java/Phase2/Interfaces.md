# Phase 2 Topic 6 — Interfaces

## 1 — What is an Interface?

An interface is a **contract** — "if you implement me, you MUST provide these methods"

Think of an interface like a job description. The job description says "this person must be able to drive, speak English, and use Excel." It doesn't say HOW they do those things — just that they can do them. Any person (class) that takes that job (implements that interface) must be able to do all those things.

**Real example:** A USB port is an interface. Any device — pen drive, mouse, keyboard — that follows the USB standard can plug in. The port doesn't care what the device does internally — just that it follows the contract.

```java
// Interface — defines the CONTRACT (what must be done)
interface Flyable {
    void fly();       // abstract method — no body, no implementation
    void land();      // every implementing class MUST provide these
}

// Class IMPLEMENTS the interface — provides the HOW
class Bird implements Flyable {
    @Override
    public void fly()  { System.out.println("Bird flaps wings to fly"); }
    @Override
    public void land() { System.out.println("Bird lands on branch"); }
}

class Airplane implements Flyable {
    @Override
    public void fly()  { System.out.println("Airplane uses engines to fly"); }
    @Override
    public void land() { System.out.println("Airplane uses runway to land"); }
}

// Both implement same contract, but HOW is completely different!
Flyable f1 = new Bird();
Flyable f2 = new Airplane();
f1.fly();   // Bird flaps wings to fly
f2.fly();   // Airplane uses engines to fly
```

**Key Keywords:**
- `interface` — to define
- `implements` — to use

**Important Rule:** A class that implements an interface MUST override ALL abstract methods — or it must be declared abstract itself.

---

## 2 — Interface Rules — What's Allowed Inside

### ✓ Allowed

#### Abstract Methods (implicitly `public abstract`)
The main purpose of interfaces. No body, just signature. All implementing classes must override these.
```java
void fly();   // same as: public abstract void fly();
```

#### Constants (implicitly `public static final`)
All interface fields are constants, always!
```java
int MAX_SPEED = 300;   // same as: public static final int MAX_SPEED = 300;
```

#### Default Methods — Java 8+ (has a body!)
Methods with a body — so existing implementing classes don't break when you add new methods to an interface. Marked with `default` keyword.

```java
default void fuelCheck() {
    System.out.println("Checking fuel level...");
}
```

#### Static Methods — Java 8+
Utility methods that belong to the interface itself, not to implementing classes. Called as `InterfaceName.method()`.

```java
static void showInfo() {
    System.out.println("Vehicle interface v2.0");
}
```

### ✗ Not Allowed

#### Instance Fields (Variables)
Interfaces cannot have instance variables — only constants (`public static final`). No state allowed!

#### Constructors
Interfaces cannot be instantiated — no `new Flyable()`. So no constructors are needed or allowed.

---

## 3 — Multiple Interfaces — Java's Solution to Multiple Inheritance

A class can implement **many interfaces** — unlimited!

Java doesn't allow extending multiple classes (diamond problem), but you CAN implement multiple interfaces. This gives you the flexibility of multiple inheritance without the ambiguity.

```java
interface Flyable  { void fly();   }
interface Swimmable { void swim();  }
interface Runnable  { void run();   }

// Duck can fly, swim AND run — implements all three!
class Duck implements Flyable, Swimmable, Runnable {
    @Override public void fly()  { System.out.println("Duck flies"); }
    @Override public void swim() { System.out.println("Duck swims"); }
    @Override public void run()  { System.out.println("Duck runs"); }
}

// You can also extend a class AND implement interfaces together:
class FlyingFish extends Fish implements Flyable, Swimmable {
    @Override public void fly()  { System.out.println("Flying fish leaps"); }
    @Override public void swim() { System.out.println("Flying fish swims"); }
}
```

**Syntax order matters:**
- `extends` always comes before `implements`
- Example: `class Dog extends Animal implements Flyable, Swimmable`

---

## 4 — Default Methods (Java 8+)

Interface methods WITH a body — for backward compatibility

Before Java 8, if you added a new method to an interface, ALL existing implementing classes broke — they all had to implement the new method. Java 8 introduced default methods to solve this. A default method has a body, so existing classes don't need to override it unless they want to.

```java
interface Vehicle {
    void start();   // abstract — must be implemented

    // default method — has a body, optional to override
    default void fuelCheck() {
        System.out.println("Checking fuel level...");
    }

    // static method — belongs to interface itself
    static void showInfo() {
        System.out.println("Vehicle interface v2.0");
    }
}

class Car implements Vehicle {
    @Override
    public void start() { System.out.println("Car engine starts"); }
    // fuelCheck() not overridden — uses interface's default version
}

class ElectricCar implements Vehicle {
    @Override
    public void start() { System.out.println("Electric motor starts"); }

    @Override
    public void fuelCheck() {   // overrides default with custom version
        System.out.println("Checking battery level...");
    }
}

Car c = new Car();
c.start();      // Car engine starts
c.fuelCheck();  // Checking fuel level... (default version)

ElectricCar e = new ElectricCar();
e.fuelCheck();  // Checking battery level... (overridden version)

Vehicle.showInfo();  // static — called on interface name directly
```

---

## 5 — Functional Interfaces & Lambdas (Java 8+)

**Interface with exactly ONE abstract method — can use lambda!**

A functional interface has exactly one abstract method. Java 8 introduced the `@FunctionalInterface` annotation to mark these. The big deal: you can implement a functional interface using a lambda expression — no need to write a whole class!

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);   // only ONE abstract method
}

// Old way — anonymous inner class (verbose)
Calculator add = new Calculator() {
    @Override
    public int calculate(int a, int b) { return a + b; }
};

// New way — lambda (Java 8+, same thing in one line!)
Calculator add      = (a, b) -> a + b;
Calculator subtract = (a, b) -> a - b;
Calculator multiply = (a, b) -> a * b;

System.out.println(add.calculate(10, 5));       // 15
System.out.println(subtract.calculate(10, 5));  // 5
System.out.println(multiply.calculate(10, 5));  // 50
```

### Java's Built-in Functional Interfaces (java.util.function)

| Interface | Takes | Returns | Method |
|-----------|-------|---------|--------|
| `Predicate<T>` | T | boolean | `test()` |
| `Function<T,R>` | T | R | `apply()` |
| `Consumer<T>` | T | nothing | `accept()` |
| `Supplier<T>` | nothing | T | `get()` |

**Note:** The `@FunctionalInterface` annotation is optional but recommended — it makes the compiler enforce the one-method rule, so you don't accidentally add a second abstract method.

---

## 6 — Interface vs Abstract Class

The most asked comparison in interviews

| Feature | Interface | Abstract Class |
|---------|-----------|----------------|
| **Keyword** | `interface` / `implements` | `abstract class` / `extends` |
| **Multiple?** | ✅ implement many | ❌ extend only one |
| **Constructor** | ❌ not allowed | ✅ allowed |
| **Instance fields** | ❌ only constants | ✅ allowed |
| **Method types** | abstract, default, static | abstract + concrete |
| **Access modifiers** | public only (implicitly) | any modifier |
| **Use when** | Unrelated classes share behaviour (CAN-DO) | Related classes share code (IS-A) |

### Interview Answer:
- **Use interface** when unrelated classes share a capability
  - Example: `Flyable` — Bird AND Airplane are different, but both can fly
  - Represents "CAN-DO" relationship
  
- **Use abstract class** when related classes share code and state
  - Example: Animal → Dog, Cat share name, age fields
  - Represents "IS-A" relationship

---

## Summary

| Aspect | Key Point |
|--------|-----------|
| **Purpose** | Define a contract (what, not how) |
| **Methods** | abstract, default, static |
| **Fields** | Only constants (public static final) |
| **Multiple** | Yes, unlimited interfaces |
| **Use Case** | Behavior/capability across unrelated classes |
| **Lambdas** | Only with functional interfaces (1 abstract method) |
| **vs Abstract** | Interface = CAN-DO, Abstract Class = IS-A |
