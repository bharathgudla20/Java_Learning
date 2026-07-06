# Java Phase 3 — Topic 8: Enum

## What is an Enum?

A special class with a fixed set of named constants.

Think of an enum like a traffic light — it can only be RED, YELLOW, or GREEN. Nothing else.

Enums are used when a variable should only ever hold one of a small, fixed set of values.

### Without enum

```java
static final int MON = 1, TUE = 2, WED = 3;

void scheduleTask(int day) {
    if (day == 99) {
        System.out.println("Unexpected day");
    }
}
```

This is unsafe because someone can pass `99`.

### With enum

```java
enum Day {
    MON, TUE, WED, THU, FRI, SAT, SUN
}

void scheduleTask(Day day) {
    System.out.println("Scheduling for: " + day);
}
```

Now only valid `Day` values are allowed.

---

## Why Enums are Useful

Enums provide:

- Type safety
- Readable names
- A fixed set of valid values
- Better code clarity

---

## Basic Enum

```java
enum Day {
    MON, TUE, WED, THU, FRI, SAT, SUN
}
```

### Built-in methods

```java
Day.MON.name();        // "MON"
Day.MON.ordinal();     // 0
Day.values();          // array of all constants
Day.valueOf("MON");   // Day.MON
```

### Comparison

```java
Day today = Day.MON;

if (today == Day.MON) {
    System.out.println("It's Monday");
}
```

Use `==` for enums.

---

## Enum with Fields and Methods

```java
enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6),
    MARS(6.421e+23, 3.3972e6);

    private final double mass;
    private final double radius;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }
}
```

---

## Enum with Switch

```java
enum Season { SPRING, SUMMER, MONSOON, WINTER }

Season current = Season.MONSOON;

switch (current) {
    case SPRING:
        System.out.println("Flowers blooming!");
        break;
    case SUMMER:
        System.out.println("Hot and sunny!");
        break;
    case MONSOON:
        System.out.println("Carry an umbrella!");
        break;
    case WINTER:
        System.out.println("Wear a jacket!");
        break;
}
```

---

## Enum with Abstract Methods

```java
enum Operation {
    ADD {
        @Override
        public int apply(int a, int b) { return a + b; }
    },
    SUBTRACT {
        @Override
        public int apply(int a, int b) { return a - b; }
    },
    MULTIPLY {
        @Override
        public int apply(int a, int b) { return a * b; }
    },
    DIVIDE {
        @Override
        public int apply(int a, int b) { return a / b; }
    };

    public abstract int apply(int a, int b);
}
```

---

## EnumSet and EnumMap

```java
import java.util.EnumSet;
import java.util.EnumMap;

enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

EnumSet<Day> weekdays = EnumSet.of(Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI);
EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
```

These are more efficient than `HashSet` and `HashMap` when used with enums.

---

## Enum Implementing an Interface

```java
interface Describable {
    String getDescription();
}

enum Status implements Describable {
    PENDING {
        @Override
        public String getDescription() { return "Waiting to be processed"; }
    },
    ACTIVE {
        @Override
        public String getDescription() { return "Currently being processed"; }
    },
    COMPLETED {
        @Override
        public String getDescription() { return "Successfully finished"; }
    },
    FAILED {
        @Override
        public String getDescription() { return "Encountered an error"; }
    }
}
```

---

## Summary

- An enum is a fixed set of named constants.
- It provides type safety.
- It is better than using raw integer constants.
- Enums can have constructors, methods, and fields.
- They work well with `switch`, `EnumSet`, and `EnumMap`.
