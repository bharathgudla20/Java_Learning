# Phase 2 Topic 8 — Object Class Methods

## Overview

Every class in Java inherits from the `Object` class. It provides essential methods that every object should implement properly:
1. `toString()` — convert object to string
2. `equals()` — compare two objects
3. `hashCode()` — get unique ID for the object
4. `clone()` — create a copy of an object

These methods are fundamental to OOP and frequently appear in interviews.

---

## 1. toString()

The `toString()` method turns an object into a text string. By default, it prints out the class name and a weird code (the memory address).

You should **override** this method so it prints real, useful information about your object instead.

### Default Behavior (Without Override)
```java
User user = new User("Alice", 25);
System.out.println(user);  // Output: User@15db9742 (class name + memory address)
```

### Override for Better Output
```java
class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

// Usage:
User user = new User("Alice", 25);
System.out.println(user);  // Output: User{name='Alice', age=25}
```

### When to Use
- Debugging and logging
- Displaying object information to users
- String concatenation with objects

---

## 2. equals()

The `equals()` method checks if two objects are the same. By default, it only checks if they point to the exact same spot in memory (using `==`).

You override it to check if the **data inside** the objects matches.

### Default Behavior (Without Override)
```java
User user1 = new User("Alice", 25);
User user2 = new User("Alice", 25);
System.out.println(user1.equals(user2));  // Output: false (different objects in memory)
System.out.println(user1 == user2);       // Output: false (same as equals by default)
```

### Override to Compare Data
```java
class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        // Check if comparing to same object
        if (this == obj) return true;
        
        // Check if obj is null or different class
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // Cast and compare fields
        User user = (User) obj;
        return age == user.age && name.equals(user.name);
    }
}

// Usage:
User user1 = new User("Alice", 25);
User user2 = new User("Alice", 25);
System.out.println(user1.equals(user2));  // Output: true (same data)
```

### Best Practices for equals()
1. Check `this == obj` first (optimization)
2. Check `obj == null` 
3. Check class type with `getClass()` (not `instanceof` — avoids issues with subclasses)
4. Cast the object
5. Compare all relevant fields

---

## 3. hashCode()

The `hashCode()` method gives your object a unique ID number. Java uses this number to organize objects quickly in collections like `HashSet` or `HashMap`.

### The Golden Rule
> If two objects are equal according to `equals()`, they **must** return the same number for `hashCode()`.

**Important:** If you override `equals()`, you **MUST** override `hashCode()` too!

### Default Behavior
```java
User user1 = new User("Alice", 25);
User user2 = new User("Alice", 25);
System.out.println(user1.hashCode());  // e.g., 1234567
System.out.println(user2.hashCode());  // e.g., 9876543 (different!)
```

### Override Using Objects.hash()
```java
class User {
    private String name;
    private int age;

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return age == user.age && name.equals(user.name);
    }
}

// Usage:
User user1 = new User("Alice", 25);
User user2 = new User("Alice", 25);
System.out.println(user1.hashCode());   // same hash code
System.out.println(user2.hashCode());   // same hash code
System.out.println(user1.equals(user2)); // true

Set<User> set = new HashSet<>();
set.add(user1);
set.add(user2);
System.out.println(set.size());  // Output: 1 (duplicates removed correctly)
```

### Why hashCode() Matters
- **HashMap/HashSet Performance:** These collections use `hashCode()` to bucket objects
- **Equality Contract:** Breaking the contract leads to bugs — duplicates in sets, lost values in maps

### Best Practices
1. Include all fields used in `equals()`
2. Use `Objects.hash()` for simplicity
3. Override both `equals()` and `hashCode()` together
4. Never change `hashCode()` value while object is in a hash-based collection

---

## 4. clone()

The `clone()` method makes a copy of your object. To use it, your class must state that it allows copying by using `implements Cloneable`.

### Shallow Copy vs Deep Copy

**Shallow Copy** (Default):
```
Original: name="Alice", address=@9876
Clone:    name="Alice", address=@9876  (same address object!)
```

**Deep Copy** (Explicit):
```
Original: name="Alice", address=@9876
Clone:    name="Alice", address=@1234  (different address object)
```

### Shallow Copy (Default)
```java
class User implements Cloneable {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow copy
    }
}

// Usage:
User original = new User("Alice", 25);
User copy = (User) original.clone();
System.out.println(copy.name);  // Alice (copy of data)
```

### Deep Copy (For Complex Objects)
```java
class Address implements Cloneable {
    private String city;
    private String country;

    public Address(String city, String country) {
        this.city = city;
        this.country = country;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class User implements Cloneable {
    private String name;
    private int age;
    private Address address;

    public User(String name, int age, Address address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        User cloned = (User) super.clone();
        // Deep copy the address object
        cloned.address = (Address) address.clone();
        return cloned;
    }
}

// Usage:
Address addr = new Address("NYC", "USA");
User original = new User("Alice", 25, addr);
User deepCopy = (User) original.clone();

// Modifying original's address won't affect deepCopy's address
```

### When to Use clone()
- Creating independent copies of objects
- Implementing copy constructors
- Backup/undo functionality

### Alternatives to clone()
- **Copy Constructor:**
  ```java
  public User(User other) {
      this.name = other.name;
      this.age = other.age;
  }
  ```
- **Builder Pattern** (modern approach)
- **Copy Factory Methods**

---

## Interview Q&A

### Q1: What's the difference between `==` and `equals()`?

**Your Answer:**

"`==` compares object references — it checks if two variables point to the exact same memory location. `equals()` compares object content — it checks if two objects have the same data.

```java
String s1 = new String("Hello");
String s2 = new String("Hello");
System.out.println(s1 == s2);      // false (different objects in memory)
System.out.println(s1.equals(s2)); // true (same content)
```"

---

### Q2: Why must you override hashCode() if you override equals()?

**Your Answer:**

"This is the **hashCode/equals contract**. If two objects are equal according to `equals()`, they must return the same `hashCode()`. If you violate this, collections like `HashSet` and `HashMap` will malfunction — duplicates won't be detected, and you'll lose values in maps.

Example of the problem:
```java
HashSet<User> set = new HashSet<>();
set.add(user1);
set.add(user2);  // user1 and user2 are equal, but have different hashCodes
System.out.println(set.size());  // Wrong! Should be 1, but might be 2
```"

---

### Q3: Explain Shallow Copy vs Deep Copy.

**Your Answer:**

"**Shallow Copy** copies only the main object. If the object contains references to other objects, those references are copied — not the objects themselves. Both the original and copy point to the same inner objects.

**Deep Copy** recursively copies everything, including inner objects. The copy is completely independent.

```java
// Shallow copy problem:
User original = new User("Alice", new Address("NYC", "USA"));
User shallow = (User) original.clone();
// Both original and shallow now point to the same Address object!
shallow.address.city = "LA";  // This changes original too!

// Deep copy solution:
// Override clone() to also clone the address
```"

---

### Q4: Why is clone() considered problematic? What are alternatives?

**Your Answer:**

"The `clone()` method has several issues:
1. Awkward to use — must implement `Cloneable` and handle `CloneNotSupportedException`
2. Shallow copy by default — easy to make mistakes
3. Not well-integrated with generics
4. Method is protected, not intuitive

**Better alternatives:**
- **Copy Constructor:** `new User(original)` — clear and explicit
- **Copy Factory:** `User.copy(original)` — more flexible
- **Builder Pattern:** Fluent, modern approach

```java
// Copy constructor (clean and clear)
public User(User other) {
    this.name = other.name;
    this.age = other.age;
    this.address = new Address(other.address);  // deep copy
}

// Usage:
User copy = new User(original);
```"

---

## Quick Reference

| Method | Purpose | Must Override? | Key Rule |
|--------|---------|---|----------|
| `toString()` | Convert to string | ❌ Optional | For debugging/logging |
| `equals()` | Compare objects | ⚠️ If custom logic | Override with `hashCode()` |
| `hashCode()` | Get unique ID | ⚠️ If override `equals()` | Must match `equals()` contract |
| `clone()` | Create copy | ❌ Optional | Implement `Cloneable` first |

---

## Best Practices Checklist

✅ Always override `toString()` for better debugging
✅ Override `equals()` to compare object data
✅ **ALWAYS** override `hashCode()` when you override `equals()`
✅ Use `Objects.hash()` for simplicity
✅ Prefer copy constructors over `clone()`
✅ Implement deep copy when objects contain other objects
✅ Test with collections (`HashSet`, `HashMap`) to verify correctness

---

## Summary Table

| Scenario | Solution |
|----------|----------|
| Need custom display | Override `toString()` |
| Compare object data | Override `equals()` + `hashCode()` |
| Store in HashSet/HashMap | Override `equals()` + `hashCode()` |
| Need object copy | Use copy constructor or builder |
| Shallow copy needed | `implements Cloneable` + `clone()` |
| Deep copy needed | Override `clone()` and clone inner objects |
