# Phase 6, Topic 5: Structural Design Patterns

Structural Design Patterns — Adapter, Decorator, Facade, Proxy, Composite. These deal with how classes and objects are composed together to form larger structures, rather than how they are created.

## 1. Adapter Pattern

**Definition:** Converts the interface of a class into another interface the client expects.

```java
class OldPrinter {
    void printOld(String msg) { System.out.println("Old: " + msg); }
}

interface ModernPrinter {
    void print(String msg);
}

class PrinterAdapter implements ModernPrinter {
    private OldPrinter oldPrinter;

    PrinterAdapter(OldPrinter oldPrinter) { this.oldPrinter = oldPrinter; }

    public void print(String msg) { oldPrinter.printOld(msg); }
}
```

## 2. Decorator Pattern

**Definition:** Adds behavior dynamically without changing the original class.

```java
interface Coffee { double cost(); }

class SimpleCoffee implements Coffee {
    public double cost() { return 2.0; }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    CoffeeDecorator(Coffee c) { this.decoratedCoffee = c; }
}

class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee c) { super(c); }

    public double cost() { return decoratedCoffee.cost() + 0.5; }
}
```

## 3. Facade Pattern

**Definition:** Provides a simplified interface to a complex subsystem.

## 4. Proxy Pattern

**Definition:** Controls access to an object, often for lazy loading or security.

## 5. Composite Pattern

**Definition:** Treats individual objects and groups uniformly in a tree structure.

## Quick Comparison

- Adapter: makes incompatible interfaces work together
- Decorator: adds behavior dynamically
- Facade: simplifies complex subsystems
- Proxy: controls access to an object
- Composite: handles part-whole hierarchies
