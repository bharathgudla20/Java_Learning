# Phase 2 Topic 1 — Classes, Objects & Constructors

## 1 — Class vs Object

Think of a class like an architect's blueprint for a house. The blueprint describes how many rooms, windows, doors, etc. But the blueprint itself is NOT a house — you can't live in it. When you actually BUILD a house using that blueprint, THAT is the object.

A class is the template or blueprint. An object is the actual thing built from it.

- Class
  - The template/blueprint.
  - Written once.
  - Defines what fields (data) and methods (behaviour) every object will have.
  - No memory allocated for data yet.

- Object
  - A real instance built from the class.
  - Memory is allocated.
  - Has its own copy of every field.
  - You can create many objects from one class.

```java
// CLASS — the blueprint (written once)
class Dog {
    String name;     // field — every dog has a name
    int age;         // field — every dog has an age
    String breed;    // field

    void bark() {                          // method — behaviour
        System.out.println(name + " says: Woof!");
    }
}

// OBJECTS — actual dogs built from the blueprint
Dog dog1 = new Dog();    // object 1
dog1.name = "Bruno";
dog1.age  = 3;

dog2 = new Dog();       // object 2 — separate from dog1
dog2.name = "Max";
dog2.age  = 5;

dog1.bark();   // Bruno says: Woof!
dog2.bark();   // Max says: Woof!
```

## 2 — What happens when you write `new Dog()`

4 things happen behind the scenes:

1. Memory allocated on the heap
   - Java reserves space in heap memory for all the fields of the object (`name`, `age`, `breed`, etc.)

2. Fields set to default values
   - `int` fields → `0`
   - `String`/object fields → `null`
   - `boolean` → `false`
   - `double` → `0.0`

3. Constructor runs
   - The constructor method executes — initializes the object with your values.
   - If you didn't write one, Java runs a hidden default constructor.

4. Reference returned
   - A memory address (reference) is returned and stored in your variable.
   - `Dog dog1` holds the address, not the actual object.

## 3 — Constructors

A constructor is a special method that runs automatically when you do `new ClassName()`.
Its job is to initialize (set up) the object's fields.

A constructor has:
- The same name as the class
- No return type — not even `void`

### Default constructor

```java
class Dog {
    String name;
    int age;

    // Default constructor — no parameters
    Dog() {
        name = "Unknown";
        age  = 0;
    }
}

// Usage:
Dog d = new Dog();
System.out.println(d.name);  // "Unknown"
System.out.println(d.age);   // 0
```

If you write NO constructor at all, Java auto-creates an empty default constructor behind the scenes:

```java
// Dog() {}
```

But once you write ANY constructor, Java stops auto-creating it.

If you write even ONE parameterized constructor and forget the default one, calling `new Dog()` with no args will give a compile error.

### Parameterized constructor

```java
class Dog {
    String name;
    int age;
    String breed;

    Dog(String name, int age, String breed) {
        this.name  = name;
        this.age   = age;
        this.breed = breed;
    }
}

Dog d = new Dog("Bruno", 3, "Labrador");
```

## 4 — The `this` keyword

`this` refers to the CURRENT object.

Main uses:

1. Resolve name conflict between field and parameter

```java
class Dog {
    String name;   // field

    Dog(String name) {        // parameter also called name!
        this.name = name;     // this.name = field, name = parameter
    }
}
```

Without `this`, Java would think both sides are the parameter.

2. Call another constructor from a constructor — `this()`

```java
Dog() {
    this("Unknown", 0, "Unknown");  // calls Dog(String, int, String)
}
```

`this()` must be the FIRST line in the constructor.

3. Pass current object to another method

```java
printDog(this);
```

Interview tip: `this` cannot be used in static methods — static methods don't belong to any object, so there is no "current object" to refer to.

## 5 — Object playground — build your own Dog object

```java
// Parameterized constructor called:
Dog("Bruno", 3, "Labrador")

Dog(String name, int age, String breed) {
    this.name  = name;    // this.name  = "Bruno"
    this.age   = age;     // this.age   = 3
    this.breed = breed;   // this.breed = "Labrador"
}

// Result — Dog object in memory:
// ┌─────────────────────┐
// │  name  = "Bruno"   │
// │  age   = 3          │
// │  breed = "Labrador" │
// └─────────────────────┘

Dog d = new Dog("Bruno", 3, "Labrador");
d.bark();  // Bruno says: Woof!
```

## 6 — Multiple objects from one class

Each object is independent.

```java
Dog dog1 = new Dog("Bruno", 3, "Labrador");
Dog dog2 = new Dog("Max", 5, "Poodle");
Dog dog3 = new Dog("Rocky", 2, "Husky");

// Object: dog1
// Memory address (reference): 0xF80101
// Fields stored on heap:
//  dog1.name  = "Bruno"
//  dog1.age   = 3
//  dog1.breed = "Labrador"

dog1.bark();
// Output: "Bruno says: Woof!"

// This object is completely INDEPENDENT from the other dogs.
// Changing dog1.name does NOT affect dog2 or dog3.
```

## 7 — Common mistakes

✗ Forgetting `new` keyword

```java
Dog d = Dog();   // compile error
```

Must use `new`:

```java
Dog d = new Dog();
```

✗ Constructor has a return type

```java
void Dog() {}
```

This is NOT a constructor. It becomes a regular method named `Dog`.
Constructor never has a return type.

✗ Using object before initialising

```java
Dog d;
d.bark();   // NullPointerException
```

Must do:

```java
Dog d = new Dog();
```

✗ `this()` not first line in constructor

```java
Dog() {
    name = "Bruno";
    this("Bruno", 3, "Labrador");  // compile error
}
```

`this("name", 0)` must be the very first statement in a constructor.

## Quiz interview

5. What is constructor chaining using `this()`?

- Calling a parent class constructor
- Calling another constructor of the SAME class from within a constructor ✅
- Creating a copy of an object
- Overriding a constructor

✅ Correct! `this()` inside a constructor calls another constructor of the same class. It must be the first line. It avoids code duplication across multiple constructors.
