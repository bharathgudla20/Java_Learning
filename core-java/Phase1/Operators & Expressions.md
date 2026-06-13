# Topic 3 — Operators & Expressions

## Arithmetic Operators
`+ - * / %`

```java
7 / 2    // 3  (int division — drops decimal)
7 % 2    // 1  (remainder — use for even/odd check)
(double) 7 / 2  // 3.5 (cast first to get decimal)
```

## Relational Operators
`== != > < >= <=`
- Always return `boolean`

## Logical Operators
- `&&` (AND)
- `||` (OR)
- `!` (NOT)

## Short-circuit Evaluation
- `&&` stops if left is false.
- `||` stops if left is true.

```java
if (s != null && s.length() > 0)  // safe — won't crash if s is null
```

## Pre vs Post Increment
```java
int x = 5;
System.out.println(x++);  // 5 (use then increment)
System.out.println(++x);  // 7 (increment then use)
```

## Ternary Operator
```java
String result = (age >= 18) ? "Adult" : "Minor";
```

## String Concatenation Trap
```java
1 + 2 + "3"   // "33"  (1+2=3 first, then 3+"3")
"1" + 2 + 3   // "123" (string concat from left)
```

> Interview: `&` evaluates both sides always. `&&` short-circuits.
