# Phase 3, Topic 3: Collections — Set & Map

## Part 1: Map vs. Set (The Simple Concept)

### 1. What is a Map?
A `Map` stores data in key-value pairs. It is like an employee directory where each employee ID is unique and used to find the corresponding details.

- Keys must be unique.
- Values can be duplicated.

### 2. What is a Set?
A `Set` stores unique elements only. It is like a guest list where a name can appear only once.

- A `HashSet` internally uses a `HashMap` to store its elements.

---

## Part 2: The Core Flavors (Hash vs. LinkedHash vs. Tree)

| Implementation | Internal Mechanism | Ordering | Time Complexity (Search/Insert) |
|---|---|---|---|
| `HashSet` / `HashMap` | Hash Table | No guaranteed order | $O(1)$ average |
| `LinkedHashSet` / `LinkedHashMap` | Hash Table + Doubly Linked List | Insertion order | $O(1)$ average |
| `TreeSet` / `TreeMap` | Red-Black Tree | Sorted order | $O(\log n)$ |

---

## Part 3: How `HashMap` Works Internally

When you call `map.put(key, value)`, Java performs the following steps:

1. It creates a bucket array internally.
2. It calculates the hash code of the key using `hashCode()`.
3. It finds the correct bucket index using:

$$
\text{Index} = \text{hash} \& (n - 1)
$$

where $n$ is the current capacity.

4. The entry is stored in the bucket, usually as a linked list or tree node.

### Hash Collision
A collision happens when two different keys produce the same bucket index.

- Before Java 8, collisions were handled using linked lists.
- In Java 8+, if a bucket becomes too large, it is converted into a Red-Black Tree for faster lookup.

---

## Part 4: Code Demo

```java
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapMastery {
    public static void main(String[] args) {
        Map<String, String> currentProject = new HashMap<>();
        currentProject.put("EMP102", "Bharath");
        currentProject.put("EMP101", "Amit");

        System.out.println("HashMap Output (Unordered): " + currentProject);

        Map<String, String> sortedProject = new TreeMap<>();
        sortedProject.put("EMP102", "Bharath");
        sortedProject.put("EMP101", "Amit");

        System.out.println("TreeMap Output (Sorted by Key): " + sortedProject);
    }
}
```

---

## Part 5: Interview Questions

### Q1: How does `map.get(key)` work internally?
Answer:

1. If the key is `null`, Java checks bucket 0.
2. Otherwise, it computes the hash code and bucket index.
3. It goes to that bucket and searches the entries.
4. It compares keys using `==` and `equals()`.

### Q2: What is the load factor in a `HashMap`?
The default load factor is `0.75`. When the number of entries exceeds 75% of the capacity, the map is rehashed and resized.

### Q3: What happens if a mutable object is used as a key?
This is dangerous because changing the object's state may change its `hashCode()`. Then the map may fail to find the key later.

### Q4: What is the difference between `HashMap` and `ConcurrentHashMap`?
`HashMap` is not thread-safe, while `ConcurrentHashMap` is thread-safe and allows better concurrent access by locking only specific buckets or nodes.

---

## Quick Summary

- Use `HashMap` for fast access with no ordering.
- Use `LinkedHashMap` when insertion order matters.
- Use `TreeMap` when you want sorted keys.
- Use `Set` when you only need unique values.
