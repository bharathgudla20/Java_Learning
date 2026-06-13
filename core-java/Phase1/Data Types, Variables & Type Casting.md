# Java Phase 1 — Topic 2: Data Types, Variables & Type Casting

## The 8 Primitive Data Types
Java has exactly 8 primitive types — these are the building blocks of all data.

| Type | Size | Range | Default Value | Use For |
|------|------|-------|---------------|---------|
| byte | 1 byte | -128 to 127 | 0 | Small numbers, file/stream data |
| short | 2 bytes | -32,768 to 32,767 | 0 | Rarely used |
| int | 4 bytes | -2.1 billion to 2.1 billion | 0 | Default for whole numbers |
| long | 8 bytes | -9.2 quintillion to 9.2 quintillion | 0L | Large whole numbers |
| float | 4 bytes | ~7 decimal digits | 0.0f | Decimals (less precise) |
| double | 8 bytes | ~15 decimal digits | 0.0d | Default for decimals |
| char | 2 bytes | Single Unicode character | '\u0000' | Single characters |
| boolean | 1 bit | true or false only | false | Flags, conditions |

## Key Suffixes to Remember
```java
long l = 100L;      // L suffix required for long literals
float f = 3.14f;    // f suffix required for float literals
double d = 9.99;    // no suffix needed — double is the default decimal type
char c = 'A';       // single quotes, not double
```

## Primitive vs Reference Types

### Primitive Types
- Store the actual value directly in memory
- The 8 types listed above
- Cannot be null — this is a compile error: `int x = null;`
- Stored on the stack

```java
int x = 5;   // the memory box literally contains the value 5
int y = x;   // y gets a COPY of 5 — changing y won't affect x
```

### Reference Types
- Store a memory address pointing to where the object lives
- Everything else: `String`, arrays, objects, your own classes
- Can be null — `String s = null;` is valid
- Object is stored on the heap, the reference on the stack

```java
String s = "hello";   // s holds an address pointing to "hello" in heap
String t = s;          // t and s point to the SAME object
```

Interview tip: This is why `==` compares addresses for objects, not values.

Always use `.equals()` to compare `String` content.

```java
String a = new String("hi");
String b = new String("hi");
System.out.println(a == b);       // false — different addresses
System.out.println(a.equals(b));  // true  — same content
```

## Variables

### Declaring and Initializing
```java
int age;          // declaration only — not yet usable
int age = 25;     // declaration + initialization — ready to use
```

### Naming Rules
- Must start with a letter, `$`, or `_` (not a number)
- Case-sensitive — `age` and `Age` are different variables
- Cannot use Java keywords (`int`, `class`, `static`, etc.)
- Convention: use camelCase — `firstName`, `totalAmount`

### Types of Variables
```java
class Example {
    int instanceVar = 10;       // instance variable — belongs to object, has default value
    static int classVar = 20;   // class/static variable — shared across all objects

    void method() {
        int localVar = 30;      // local variable — must initialize before use, no default
    }
}
```

## Type Casting
Converting a value from one data type to another.

### 1. Widening Casting (Automatic)
Small type → Big type. Java does this automatically — no extra syntax needed.

`byte → short → int → long → float → double`

```java
int i = 100;
double d = i;       // automatic — d is now 100.0
long l = i;         // automatic — l is now 100

System.out.println(d);  // 100.0
```

- Safe — no data loss
- Java handles it silently

### 2. Narrowing Casting (Manual)
Big type → Small type. You must explicitly cast using `(targetType)`.

```java
double d = 9.99;
int i = (int) d;    // manual cast — i is now 9 (decimal is DROPPED, not rounded)

double big = 130.5;
byte b = (byte) big;   // overflow! b = -126 (wraps around)
```

- Risky — may lose data
- Truncates (chops), does NOT round
- Can cause overflow if value is out of range

### Casting Quick Reference
```java
// widening — automatic
int   → long   : long l = someInt;
int   → double : double d = someInt;
float → double : double d = someFloat;

// narrowing — manual
double → int   : int i = (int) someDouble;    // drops decimal
long   → int   : int i = (int) someLong;      // may overflow
double → float : float f = (float) someDouble;
```

## Common Interview Questions & Answers

**Q: What is the difference between `int` and `Integer`?**

- `int` is a primitive — stores value directly, cannot be null.
- `Integer` is a wrapper class (reference type) — can be null, has utility methods like `Integer.parseInt()`. Java auto-converts between them (autoboxing/unboxing).

**Q: What happens when you cast `double 9.99` to `int`?**

- You get `9`. Casting truncates the decimal — it does NOT round up.

**Q: Can a boolean store any value other than true/false?**

- No. Unlike C/C++, Java `boolean` strictly accepts only `true` or `false`.

**Q: What is the default value of an `int` instance variable?**

- `0`. Local variables have no default — you must initialize them before use or the compiler throws an error.

**Q: Why does `float f = 3.14;` cause a compile error?**

- Because `3.14` is a `double` literal by default. You need `float f = 3.14f;` (with the `f` suffix) or `float f = (float) 3.14;`.

## Tricky Code Snippets to Practice
```java
// Snippet 1 — what is the output?
int x = 5;
int y = x;
y = 10;
System.out.println(x);   // 5 — primitives are copied by value

// Snippet 2 — overflow
byte b = 127;
b++;
System.out.println(b);   // -128 — wraps around (overflow)

// Snippet 3 — integer division
int a = 7, c = 2;
double result = a / c;
System.out.println(result);   // 3.0 NOT 3.5 — division happens as int first!
// Fix: double result = (double) a / c;  → 3.5

// Snippet 4 — char arithmetic
char ch = 'A';
ch++;
System.out.println(ch);   // B — char is internally a number (Unicode)

// Snippet 5 — long literal missing L
long big = 9999999999;    // compile error! needs L
long big = 9999999999L;   // correct
```

## Summary — What to Remember
- Java has 8 primitives: `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`
- `int` is default for whole numbers, `double` is default for decimals
- Primitives store values; reference types store addresses
- Primitives cannot be null; reference types can
- Widening (small → big) = automatic; Narrowing (big → small) = manual cast
- Casting to `int` truncates, does not round
- Always add `L` for long literals, `f` for float literals
- Local variables have no default value — must initialize before use