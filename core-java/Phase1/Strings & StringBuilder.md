# Strings & StringBuilder

## 1 — What is a String?
A sequence of characters — and it is IMMUTABLE.

A String is like a name tag that's been laminated — you can read it, but you cannot change the letters on it. If you want a different name, you have to make a brand new name tag. Every "modification" actually creates a new String object.

```java
String name = "Bharath";

// This looks like it changes name — it does NOT
name.toUpperCase();
System.out.println(name);   // still "Bharath"!

// You must REASSIGN to capture the new String
name = name.toUpperCase();
System.out.println(name);   // "BHARATH" ✅
```

Every String method returns a NEW String. The original is untouched. Always reassign: `s = s.trim();` not just `s.trim();`

---

## 2 — String pool (most asked interview topic)
Why `==` fails for Strings

Java has a special memory area called the String Pool (inside the heap). When you create a String literal, Java first checks if that value already exists in the pool. If yes, it reuses the same object. If you use `new String()`, Java always creates a fresh object in the heap — bypassing the pool.

```java
String a = "hello";                // goes to String pool
String b = "hello";                // reuses same pool object
String c = new String("hello");    // NEW object in heap, NOT pool

System.out.println(a == b);        // true  — same pool object
System.out.println(a == c);        // false — c is a different heap object
System.out.println(a.equals(c));   // true  — same content
```

Rule: Always use `.equals()` to compare String content. Use `==` only to compare if two variables point to the EXACT same object.

Interview answer: `==` compares references. `.equals()` compares character content.

---

## 3 — Must-know String methods playground
Common methods: `.length()`, `.charAt(i)`, `.substring()`, `.indexOf()`, `.contains()`, `.trim()`, `.replace()`, `.split()`, `.equals()`, `.toUpperCase()`

```java
String s = "  Hello Bharath  ";
// Extracts a part of the string. End index is EXCLUSIVE.
s.substring(0, 5); // "  Hel"   // index 0 to 4
s.substring(5);    // "lo Bharath  "   // from index 5 to end
```

Remember: end index is EXCLUSIVE — `charAt(end)` is NOT included.

---

## 4 — String vs StringBuilder
String — immutable, slow in loops. Every `+` creates a NEW object. In a loop doing many concatenations, you create many temporary objects.

StringBuilder — mutable, fast in loops. Modifies the SAME internal buffer. Use it for building strings inside loops.

```java
StringBuilder sb = new StringBuilder();

sb.append("Hello");        // "Hello"
sb.append(" ");            // "Hello "
sb.append("Bharath");      // "Hello Bharath"
sb.insert(5, ",");         // "Hello, Bharath"
sb.replace(7, 14, "World"); // "Hello, World"
sb.delete(5, 6);           // "Hello World"
sb.reverse();              // "dlroW olleH"
sb.toString();             // convert back to String

System.out.println(sb.length());   // current length
```

StringBuilder builds strings without creating new objects each time.

---

## 5 — Converting between types
```java
// int → String
String s1 = String.valueOf(42);        // "42"
String s2 = Integer.toString(42);      // "42"
String s3 = "" + 42;                 // "42" (quick but not best practice)

// String → int
int n1 = Integer.parseInt("42");     // 42
int n2 = Integer.valueOf("42");      // 42 (returns Integer, auto-unboxed)

// double → String and back
String s4 = String.valueOf(3.14);      // "3.14"
double d  = Double.parseDouble("3.14"); // 3.14
```

Careful: `Integer.parseInt("abc")` throws `NumberFormatException` — wrap parses in try-catch when input comes from users.

---

## 6 — Common interview patterns
Pick a pattern to see the code (examples): reverse a string, check palindrome, count vowels, check anagram, remove duplicates, convert String to `char[]`.

Example: remove duplicates while preserving order

```java
String s = "Bharath";
StringBuilder result = new StringBuilder();
for (char c : s.toCharArray()) {
    if (result.indexOf(String.valueOf(c)) == -1) {
        result.append(c);
    }
}
System.out.println(result);   // "Bhrat"
```

In Java, both String.valueOf() and toString() convert objects or data into strings, but they handle null values differently.

The primary rule is: Use String.valueOf() when your data might be null and you want to avoid crashes. Use toString() when you are 100% certain the data exists.The Main DifferenceString.valueOf(object) is a safe utility method. If the object is null, it smoothly returns the text "null" instead of crashing.object.toString() is a method called directly on an object. If that object is null, your program will instantly crash with a NullPointerException.

can we use to int and float date type as toString?
No, you cannot call .toString() directly on basic int or float data types.In Java, int and float are primitive data types, not objects. Only objects have methods you can call with a dot (.).

However, you have a couple of easy ways to convert them to text.Option 1: Use Wrapper Classes (Best Practice)Java has special object versions of primitives called Wrapper Classes (Integer and Float). These classes have built-in tools to convert your numbers.For integers: Use Integer.toString(myInt)For floats: Use Float.toString(myFloat)javaint age = 25;
float price = 19.99f;

String ageText = Integer.toString(age);  // Works! Gives "25"
String priceText = Float.toString(price);