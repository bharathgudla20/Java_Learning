# Phase 2 Topic 4 — Polymorphism

## 1 — What is Polymorphism?

Polymorphism comes from Greek: **Poly** (Many) + **Morphs** (Forms).

It simply means "One name, many forms."

Think of yourself (Bharath):

- When you are at TCS, you behave like an Employee.
- When you are with your parents, you behave like a Son.
- When you are in a bus, you behave like a Passenger.

You are the same person (one identity), but your behavior changes entirely based on the context or environment you are in.

In Java, there are **two completely different types** of Polymorphism:

### Compile-time Polymorphism (Static Binding)

- Handled by the compiler.
- Best example: **Method Overloading** (which we already learned in Phase 1!).
- The compiler knows exactly which method to call just by looking at your arguments.

### Runtime Polymorphism (Dynamic Binding)

- Handled by the JVM during execution.
- Best example: **Method Overriding**.
- This is what we will focus on today.

## 2 — What is Method Overriding?

Overriding happens when a Child class rewrites a method that it inherited from a Parent class to give it a specific behavior.

Imagine a parent company designing a generic system for a TaxCalculator with a method `calculateTax()`.

- TCS might calculate tax one way.
- Google might calculate tax another way.

Both classes have the exact same method signature `calculateTax()`, but different outputs.

## 3 — The Code Implementation (The Interviewer's Favorite Structure)

Pay close attention to how the object is created in the main method below. This is called **Upcasting**.

```java
// Parent Class
class Company {
    void standardHike() {
        System.out.println("Standard company hike is 5%");
    }
}

// Child Class 1
class TCS extends Company {
    // Overriding the parent's method
    @Override
    void standardHike() {
        System.out.println("TCS variable hike is 7%");
    }
}

// Child Class 2
class ProductCompany extends Company {
    // Overriding the parent's method
    @Override
    void standardHike() {
        System.out.println("Product company hike is 15%");
    }
}

public class Main {
    public static void main(String[] args) {
        // UPCASTING: Parent reference pointing to a Child object
        Company myCompany = new TCS(); 
        
        // Which method runs? Parent's or TCS's?
        myCompany.standardHike(); // Output: TCS variable hike is 7%
        
        // Changing the object reference dynamically
        myCompany = new ProductCompany();
        myCompany.standardHike(); // Output: Product company hike is 15%
    }
}
```

## 4 — Why did this happen? (Dynamic Method Dispatch)

Look at the line: `Company myCompany = new TCS();`

**At Compile Time:**
- The compiler checks the Reference Type (`Company`).
- It checks: "Does the Company class have a `standardHike()` method?"
- Yes, it does. Code compiles fine.

**At Runtime:**
- The JVM looks at the Actual Object Type on the heap (`new TCS()`).
- It says: "Aha, this is a TCS object, let me run the overridden version inside TCS!"

This is **Dynamic Method Dispatch** — the decision of which method to call is made at runtime, not at compile time.

## 5 — TCS to Product-Company Level Interview Questions

### Q1: What is Upcasting, and why do we use a Parent reference to hold a Child object (`Parent p = new Child();`)?

**Your Answer:**

"This is called Upcasting. We do this to achieve loose coupling and flexibility. If I design a method that accepts a Company reference, that method can automatically accept any future child class (like TCS, Google, or Meta) without breaking or rewriting the core logic. It allows us to write scalable code."

Example:

```java
public void giveBonus(Company company, double bonus) {
    company.calculateBonus(bonus);  // Works for ANY Company subclass!
}

giveBonus(new TCS(), 5000);           // Works
giveBonus(new ProductCompany(), 8000); // Works
giveBonus(new Google(), 10000);        // Works (future class)
```

### Q2: Can we override a static method?

**Your Answer:**

"No, we cannot override static methods. If you define a static method with the same name and signature in both parent and child classes, it is called **Method Hiding**, not overriding. Static methods belong to the Class itself, not to the object instance, so polymorphism/dynamic binding does not apply to them."

Example:

```java
class Parent {
    static void display() {
        System.out.println("Parent static");
    }
}

class Child extends Parent {
    static void display() {    // This is Method Hiding, NOT overriding
        System.out.println("Child static");
    }
}

Parent p = new Child();
p.display();  // Output: Parent static (based on reference type, not object type)
```

### Q3: What is the purpose of the `@Override` annotation? Is it mandatory?

**Your Answer:**

"It is not mandatory, but it is a best practice. It tells the compiler to check that the method actually matches a method in the parent class. If I make a typo (e.g., spelling it `standardhike()` with a lowercase 'h'), the compiler will throw an error immediately instead of letting me create a brand new method by mistake."

Example:

```java
class Company {
    void standardHike() { }
}

class TCS extends Company {
    @Override
    void standardHike() {   // Compiler checks: "This method exists in parent? Yes!"
        System.out.println("TCS hike is 7%");
    }
    
    // @Override     // If we uncomment this and make a typo:
    // void standardhike() { }  // Compiler ERROR: Method does not override parent method!
}
```

### Q4: Can we override a `private` or a `final` method in Java?

**Your Answer:**

"No.

- **private methods** are restricted only to that specific class and are not inherited by child classes, so they cannot be overridden.
- **final methods** are explicitly locked by the developer to prevent alteration. Trying to override a final method results in a compile-time error."

Example:

```java
class Company {
    private void secret() { }        // Private — not inherited
    final void locked() { }          // Final — cannot be overridden
    void normal() { }                // Can be overridden
}

class TCS extends Company {
    void secret() { }                // This is a NEW method, not an override
    
    // @Override
    // void locked() { }              // COMPILE ERROR: Cannot override final method
    
    @Override
    void normal() { }                // OK — normal override
}
```

### Q5: What are the strict rules for Method Overriding regarding Return Types and Access Modifiers?

**Your Answer:**

**1. Access Modifiers:** The overriding method in the child class cannot be more restrictive than the parent method.

- If the parent method is `protected`, the child method must be `protected` or `public`. It cannot be `private`.

**2. Return Type:** It must be the same, or it can be a **Covariant Return Type** (a subclass of the original return type, introduced in Java 5).

Example:

```java
class Company {
    protected Employee getEmployee() { return new Employee(); }
}

class TCS extends Company {
    @Override
    public Employee getEmployee() {  // OK — public is less restrictive than protected
        return new Employee();
    }
}

// WRONG — more restrictive:
class Wrong extends Company {
    @Override
    private Employee getEmployee() { }  // COMPILE ERROR — private is more restrictive
}

// Covariant Return Type (Java 5+):
class Company {
    public Animal getAnimal() { return new Animal(); }
}

class TCS extends Company {
    @Override
    public Dog getAnimal() {   // OK — Dog is a subclass of Animal (covariant)
        return new Dog();
    }
}
```

## 6 — Key Takeaways

- **Polymorphism** = "One name, many forms"
- **Runtime Polymorphism** = achieved through inheritance + method overriding
- **Upcasting** = parent reference holds child object (used for flexibility)
- **Dynamic Method Dispatch** = JVM decides which method to call at runtime based on the actual object type
- **Method Overriding Rules:**
  - Access modifiers cannot be more restrictive
  - Return type must be same or covariant
  - Cannot override `private` or `final` methods
  - Static methods cannot be overridden (method hiding instead)
