# Phase 3, Topic 5: Generics

## Part 1: Life Before Generics

Without generics, a class can only hold `Object`, which is too flexible and unsafe.

```java
class Glass {
    private Object liquid;

    public void pourIn(Object liquid) {
        this.liquid = liquid;
    }

    public Object drinkOut() {
        return liquid;
    }
}
```

### Problem
A developer may pour `Juice` into a glass that is meant for `Water`, and the program will fail at runtime.

```java
Glass waterGlass = new Glass();
waterGlass.pourIn(new Water());
Water myWater = (Water) waterGlass.drinkOut();
```

This causes a `ClassCastException` if the wrong object is inserted.

---

## Part 2: Life After Generics

Generics make code type-safe and avoid manual casting.

```java
class GenericGlass<T> {
    private T liquid;

    public void pourIn(T liquid) {
        this.liquid = liquid;
    }

    public T drinkOut() {
        return liquid;
    }
}
```

### Example

```java
GenericGlass<Water> waterGlass = new GenericGlass<>();
waterGlass.pourIn(new Water());
Water myWater = waterGlass.drinkOut();
```

Now the compiler ensures that only `Water` can be stored in that glass.

---

## Part 3: Bounded Types

Sometimes you want to restrict the type parameter to a family of classes.

```java
class RestrictedGlass<T extends Liquid> {
    private T item;
}
```

This means `T` must be a subclass of `Liquid`.

### Example

```java
RestrictedGlass<Water> g1 = new RestrictedGlass<>();
RestrictedGlass<Juice> g2 = new RestrictedGlass<>();
```

But this would be invalid:

```java
RestrictedGlass<String> g3 = new RestrictedGlass<>();
```

---

## Part 4: Wildcards

Wildcards are used when a method should accept unknown generic types.

```java
public static void printList(List<?> list) {
    for (Object element : list) {
        System.out.println(element);
    }
}
```

### Upper Bound Wildcard

```java
public static double sumList(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) {
        sum += n.doubleValue();
    }
    return sum;
}
```

This accepts `List<Integer>`, `List<Double>`, etc.

### Lower Bound Wildcard

```java
public static void addInteger(List<? super Integer> list) {
    list.add(10);
}
```

This accepts a list of `Integer`, `Number`, or `Object`.

---

## Part 5: Type Erasure

Java generics use type erasure.

- The compiler checks generic types during compilation.
- The bytecode removes the generic type information.
- The runtime uses raw types internally.

### Example

```java
Box<String> box = new Box<>();
```

At runtime, it becomes something like:

```java
Box box = new Box();
```

This is why generics are mainly a compile-time safety feature.

---

## Quick Summary

- Generics make code safer and cleaner.
- They prevent runtime `ClassCastException`.
- `T` is a type parameter.
- `T extends SomeClass` restricts the allowed types.
- `?` is used for unknown types in wildcards.
- Java uses type erasure under the hood.
