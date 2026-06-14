# Methods — Definition & Overloading

To help you crack product-based or service-based company interviews after your time at TCS, this note is split into three parts: **The Simple Concept**, **The Code Example**, and **The Exact Interview Questions** you will face.

## Part 1: Methods in Java (The Basics)

### What is a Method? (In Simple Terms)
Think of a method as a **vending machine**. You put something in (Inputs/Parameters), the machine does some processing inside, and it drops something out (Output/Return Type).

Instead of writing the same logic 10 times, wrap it inside a method and call it whenever needed. This makes code reusable and clean.

### Method Definition Structure
```java
public int addNumbers(int a, int b) {
    int sum = a + b;
    return sum;
}
```

- `public` (Access Modifier): who can see/use this method.
- `int` (Return Type): type of data returned. Use `void` if nothing is returned.
- `addNumbers` (Method Name): name to call the method.
- `(int a, int b)` (Parameters/Arguments): the inputs.
- `return sum;`: delivers the output.

---

## Part 2: Method Overloading (The Core Interview Winner)

### What is Method Overloading?
Multiple methods with the same name but different inputs (parameters) inside the same class.

Example: `pay()` processes different payment types depending on parameters passed.

### The Golden Rules of Overloading
The compiler distinguishes overloaded methods ONLY by their **argument list** (the method signature). You can overload by changing:
1. Number of parameters
2. Data types of parameters
3. Sequence/order of parameters

⚠️ Changing only the return type is NOT overloading and causes a compile-time error.

### Code Example
```java
class Calculator {
    // Takes two ints
    public int multiply(int a, int b) { return a * b; }

    // Overloaded: three ints
    public int multiply(int a, int b, int c) { return a * b * c; }

    // Overloaded: two doubles
    public double multiply(double a, double b) { return a * b; }
}
```

---

## Part 3: TCS to Product-Company Level Interview Questions

Q1: Why do we need Method Overloading? Why not separate names like `multiplyTwo()` and `multiplyThree()`?

Answer: It increases readability and provides a simple API. Use a single name for the same conceptual action with different inputs.

Q2: Is Method Overloading Compile-time or Runtime Polymorphism?

Answer: Compile-time Polymorphism (Static Binding) — the compiler picks the method during compilation based on the arguments.

Q3: Can we overload by changing only the return type?

Answer: No. Changing only return type causes a compile-time error because the compiler uses name + parameters to identify methods.

Q4: What if I pass an `int` to a method that expects a `double`, and no `int` version exists?

Example:
```java
public void show(double d) { System.out.println("Double version"); }
// calling: show(5);
```
Answer: Java performs implicit widening (type promotion). `int` → `double` is allowed and `show(5)` calls the double version.

Q5: Can we overload `main` in Java?

Answer: Yes — you can have multiple `main` methods with different parameters, but the JVM always uses `public static void main(String[] args)` as the entry point.
