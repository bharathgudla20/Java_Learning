# Java Phase 3 — Topic 5: Generics

## Why Generics Exist

Before Java 5, collections could hold ANY type mixed together — Strings, Integers, anything. Type mistakes were only caught at **runtime**, often causing crashes in production.

```java
// BEFORE generics — old, unsafe way (pre-Java 5)
List list = new ArrayList();   // no type specified!
list.add("Bharath");           // String added
list.add(25);                  // Integer added — no error!

String name = (String) list.get(0);  // manual casting needed
String oops = (String) list.get(1);  // CRASH! ClassCastException at RUNTIME
// 25 is an Integer, not a String — compiler didn't catch it!
```

```java
// AFTER generics — type-safe, modern way
List<String> list = new ArrayList<>();   // only Strings allowed!
list.add("Bharath");      // works
list.add(25);              // COMPILE ERROR! caught immediately

String name = list.get(0);  // no casting needed — compiler KNOWS it's String!
```

**Generics = type safety + no manual casting + errors caught at compile time instead of runtime.**

---

## Generic Classes — Creating Your Own

A generic class works with ANY type — you decide the type when you use it.

```java
// Generic class — T is a placeholder for "any type"
class Box<T> {
    private T content;

    void set(T content) { this.content = content; }
    T get() { return content; }
}

// Use it with ANY type:
Box<String> stringBox = new Box<>();
stringBox.set("Bharath");
String s = stringBox.get();   // no casting needed!

Box<Integer> intBox = new Box<>();
intBox.set(25);
int n = intBox.get();         // works for Integer too!
```

**T naming conventions:**
- `T` = Type
- `E` = Element
- `K` = Key
- `V` = Value
- `N` = Number
- `R` = Result

### Generic class with multiple types

```java
class Pair<A, B> {
    private A first;
    private B second;

    Pair(A first, B second) {
        this.first  = first;
        this.second = second;
    }

    A getFirst()  { return first; }
    B getSecond() { return second; }
}

// Usage — pair a String with an Integer:
Pair<String, Integer> studentMarks = new Pair<>("Bharath", 85);
System.out.println(studentMarks.getFirst());   // "Bharath"
System.out.println(studentMarks.getSecond());  // 85

// HashMap uses this exact pattern internally — Map<K, V>
```

---

## Generic Methods

You can make just ONE method generic, even in a non-generic class. The type parameter goes right before the return type.

```java
class Utility {
    // <T> before return type — declares this method is generic
    static <T> void printArray(T[] arr) {
        for (T item : arr) {
            System.out.println(item);
        }
    }

    static <T extends Comparable<T>> T max(T a, T b) {
        return (a.compareTo(b) > 0) ? a : b;
    }
}

// Usage — works with ANY array type:
Integer[] nums  = {1, 2, 3};
String[] names  = {"Bharath", "Ravi"};

Utility.printArray(nums);    // works!
Utility.printArray(names);   // also works! SAME method

System.out.println(Utility.max(10, 20));        // 20
System.out.println(Utility.max("apple", "zoo")); // zoo
```

---

## Bounded Type Parameters

Restrict T to only certain types using `extends`.

```java
// Without bound — T could be ANYTHING
class Box<T> {
    T value;
    // Can't call value.compareTo() — T might not support it!
}

// WITH bound — T must extend Number
class NumberBox<T extends Number> {
    T value;

    double getDoubleValue() {
        return value.doubleValue();  // works! Number guarantees this method
    }
}

NumberBox<Integer> intBox = new NumberBox<>();   // OK — Integer extends Number
NumberBox<Double> dblBox  = new NumberBox<>();   // OK — Double extends Number
NumberBox<String> strBox  = new NumberBox<>();   // COMPILE ERROR! String doesn't extend Number

// Bounded generic method
static <T extends Comparable<T>> T findMax(List<T> list) {
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) max = item;
    }
    return max;
}
```

> Note: `extends` is used for BOTH classes and interfaces in generic bounds — even though you'd normally use `implements` for interfaces. In generics, it's always `extends`.

---

## Wildcards — `?`, `? extends T`, `? super T`

### 1. Unbounded wildcard `?`

Use when you don't care what type, only need to read.

```java
static void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);   // can only treat as Object
    }
    // list.add("x");  COMPILE ERROR — can't add anything (except null)
}

printList(new ArrayList<String>());   // works
printList(new ArrayList<Integer>());  // works
```

### 2. Upper bound `? extends T` — Producer

```java
static double sumOfList(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) {       // can READ as Number safely
        sum += n.doubleValue();
    }
    return sum;
    // list.add(10);  COMPILE ERROR — can't ADD, type uncertain
}

sumOfList(List.of(1, 2, 3));      // Integer extends Number — works
sumOfList(List.of(1.5, 2.5));     // Double extends Number — works
```

### 3. Lower bound `? super T` — Consumer

```java
static void addNumbers(List<? super Integer> list) {
    list.add(10);    // can ADD Integer safely
    list.add(20);    // works — guaranteed to fit
    // Integer x = list.get(0);  might not compile cleanly
}

addNumbers(new ArrayList<Integer>());  // works
addNumbers(new ArrayList<Number>());   // works — Number is superclass of Integer
addNumbers(new ArrayList<Object>());   // works — Object is superclass of Integer
```

### PECS Rule — the one to remember for interviews

> **P**roducer **E**xtends, **C**onsumer **S**uper

- Reading (producing) FROM the list → use `extends`
- Writing (consuming) INTO the list → use `super`

---

## Type Erasure — What Happens at Runtime

Generics exist ONLY at **compile time**. After compilation, the JVM doesn't actually know about `<String>` or `<Integer>` — it's erased to raw types for backward compatibility with pre-Java 5 code.

```java
List<String>  stringList = new ArrayList<>();
List<Integer> intList    = new ArrayList<>();

System.out.println(stringList.getClass() == intList.getClass());
// OUTPUT: true!
// Both are just "ArrayList" at runtime — generic info is ERASED
```

**Consequences of type erasure:**

```java
// Cannot use instanceof with generic types
if (obj instanceof List<String>) { }   // COMPILE ERROR

// Cannot create generic arrays
List<String>[] array = new List<String>[10];   // NOT ALLOWED
T[] arr = new T[10];                            // COMPILE ERROR

// Cannot have static fields of generic type
// Cannot overload methods that differ only by generic type
```

> Interview answer: "Type erasure means generic type information exists only during compilation for type checking. At runtime, all generic types are erased to their raw form for backward compatibility with pre-Java 5 code."

---

## Common Interview Questions & Answers

**Q: Why were generics introduced in Java 5?**
To provide compile-time type safety and eliminate the need for manual casting. Before generics, collections held `Object`, requiring casts, and type mismatches were only caught at runtime via `ClassCastException`.

**Q: What is type erasure?**
Generic type information exists only at compile time for type checking. At runtime, all generics are erased — `List<String>` and `List<Integer>` both become plain `List`.

**Q: When should you use `? extends T`?**
When you only need to READ/get elements from the collection (Producer). You cannot safely add elements because the exact type is uncertain.

**Q: When should you use `? super T`?**
When you only need to WRITE/add elements to the collection (Consumer). Reading is restricted because the exact return type is uncertain.

**Q: Can you create a generic array like `new T[10]`?**
No — not allowed due to type erasure. Arrays need runtime type info, but generics don't have it. Use `List<T>` instead.

**Q: Can generic types be primitives like `int` or `double`?**
No. Generics only work with object types, never primitives. Use wrapper classes instead: `Box<Integer>` not `Box<int>`. This is why autoboxing exists.

---

## Tricky Code Snippets to Practice

```java
// Snippet 1 — type erasure proof
List<String> a = new ArrayList<>();
List<Integer> b = new ArrayList<>();
System.out.println(a.getClass() == b.getClass());   // true — both just ArrayList

// Snippet 2 — bounded type rejection
class NumberBox<T extends Number> { T value; }
NumberBox<String> box = new NumberBox<>();  // COMPILE ERROR — String not a Number

// Snippet 3 — wildcard read restriction
List<? extends Number> list = List.of(1, 2, 3);
Number n = list.get(0);   // OK — reading is fine
list.add(5);              // COMPILE ERROR — can't add with extends wildcard

// Snippet 4 — generic method usage
static <T extends Comparable<T>> T max(T a, T b) {
    return (a.compareTo(b) > 0) ? a : b;
}
max(10, 20);          // 20
max("apple", "zoo");  // zoo
```

---

## Summary — What to Remember

- Generics provide **compile-time** type safety — errors caught while writing code, not at runtime
- `Box<T>` — same class works for any type you specify when creating an instance
- Naming convention: `T`=Type, `E`=Element, `K`=Key, `V`=Value
- Generic methods: `<T>` goes before the return type
- Bounded types: `<T extends Number>` restricts T to Number or its subclasses
- **PECS**: Producer **Extends**, Consumer **Super**
- **Type erasure**: generics are erased at runtime — `List<String>` and `List<Integer>` are identical at runtime
- Cannot use `instanceof` with generic types, cannot create `new T[10]`
- Generics only work with object types — use wrapper classes for primitives

---

