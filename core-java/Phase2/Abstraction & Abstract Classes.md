# Phase 2 Topic 5 — Abstraction & Abstract Classes

## What is Abstraction? (In Simple Terms)

Abstraction is the process of **hiding internal implementation details and showing only the essential functionality** to the user.

Think of driving a car: You press the accelerator, and the car moves. You don't need to know how the fuel injection works or how cylinders pump inside the engine. The pedals are your abstract interface; the engine details are hidden.

## What is an Abstract Class?

An abstract class is a **restricted class that cannot be used to create objects** (you cannot do `new Vehicle()`). To access it, it must be inherited by another class.

It can contain:

1. **Abstract Methods:** Methods that only have a declaration (a name and a signature) but **no body/code**. The child class *must* fill in the body.
2. **Concrete Methods:** Regular methods that already have fully written code that child classes can readily reuse.

## Part 2: The Code Implementation

Imagine you are designing a payment engine processing system. The core framework knows that every payment must process a transaction, but it doesn't know the exact security steps for Credit Cards vs. UPI.

```java
// Abstract Parent Class
abstract class PaymentProcessor {
    
    // 1. Abstract Method: NO BODY! It's a strict rule/contract.
    abstract void capturePayment(double amount);
    
    // 2. Concrete Method: Regular code that every child can instantly use.
    void generateReceipt(String transactionId) {
        System.out.println("Receipt generated for Transaction ID: " + transactionId);
    }
}

// Child Class 1 - MUST implement capturePayment
class UPIProcessor extends PaymentProcessor {
    @Override
    void capturePayment(double amount) {
        System.out.println("Processing UPI payment of ₹" + amount + " using MPIN.");
    }
}

// Child Class 2 - MUST implement capturePayment
class CardProcessor extends PaymentProcessor {
    @Override
    void capturePayment(double amount) {
        System.out.println("Processing Credit Card payment of ₹" + amount + " via Payment Gateway.");
    }
}

public class Main {
    public static void main(String[] args) {
        // Line below would give a COMPILE ERROR: 'PaymentProcessor is abstract; cannot be instantiated'
        // PaymentProcessor processor = new PaymentProcessor(); 
        
        // Runtime Polymorphism: Parent reference pointing to child object
        PaymentProcessor payment = new UPIProcessor();
        payment.capturePayment(1500.00); // Calls UPI version
        payment.generateReceipt("TXN98765"); // Reuses parent concrete method
    }
}
```

## Part 3: TCS to Product-Company Level Interview Questions

### Q1: If an abstract class cannot be instantiated (we cannot do `new`), can it have a constructor?

**Your Answer:**

"Yes, an abstract class **can have a constructor**. Even though we cannot create an object of the abstract class directly, its constructor is executed when a child class object is created via the `super()` call inside the child's constructor. This is useful for initializing common fields used by all subclasses."

### Q2: Can a class be declared abstract without having any abstract methods inside it?

**Your Answer:**

"Yes! A class can be declared `abstract` even if all of its methods are fully concrete (have bodies). By doing this, you are telling the Java compiler: *'I want this class to act purely as a blueprint for inheritance, and I don't want anyone creating standalone objects of this class.'*"

### Q3: Can an abstract method be declared as `static` or `private` or `final`?

**Your Answer:**

"No, absolutely not.

- **`private` or `final`:** Abstract methods *rely* on being overridden by child classes to function. Making them `private` or `final` prevents overriding, creating a direct compilation conflict.
- **`static`:** Static methods belong to the class template itself and can be called without an object. An abstract method has no implementation body, so calling a static abstract method would have no execution logic to run, resulting in a compile-time error."

### Q4: What is the difference between Abstraction and Encapsulation? (Very Common Trap Question!)

**Your Answer:**

"They look similar but solve different design problems:

- **Abstraction** is about *hiding complexity* (hiding design/implementation details). It answers *WHAT* the object does rather than *HOW* it does it (e.g., using abstract classes and interfaces).
- **Encapsulation** is about *data hiding and security*. It binds the data (variables) and code (methods) together into a single unit and restricts direct access using private modifiers and public getters/setters."
