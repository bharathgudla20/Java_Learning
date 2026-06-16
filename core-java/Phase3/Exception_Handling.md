# Phase 3 Topic 1 — Exception Handling

## Part 1: What is an Exception? (In Simple Terms)

An exception is a **runtime accident**. It is an unexpected event that disrupts the normal flow of your program's instructions.

Think of ordering food on an app:

- **Normal flow:** You pick food, pay, and the delivery rider brings it.
- **Exception flow:** The restaurant runs out of chicken, or the delivery rider's bike gets a flat tire.

If the app doesn't have an alternative plan for a flat tire, it will crash completely. Exception handling is writing that **"Plan B"** so your system doesn't shut down mid-way.

### The Exception Hierarchy

In Java, exceptions are objects. It all starts with a class called `Throwable`. Underneath it, it splits into two primary paths:

1. **Error:** Severe system-level disasters that your code cannot fix or recover from (e.g., `OutOfMemoryError` or `StackOverflowError`). You don't try to catch these; your program just dies.
2. **Exception:** Operational mishaps that your code **can and should** handle. This splits into two critical interview sub-groups:
   - **Checked Exceptions (Compile-time):** Mishaps outside the application's direct control (like a file missing on the hard drive: `FileNotFoundException`). The Java compiler checks your code and *forces* you to write a Plan B before it even lets you run it.
   - **Unchecked Exceptions (Runtime):** Programming mistakes or bugs (like dividing a number by zero: `ArithmeticException`, or trying to touch a `null` object: `NullPointerException`). The compiler doesn't force you to handle them; you fix them with better coding logic.

---

## Part 2: The 5 Keywords of Exception Handling

Java handles exceptions using a combination of 5 keywords: `try`, `catch`, `finally`, `throw`, and `throws`.

### 1. `try` and `catch` (The Guard and The Treatment)

- Put the dangerous code that might cause an accident inside the `try` block.
- Put the recovery logic inside the `catch` block.

### 2. `finally` (The Cleanup Crew)

A block of code that **always executes**, regardless of whether an exception was thrown or caught. It is used to close database connections or file streams so you don't leak system resources.

### 3. `throw` vs `throws` (The Exploder vs The Warning Sign)

- **`throw`:** Used inside a method to explicitly create and launch an exception object right now.
  - Example: `throw new IllegalArgumentException("Age cannot be negative");`
- **`throws`:** Attached to a method signature to give a public warning to anyone calling the method: *"Hey, call me at your own risk. I might explode with this specific checked exception."*

---

## Part 3: The Code Implementation

Here is how a clean, professional method handles input validation, custom exceptions, and resource handling.

```java
// A custom checked exception
class InvalidSalaryException extends Exception {
    public InvalidSalaryException(String message) {
        super(message);
    }
}

public class HRSystem {

    // Using 'throws' to warn callers about the checked exception
    public void onboardEmployee(String name, double salary) throws InvalidSalaryException {
        if (salary < 0) {
            throw new InvalidSalaryException("Salary cannot be negative");
        }

        // Normal onboarding logic here
        System.out.println("Employee onboarded: " + name + ", salary=" + salary);
    }

    public void run() {
        try {
            onboardEmployee("Amit", -5000);
        } catch (InvalidSalaryException e) {
            System.out.println("Onboarding failed: " + e.getMessage());
        } finally {
            // This runs NO MATTER WHAT to clean up resources
            System.out.println("Closing database connection safely.");
        }

        System.out.println("Application continues running smoothly...");
    }
}
```

---

## Part 4: TCS to Product-Company Level Interview Questions

### Q1: What is the difference between Checked and Unchecked Exceptions? Give real examples.

**Your Answer:** "The major difference is when they are enforced by the compiler.

- **Checked Exceptions** represent environmental errors outside the code's control. The compiler forces you to handle them using a try-catch block or declare them with a `throws` clause during compilation. Examples include `IOException`, `FileNotFoundException`, and `SQLException`.
- **Unchecked Exceptions** inherit from `RuntimeException` and represent logical flaws or bad programming bugs. The compiler does not verify them at compile-time. Examples include `NullPointerException`, `ArrayIndexOutOfBoundsException`, and `ArithmeticException`."

### Q2: Can you have a `try` block without a `catch` block?

**Your Answer:** "Yes, you can have a `try` block without a `catch` block, provided it is immediately followed by a **`finally`** block. This structure is useful when a method wants to let an exception bubble up to its caller but must clean up its resources before leaving. Additionally, from Java 7 onwards, you can use **Try-with-Resources** which automatically closes resources without requiring an explicit catch or finally block."

### Q3: What is Try-with-Resources, and how does it work under the hood? (High-Priority Question!)

**Your Answer:** "Introduced in Java 7, Try-with-Resources is a feature that automatically closes resources (like database connections, file streams, or sockets) at the end of the statement block.
Syntax: `try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) { ... }`.
Under the hood, any resource used within this syntax must implement the **`AutoCloseable`** interface. The compiler automatically inserts an implicit `finally` block that invokes the `.close()` method on those resources, eliminating boilerplate code and avoiding silent resource leaks."

### Q4: Is there any scenario where the `finally` block will NOT execute? (Classic Trap Question!)

**Your Answer:** "Yes, there are a few extreme cases where a `finally` block fails to execute:

1. If you call **`System.exit(0);`** inside the try or catch block, which abruptly terminates the running JVM process.
2. If the OS crashes or the server loses power.
3. If an infinite loop or a fatal deadlock happens inside the try or catch block, preventing execution from moving forward.
4. If a fatal `OutOfMemoryError` occurs that instantly panics the runtime environment."

### Q5: What is Exception Chaining? Why do we use it?

**Your Answer:** "Exception chaining is the practice of catching a lower-level exception and throwing a higher-level abstract exception while wrapping the original error inside it (using `initCause()` or via the constructor). For example, if a database query fails with a raw, low-level `SQLException`, we catch it and throw a business-level `DataAccessException`. We do this to hide internal system implementation complexities from the caller while preserving the original root cause stack trace for debugging purposes."
