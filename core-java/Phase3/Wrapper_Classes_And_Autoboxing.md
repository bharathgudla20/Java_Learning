# Java Phase 3 — Topic 7: Wrapper Classes & Autoboxing

## Why Wrapper Classes Exist

Java has two kinds of data:
- Primitive types: `int`, `double`, `char`, `boolean`
- Objects: `Integer`, `Double`, `Character`, `Boolean`

Primitives are fast and use stack memory.

But many Java features only work with objects:
- Generics
- Collections like `ArrayList`, `HashMap`
- Frameworks and APIs

So Java provides wrapper classes to treat primitives as objects when needed.

---

## 1. Wrapper Classes

Each primitive has a matching wrapper class:

| Primitive | Wrapper Class |
|---|---|
| `int` | `Integer` |
| `double` | `Double` |
| `char` | `Character` |
| `boolean` | `Boolean` |
| `byte` | `Byte` |
| `short` | `Short` |
| `long` | `Long` |
| `float` | `Float` |

### Example

```java
int x = 10;
Integer y = Integer.valueOf(x);   // wrapping primitive into object
```

---

## 2. Autoboxing

Autoboxing means Java automatically converts a primitive into its wrapper class.

```java
List<Integer> numbers = new ArrayList<>();
numbers.add(5);   // autoboxing
```

Behind the scenes, Java does this:

```java
numbers.add(Integer.valueOf(5));
```

---

## 3. Unboxing

Unboxing means Java automatically converts a wrapper object back to a primitive.

```java
Integer objNum = Integer.valueOf(10);
int num = objNum;   // unboxing
```

Behind the scenes, Java does this:

```java
int num = objNum.intValue();
```

---

## 4. Why This Is Useful

Wrapper classes allow primitives to be used in places that require objects.

```java
ArrayList<Integer> list = new ArrayList<>();
list.add(100);
list.add(200);
```

Without wrappers, this would not work because `ArrayList` stores objects, not primitives.

---

## 5. Common Interview Trap 1: NullPointerException

Primitives cannot be `null`.

But wrappers are objects, so they can be `null`.

```java
Integer age = null;
int x = age;   // throws NullPointerException
```

### Why?

Because unboxing tries to call `.intValue()` on a `null` object.

### Safe approach

```java
Integer age = null;
if (age != null) {
    int x = age;
}
```

---

## 6. Common Interview Trap 2: `==` vs `.equals()`

This is one of the most asked interview questions.

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);   // true
```

```java
Integer x = 200;
Integer y = 200;
System.out.println(x == y);   // false
```

### Why?

Java uses an internal cache for small integers.

The default cache range is:

```java
-128 to 127
```

So for values inside this range, Java may reuse the same object reference.

For values outside the range, new objects are created.

### Correct way to compare wrapper values

```java
Integer a = 100;
Integer b = 100;
System.out.println(a.equals(b));   // true
```

> `==` compares references, not values.
> `.equals()` compares the actual contents.

---

## 7. Integer Cache Explained

Java caches frequently used wrapper objects to improve performance and reduce memory usage.

This is especially true for `Integer`.

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);   // true
```

The reason is that both variables point to the same cached object.

### Important point

```java
Integer x = 200;
Integer y = 200;
System.out.println(x == y);   // false
```

Because `200` is outside the cache range, Java creates separate objects.

---

## 8. Performance Considerations

Wrapper classes are useful, but they can be slower than primitives.

### Why?

Every boxing and unboxing operation may involve:
- object creation
- memory allocation
- garbage collection work

### Best practice

Use primitives in performance-critical code such as:
- loops
- mathematical calculations
- large data processing

Use wrappers when working with:
- collections
- generics
- APIs that require objects

---

## 9. Important Interview Answers

### Q1: What is the Integer Cache?

Java caches small `Integer` objects to save memory and improve performance. The default range is `-128` to `127`.

### Q2: How does `NullPointerException` happen in unboxing?

It happens when a wrapper object is `null` and Java tries to convert it to a primitive automatically.

### Q3: Why avoid wrappers in heavy loops?

Because boxing and unboxing create extra objects, increasing memory pressure and garbage collection overhead.

### Q4: Is `==` safe for wrapper classes?

No. `==` compares references, not values. Use `.equals()` for value comparison.

---

## 10. Quick Summary

- Wrapper classes let primitives behave like objects.
- Autoboxing converts primitive → wrapper automatically.
- Unboxing converts wrapper → primitive automatically.
- Wrappers can be `null`, primitives cannot.
- `==` is unsafe for wrapper objects; use `.equals()`.
- Java caches small integers from `-128` to `127`.

---

## Mini Example

```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>();
        nums.add(10);   // autoboxing

        Integer a = nums.get(0);
        int b = a;      // unboxing

        System.out.println(b); // 10
    }
}
```

This is the core idea behind wrapper classes and autoboxing in Java.
