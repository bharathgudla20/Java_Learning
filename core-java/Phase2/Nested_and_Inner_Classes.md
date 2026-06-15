# Phase 2 Topic 7 — Nested & Inner Classes

## Part 1: Nested & Inner Classes (The Concept)

### What is it? (In Simple Terms)

A **nested class** is simply a class written inside another class.

Think of a **Car and its Engine**:
An `Engine` doesn't make much sense standing all by itself in a separate file if it is uniquely designed just for a specific `Car`. By putting the `Engine` class *inside* the `Car` class, you cleanly bundle them together.

Java divides nested classes into two main buckets based on the `static` keyword:

1. **Static Nested Class:** It has the `static` keyword. It behaves just like a regular outer class, it's just housed inside for logical grouping. It **cannot** see the non-static private variables of the outer class without an object.

2. **Inner Class (Non-Static):** It does *not* have the `static` keyword. It is deeply tied to the outer class. Every inner class object **must** belong to an instance of the outer class, and it can see all variables of the outer class directly.

---

## Part 2: The Types & Code Implementations

There are four variations you need to know for an interview.

### 1. Static Nested Class vs 2. Member Inner Class (Non-Static)

```java
class OuterClass {
    private String outerField = "Outer Data";
    private static String staticOuterField = "Static Outer Data";

    // 1. Static Nested Class
    static class StaticNested {
        void display() {
            // System.out.println(outerField); // COMPILE ERROR! Can't see non-static fields.
            System.out.println("Inside Static Nested: " + staticOuterField);
        }
    }

    // 2. Member Inner Class (Non-static)
    class InnerClass {
        void display() {
            // Can see everything directly!
            System.out.println("Inside Member Inner: " + outerField + " & " + staticOuterField);
        }
    }
}
```

#### How to Create Objects — Syntax Difference!

```java
public class Main {
    public static void main(String[] args) {
        // Instantiating the Static Nested Class (No Outer object required)
        OuterClass.StaticNested staticObj = new OuterClass.StaticNested();
        staticObj.display();

        // Instantiating the Inner Class (Requires an Outer object first!)
        OuterClass outerObj = new OuterClass();
        OuterClass.InnerClass innerObj = outerObj.new InnerClass(); 
        innerObj.display();
    }
}
```

**Key Difference:**
- **Static Nested:** `new OuterClass.StaticNested()` — no outer instance needed
- **Inner Class:** `outerObj.new InnerClass()` — must create outer instance first

---

### 3. Local Inner Class

A class written **inside a method**. It is only visible and usable within that specific method execution.

```java
void myMethod() {
    class LocalInner {
        void print() { 
            System.out.println("Hello from Local Inner Class"); 
        }
    }
    LocalInner li = new LocalInner();
    li.print();
}
```

**Scope:** Only available within the method where it's defined.

---

### 4. Anonymous Inner Class (Huge Interview Favorite!)

An inner class that has **no name**. You declare it and instantiate it at the exact same time. It is used to override a method of a class or interface on the fly without creating a dedicated subclass file.

```java
// Suppose we have a generic interface
interface PriceCalculator {
    void calculate();
}

public class Test {
    public static void main(String[] args) {
        // Creating an anonymous inner class to implement the interface instantly
        PriceCalculator tcsCalculator = new PriceCalculator() {
            @Override
            public void calculate() {
                System.out.println("Calculating special service tax logic...");
            }
        };
        tcsCalculator.calculate();
    }
}
```

**Key Point:** Declaration and instantiation happen at the same time. Useful for quick, one-off implementations.

---

## Part 3: Interview Questions & Answers

### Q1: What is the main difference between a Static Nested Class and a Non-Static Inner Class regarding Memory/Instantiation?

**Your Answer:** 

"A **Static Nested Class** is independent of the outer class instance. It can be instantiated directly using `new Outer.StaticNested();` and does not hold an implicit reference to an outer object, preventing memory leaks.

A **Non-Static Inner Class** is strongly tied to an instance of the outer class. It cannot exist without an outer object, instantiated via `outerInstance.new Inner();`. It holds a hidden reference to the outer instance, meaning the outer object cannot be garbage collected as long as the inner class object is alive."

---

### Q2: What is a Memory Leak Hazard Associated with Non-Static Inner Classes?

**Your Answer:** 

"Since non-static inner classes hold an implicit pointer to their outer class instance, if you create a short-lived inner object (like a background thread or a callback handler) and pass it to a framework, the outer class instance will remain pinned in memory even if it's no longer needed. This can lead to a severe memory leak, which is why static nested classes are preferred if access to outer instance variables isn't strictly required."

**Example Scenario:**
- Create an `OuterClass` instance
- Create an inner class listener/thread inside it
- Pass the listener to an external framework that holds a reference to it
- Even when you're done with the `OuterClass`, it can't be garbage collected because the listener still exists

---

### Q3: Why Can Local Inner Classes Only Access `final` or `effectively final` Variables Inside a Method?

**Your Answer:** 

"Local inner class objects might outlive the method execution (for example, if the class starts a separate thread). The local variables of a method live on the stack and are destroyed when the method returns, but the inner class object lives on the heap. To handle this, Java makes a *copy* of the local variable for the inner class object. If the variable's value could change, the copy and the original would get out of sync. To prevent this, Java forces local variables accessed by inner classes to be unchangeable (`final` or `effectively final`)."

```java
void myMethod() {
    int x = 10;         // can be accessed (effectively final after first assignment)
    // x = 20;          // if you uncomment this, compiler error!
    
    class LocalInner {
        void print() { 
            System.out.println(x);  // x is effectively final
        }
    }
}
```

---

### Q4: How do Lambda Expressions in Java 8 Relate to Anonymous Inner Classes? Can a Lambda Replace Any Anonymous Class?

**Your Answer:** 

"Lambda expressions are a cleaner, more concise way to replace anonymous inner classes, but **only if the anonymous class implements a Functional Interface** (an interface with exactly one abstract method). If the anonymous inner class extends an abstract or concrete class, or implements an interface with multiple methods, it *cannot* be replaced by a lambda expression."

**Example:**

```java
// Anonymous inner class (can be replaced with lambda)
PriceCalculator calc1 = new PriceCalculator() {
    @Override
    public void calculate() {
        System.out.println("Calculating...");
    }
};

// Lambda replacement (cleaner!)
PriceCalculator calc2 = () -> System.out.println("Calculating...");

// Cannot be replaced with lambda (multiple methods)
class MyListener implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) { }
    @Override
    public void mousePressed(MouseEvent e) { }
    @Override
    public void mouseReleased(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
}
```

---

## Quick Reference Table

| Type | Keyword | Access | Instantiation | Use Case |
|------|---------|--------|---------------|----------|
| **Static Nested** | `static` | Only static members of outer | `new Outer.Nested()` | Logical grouping, no outer reference needed |
| **Member Inner** | none | All outer members | `outer.new Inner()` | Needs access to outer instance |
| **Local Inner** | none | Outer & method `final` vars | Within method only | Temporary implementations within methods |
| **Anonymous Inner** | none | Outer & method `final` vars | `new Interface() { }` | Quick one-off implementations |

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Nested Classes** | Classes inside other classes for logical grouping |
| **Static Nested** | Independent, no hidden outer reference, prevents memory leaks |
| **Inner Class** | Tied to outer instance, hidden reference, memory leak risk |
| **Local Inner** | Only in methods, can only access `final`/`effectively final` variables |
| **Anonymous Inner** | No name, declaration + instantiation in one, often replaced by lambdas |
| **Memory Leak Risk** | Non-static inner classes can prevent outer instance garbage collection |
| **Lambda vs Anonymous** | Lambda only works with functional interfaces (1 abstract method) |
