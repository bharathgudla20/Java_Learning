# Phase 4, Topic 7: Default & Static Methods in Interfaces

Before Java 8, an interface was a strict, 100% abstract contract. If an interface declared a method, every implementing class was forced to provide a concrete implementation for it. Java 8 changed that rule by allowing interfaces to contain actual method bodies.

---

## Part 1: Why Did Java Change the Rules? (The Problem)

Imagine a large product codebase where an interface called `Robot` is implemented by thousands of classes.

If you suddenly add a new method like `chargeBattery()`, then:

- Before Java 8, every implementing class would break until each one added its own implementation.
- With Java 8, you can add a `default` method inside the interface and all existing classes automatically inherit it.

This made API evolution much safer and easier.

---

## Part 2: Default vs. Static Methods (In Simple Terms)

Java gives you two ways to write method bodies inside an interface:

### 1. Default Methods (`default`)

- Keyword: `default`
- Purpose: provides a fallback implementation
- Usage: they are instance methods, so they are inherited by classes and called on an object
- Override: implementing classes may override them

### 2. Static Methods (`static`)

- Keyword: `static`
- Purpose: acts as a utility/helper method tied to the interface itself
- Usage: called using the interface name, such as `Robot.checkSystem()`
- Override: cannot be overridden by implementing classes

---

## Part 3: Code Implementation & The Diamond Problem

```java
interface Machine {
    void turnOn();

    default void upgradeSoftware() {
        System.out.println("Machine: Upgrading core firmware OS...");
    }

    static boolean isEnergyEfficient(int wattage) {
        return wattage < 100;
    }
}

interface SmartDevice {
    default void upgradeSoftware() {
        System.out.println("SmartDevice: Downloading cloud security patches...");
    }
}

class AutonomousCar implements Machine, SmartDevice {
    @Override
    public void turnOn() {
        System.out.println("Car engine purring to life.");
    }

    @Override
    public void upgradeSoftware() {
        System.out.println("Car: Initiating safe diagnostic scan before update...");
        Machine.super.upgradeSoftware();
    }
}

public class InterfaceMethodsMastery {
    public static void main(String[] args) {
        AutonomousCar tesla = new AutonomousCar();
        tesla.turnOn();
        tesla.upgradeSoftware();

        boolean efficient = Machine.isEnergyEfficient(45);
        System.out.println("Is the machine efficient? " + efficient);
    }
}
```

---

## Part 4: The Diamond Problem

The diamond problem happens when a class implements two interfaces that both provide a default method with the same signature.

Because Java cannot automatically decide which method to inherit, the implementing class must resolve the conflict by overriding the method.

Example:

- `Machine` has `default void upgradeSoftware()`
- `SmartDevice` also has `default void upgradeSoftware()`
- `AutonomousCar` implements both, so it must override `upgradeSoftware()`

You can resolve it by:

- writing your own implementation, or
- explicitly calling one parent interface’s default method using `InterfaceName.super.methodName()`

---

## Interview Questions

### Q1: What is the Diamond Problem in Java 8 interfaces, and how does the compiler force you to resolve it?

**Answer:** The diamond problem occurs when a class implements two interfaces that both define the same default method. Java cannot choose one automatically, so the implementing class must override the method and either provide custom logic or explicitly call one of the parent interface implementations.

### Q2: If a class extends a parent class and also implements an interface, and both contain a method with the same signature, which one takes priority?

**Answer:** The parent class method takes priority. This is known as the “class wins” rule. A concrete or abstract superclass method overrides any default method from an interface.

### Q3: Why can’t interface static methods be overridden by implementing classes?

**Answer:** Static methods belong to the interface itself, not to the instances of the implementing classes. Because they are not part of dynamic polymorphism, they cannot be overridden; they can only be hidden by a method with the same name in the class.

### Q4: What is the main structural difference left between an abstract class and an interface?

**Answer:** The main difference is state and inheritance:

- Abstract classes can have instance fields and constructors.
- Interfaces can only declare constants, not mutable instance state.
- A class can implement multiple interfaces but extend only one abstract class.

---

## Final Takeaway

Default methods help evolve interfaces without breaking existing implementations.

Static methods are useful as helper methods tied to the interface itself.

Use default methods when you want backward-compatible behavior, and use static methods when you want utility logic that belongs to the interface.
