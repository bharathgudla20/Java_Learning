# Phase 2 Topic 3 — Inheritance

## 1 — What is Inheritance?

Child class gets everything from parent class automatically.

Think of inheritance like genes in a family. A child naturally gets the parents' features — eye colour, height, etc. — without having to define them again.

In Java, a child class (subclass) automatically gets all the fields and methods of the parent class (superclass). You only write the NEW things the child adds or changes.

Real example: Animal is the parent. Dog and Cat are children. Both animals breathe, eat, sleep — defined once in Animal. Dog adds `bark()`, Cat adds `meow()` — only new behaviours are added in child classes.

```java
// PARENT class — common behaviour written once
class Animal {
    String name;
    int age;

    void eat()   { System.out.println(name + " is eating"); }
    void sleep() { System.out.println(name + " is sleeping"); }
    void breathe(){ System.out.println(name + " is breathing"); }
}

// CHILD class — gets Animal's features + adds its own
class Dog extends Animal {   // 'extends' keyword
    String breed;

    void bark() { System.out.println(name + " says: Woof!"); }
    // name field is inherited from Animal — no need to redefine!
}

class Cat extends Animal {
    void meow() { System.out.println(name + " says: Meow!"); }
}

// Usage:
Dog d = new Dog();
d.name = "Bruno";      // inherited from Animal
d.eat();               // inherited from Animal → "Bruno is eating"
d.bark();              // Dog's own method → "Bruno says: Woof!"
d.sleep();             // inherited from Animal
```

Keyword to remember: `extends` for inheritance.
"Dog extends Animal" = Dog IS-A Animal.

This is the IS-A relationship — always check: does it make sense to say "Dog IS-A Animal"? Yes! So inheritance is correct here.

## 2 — Inheritance hierarchy

Multi-level inheritance: Animal → Dog → GoldenRetriever

```java
class GoldenRetriever extends Dog {
    String coatColour;  // GoldenRetriever's OWN field

    void fetch() { 
        System.out.println(name + " is fetching"); 
    }   // GoldenRetriever's OWN method
}

// GoldenRetriever INHERITS from Dog:
//   fields  : breed       (from Dog)
//   methods : bark()      (from Dog)

// GoldenRetriever INHERITS from Animal (through Dog):
//   fields  : name, age   (from Animal)
//   methods : eat(), sleep(), breathe()

// GoldenRetriever ADDS:
//   fields  : coatColour
//   methods : fetch()

// Total available: name, age, breed, coatColour,
//                  eat(), sleep(), breathe(), bark(), fetch()
```

## 3 — The `super` keyword

Refers to the PARENT class — 3 uses.

`super` is the opposite of `this`. While `this` refers to the current object, `super` refers to the parent class.

Uses:
- `super()` — call parent constructor
- `super.field` — access parent field
- `super.method()` — call parent method

```java
class Animal {
    String name;
    int age;

    Animal(String name, int age) {
        this.name = name;
        this.age  = age;
        System.out.println("Animal created: " + name);
    }
}

class Dog extends Animal {
    String breed;

    Dog(String name, int age, String breed) {
        super(name, age);   // ← calls Animal's constructor
                            // MUST be the FIRST line!
        this.breed = breed;
        System.out.println("Dog created: " + breed);
    }
}

// new Dog("Bruno", 3, "Labrador") prints:
// Animal created: Bruno
// Dog created: Labrador
```

`super()` must always be the FIRST line in the child constructor. If you forget it and the parent has no default constructor, you get a compile error.

## 4 — What gets inherited? What doesn't?

✓ Inherited:
- `public` and `protected` fields and methods
  - Child class gets all `public` and `protected` members from parent automatically.
- `default` (package-private) members — if same package
  - Default access members are inherited only if child is in the same package as parent.

✗ NOT inherited:
- `private` fields and methods
  - Private members stay hidden in the parent.
  - Child cannot access them directly — but can use them through inherited public getters/setters.
- Constructors
  - Constructors are never inherited.
  - But child constructors automatically call `super()` (parent's no-arg constructor) as the first step unless you specify otherwise.
- Multiple inheritance with classes
  - A class can only `extend` ONE parent class.
  - Java does not support multiple class inheritance to avoid the "diamond problem".
  - (Interfaces solve this — coming in Topic 6.)

## 5 — Constructor chaining with `super()`

When you create a child object, parent constructor runs FIRST.

Every child constructor automatically calls the parent's no-arg constructor as its very first step — even if you don't write it. This is Java's way of ensuring the parent part of the object is initialised before the child part.

```java
class Animal {
    String name;
    Animal(String name) {
        this.name = name;
        System.out.println("Animal constructor: " + name);
    }
}

class Dog extends Animal {
    String breed;
    Dog(String name, String breed) {
        super(name);   // MUST be first line — calls Animal constructor
        this.breed = breed;
        System.out.println("Dog constructor: " + breed);
    }
}

// When you do: new Dog("Bruno", "Labrador")
// Output order:
// 1. Animal constructor: Bruno   ← parent runs first!
// 2. Dog constructor: Labrador   ← then child runs
```

If parent has NO default (no-arg) constructor, child MUST explicitly call `super(args)` as the first line. Otherwise compile error — Java can't find a matching parent constructor to call.

### Trace constructor call order

Creating: `new GoldenRetriever("Bruno", "Labrador", "Golden")`

```java
// Step 1 — GoldenRetriever constructor starts
//   → calls super("Bruno", "Labrador")  [Dog constructor]

// Step 2 — Dog constructor starts
//   → calls super("Bruno")  [Animal constructor]

// Step 3 — Animal constructor runs FIRST (deepest parent first)
System.out.println("Animal constructor runs: name = Bruno");

// Step 4 — Dog constructor resumes
System.out.println("Dog constructor runs: breed = Labrador");

// Step 5 — GoldenRetriever constructor resumes
System.out.println("GoldenRetriever constructor runs: colour = Golden");

// OUTPUT ORDER:
// Animal constructor runs: name = Bruno
// Dog constructor runs: breed = Labrador
// GoldenRetriever constructor runs: colour = Golden

// KEY: Parent always initialised BEFORE child!
```

## 6 — Types of inheritance in Java

### Single inheritance
One child, one parent.

```java
class Animal { }
class Dog extends Animal { }
```

### Multilevel inheritance
Grandparent → Parent → Child.

```java
class Animal { }
class Dog extends Animal { }
class GoldenRetriever extends Dog { }
```

### Hierarchical inheritance
One parent, multiple children.

```java
class Animal { }
class Dog extends Animal { }
class Cat extends Animal { }
class Bird extends Animal { }
```

### Multiple inheritance with classes — NOT allowed

```java
// Java does NOT allow this with classes:
class A { void methodA() { ... } }
class B { void methodB() { ... } }

// class C extends A, B  → COMPILE ERROR in Java!

// WHY? The "diamond problem":
// If both A and B have a method with the same name,
// Java doesn't know which one C should inherit.

// SOLUTION: Use interfaces (Topic 6)
// A class can implement multiple interfaces!
// interface A + interface B + class C implements A, B → ALLOWED
```

## 7 — Common interview questions

### Q: What is the difference between `extends` and `implements`?

`extends` → used to inherit from a CLASS (single only)
```java
class Dog extends Animal
```

`implements` → used to implement an INTERFACE (multiple allowed)
```java
class Dog implements Runnable, Serializable
```

A class can do BOTH:
```java
class Dog extends Animal implements Runnable, Comparable
```

### Q: Does Java support multiple inheritance?

With CLASSES: NO — a class can only extend ONE class

```java
class C extends A, B;  // → COMPILE ERROR
```

With INTERFACES: YES — a class can implement many interfaces

```java
class C implements A, B;  // → ALLOWED
```

Reason: Interfaces avoid the diamond problem because they define behaviour contracts, not implementations (until Java 8 default methods — but that's Topic 6!).

### Q: What happens if parent has no default constructor?

If parent has ONLY a parameterized constructor:

```java
class Animal {
    Animal(String name) { ... }  // no Animal() {}
}
```

Child MUST explicitly call `super(args)` as first line:

```java
class Dog extends Animal {
    Dog(String name) {
        super(name);   // required! no default to auto-call
    }
}
```

Forgetting `super(name)` here → COMPILE ERROR.

### Q: Can a child access `private` members of parent?

Direct access: NO

```java
class Animal { 
    private int age; 
}
class Dog extends Animal {
    void show() { 
        System.out.println(age);  // COMPILE ERROR!
    }
}
```

Through inherited getters: YES

```java
class Animal {
    private int age;
    public int getAge() { return age; }  // public getter
}
class Dog extends Animal {
    void show() {
        System.out.println(getAge());  // works! getter is inherited
    }
}
```

## 8 — Quiz interview

**Q: What is the IS-A relationship in inheritance?**

- A Dog HAS-A Animal inside it
- A Dog IS-A Animal — Dog can be used wherever Animal is expected ✅
- Animal IS-A Dog
- No relationship between parent and child

✅ **Correct!** IS-A means the child can be treated as the parent type. Dog IS-A Animal — so you can write:

```java
Animal a = new Dog();
```

This is the foundation of polymorphism (next topic!).
