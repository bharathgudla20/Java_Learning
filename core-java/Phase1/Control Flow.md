# Control Flow

## if / else if / else
```java
if (score >= 90) grade = "A";
else if (score >= 75) grade = "B";
else grade = "C";
```

## switch (use for fixed values, not ranges)
```java
switch (day) {
    case "MON": System.out.println("Monday"); break;
    default:    System.out.println("Other");
}
```

## Java 14+ arrow switch (no break needed)
```java
switch (day) {
    case "MON" -> System.out.println("Monday");
    default    -> System.out.println("Other");
}
```

## Loops
```java
for (int i = 0; i < 5; i++) { }          // when count is known
while (condition) { }                      // when count is unknown
do { } while (condition);                  // runs at least once

## Enhanced for loop:
    The enhanced for loop in Java (also known as the for-each loop) is a compact syntax introduced in Java 5 used to iterate sequentially through arrays or collections.
for (String s : list) { }                 // enhanced for — iterating collections
```

## break vs continue
```java
break;     // exits the loop entirely
continue;  // skips current iteration, moves to next
```

> Interview: `do-while` always executes body at least once. `switch` without `break` falls through to next case.
