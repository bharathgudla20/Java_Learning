# Phase 2 Topic 2 — Encapsulation

## 1 — What is Encapsulation?

Wrapping data and protecting it from outside interference.

Think of a capsule tablet — the medicine (data) is inside, protected by the outer shell. You can't directly touch the medicine, but you can take the tablet and it does its job.

In Java, encapsulation means hiding the internal data (fields) of a class and only allowing access through controlled methods (getters and setters).

Real-world example: An ATM machine. You can't reach inside and grab money directly. You interact through a controlled interface — insert card, enter PIN, withdraw. The internal mechanism is hidden.

Without encapsulation — dangerous!

```java
class BankAccount {
    int balance = 1000;   // public by default — anyone can touch it!
}

BankAccount acc = new BankAccount();
acc.balance = -99999;    // ANYONE can set any invalid value directly!
acc.balance = 0;         // no validation, no control — dangerous
```

Without encapsulation, anyone can set invalid data directly. There is nothing stopping someone from setting a negative bank balance or an age of -5.

With encapsulation — safe and controlled!

```java
class BankAccount {
    private int balance = 1000;   // hidden from outside

    // Getter — read the balance
    public int getBalance() {
        return balance;
    }

    // Setter-like methods — change balance only through controlled methods
    public void deposit(int amount) {
        if (amount > 0) {             // validation!
            balance += amount;
        }
    }

    public void withdraw(int amount) {
        if (amount > 0 && amount <= balance) {   // validation!
            balance -= amount;
        } else {
            System.out.println("Invalid amount or insufficient funds");
        }
    }
}

BankAccount acc = new BankAccount();
acc.balance = -99999;   // COMPILE ERROR — private! can't touch directly
acc.deposit(500);       // controlled — validation runs inside
System.out.println(acc.getBalance());   // 1500
```

Encapsulation = `private` fields + `public` getters/setters.
The outside world only sees the controlled interface, not the raw data.

## 2 — Access modifiers

4 levels — who can see what.

Access modifiers control visibility — who is allowed to read or change a field or method. Think of it like different security clearance levels.

Modifier | Same Class | Same Package | Subclass | Everywhere
--- | --- | --- | --- | ---
private | ✓ | ✗ | ✗ | ✗
default (no keyword) | ✓ | ✓ | ✗ | ✗
protected | ✓ | ✓ | ✓ | ✗
public | ✓ | ✓ | ✓ | ✓

Golden rule for encapsulation: fields → `private`. Getters/setters → `public`. This gives you full control over your data.

### Modifier details

- private
  - Most restrictive — only this class can see it.
- default (no keyword)
  - Package-level access.
- protected
  - Package + subclasses.
- public
  - Accessible everywhere.

Example:

```java
protected String breed;

class Poodle extends Dog {
    void show() {
        System.out.println(breed); // works! Poodle is a subclass
    }
}
```

Use `protected` for fields/methods you want subclasses to inherit.

## 3 — Getters and Setters

The controlled doorway to private data.

Getter = a method that reads and returns a private field.
Setter = a method that validates and sets a private field.
Together they are the controlled interface to your object's data.

```java
class Student {
    // private fields — hidden from outside
    private String name;
    private int age;
    private double marks;

    // Constructor
    Student(String name, int age, double marks) {
        this.name  = name;
        setAge(age);       // use setter even in constructor for validation!
        setMarks(marks);
    }

    // Getters — read only, no validation needed
    public String getName()  { return name; }
    public int    getAge()   { return age; }
    public double getMarks() { return marks; }

    // Setters — write with validation
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public void setAge(int age) {
        if (age >= 1 && age <= 150) {   // validate range!
            this.age = age;
        } else {
            System.out.println("Invalid age: " + age);
        }
    }

    public void setMarks(double marks) {
        if (marks >= 0 && marks <= 100) {
            this.marks = marks;
        } else {
            System.out.println("Marks must be between 0 and 100");
        }
    }
}
```

## 4 — Try it — Student object playground

Set values through setters — see validation in action.

```java
Student s = new Student("Bharath", 25, 85);

// Setter validations:
setName("Bharath")   → ✅ accepted — name="Bharath"
setAge(25)            → ✅ accepted — age=25
setMarks(85)          → ✅ accepted — marks=85

// Getters:
s.getName()  → "Bharath"
s.getAge()   → 25
s.getMarks() → 85

// Direct access — always blocked:
s.name = "..."  → ❌ COMPILE ERROR — name is private!
```

## 5 — Why encapsulation matters — 3 real benefits

1. Validation — prevent invalid data

- Setter can reject invalid values.
- Without encapsulation, someone could set `age = -5` or `balance = -9999` with no checks.

2. Flexibility — change internals without breaking outside code

- If you change how a field is stored internally (for example, store marks as `0.0-1.0` instead of `0-100`), you only update the getter/setter.
- All code that calls `getMarks()` still works — it doesn't know or care about internal changes.

3. Read-only or write-only fields

- Want a field that can only be read, never changed from outside? Just provide a getter, no setter.
- Want write-only (like a password)? Provide a setter, no getter.

```java
class Person {
    private String name;
    private String password;
    private final int id;    // read-only after creation

    Person(int id, String name, String password) {
        this.id       = id;
        this.name     = name;
        this.password = password;
    }

    // Read-only — getter only, no setter
    public int getId() { return id; }

    // Normal — both getter and setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Write-only — setter only, no getter (password)
    public void setPassword(String password) {
        if (password != null && password.length() >= 8) {
            this.password = password;
        }
    }
}
```
