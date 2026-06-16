# Phase 3 Topic — Collections Framework: List (ArrayList vs LinkedList)

## Part 1: What is the List Interface? (In Simple Terms)

Think of a `List` as an ordered assembly line. It keeps items in the exact sequence you add them (insertion order), allows duplicates, and each item has an index starting at `0`.

Two main implementations you should know: `ArrayList` and `LinkedList`.

### ArrayList (The Dynamic Array)

- Memory layout: contiguous array.
- Growth: default capacity starts at 10; when full, it grows (Java typically expands by 50%).
- Best at: random access (`get(index)`) — O(1).
- Worst at: inserting/removing in the middle — O(n) due to shifting elements.

### LinkedList (Doubly Linked Nodes)

- Memory layout: elements stored in nodes scattered across the heap; each node has `next` and `prev` references.
- Best at: adding/removing at ends or known node positions — O(1) for pointer updates.
- Worst at: random access by index — O(n) because you must traverse nodes.

---

## Part 2: Code Implementation & Iteration Techniques

Use `List<E>` for upcasting and coding to the interface. Below are key iteration techniques and safe modification during traversal.

```java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ListMastery {
    public static void main(String[] args) {
        // Upcasting: code to the interface
        List<String> candidatePool = new ArrayList<>();
        candidatePool.add("Bharath");
        candidatePool.add("TCS_Developer");
        candidatePool.add("Product_Engineer");

        // --- Iteration Method 1: Standard Iterator ---
        // Safe to remove elements while iterating
        Iterator<String> itr = candidatePool.iterator();
        while (itr.hasNext()) {
            String element = itr.next();
            if ("TCS_Developer".equals(element)) {
                itr.remove(); // safe removal
            }
        }

        // --- Iteration Method 2: Enhanced For-Loop (for-each) ---
        // Simple and readable for read-only access
        System.out.println("Remaining Candidates:");
        for (String candidate : candidatePool) {
            System.out.println(candidate);
        }

        // --- Iteration Method 3: ListIterator ---
        // Bidirectional traversal and modification
        List<String> ll = new LinkedList<>(candidatePool);
        ListIterator<String> listIt = ll.listIterator();
        while (listIt.hasNext()) {
            String s = listIt.next();
            if (s.startsWith("P")) {
                listIt.set(s + "_UPDATED"); // modify current element
            }
        }

        // Print linked list
        System.out.println(ll);
    }
}
```

Notes:
- Use `Iterator.remove()` to avoid `ConcurrentModificationException` while traversing.
- `ListIterator` supports `hasPrevious()`, `previous()`, `add()`, and `set()` and only works on `List` implementations.

---

## Part 3: Interview Q&A (TCS → Product-Level)

**Q1: Main difference between `ArrayList` and `LinkedList`?**

- `ArrayList`: backed by a resizable array — choose it for fast random access and read-heavy workloads.
- `LinkedList`: backed by doubly-linked nodes — choose it for frequent inserts/removals at ends or when you hold node references.

**Q2: Default capacity growth formula for `ArrayList`?**

- Java expands capacity using `int newCapacity = oldCapacity + (oldCapacity >> 1);` (≈ old + 50%).

**Q3: What is `ConcurrentModificationException` and how to prevent it?**

- Thrown when a collection is structurally modified while being iterated in a way not tracked by the iterator. Prevent by using `iterator.remove()` or using concurrent collections like `CopyOnWriteArrayList` or explicit synchronization.

**Q4: Difference between `Iterator` and `ListIterator`?**

- `Iterator`: forward-only, available for all `Collection` types, supports `remove()`.
- `ListIterator`: bidirectional, only for `List`, supports `add()`, `set()`, `previous()`, and index access methods.

**Q5: Is `ArrayList` thread-safe? How to make it safe?**

- `ArrayList` is not thread-safe. Make it safe with `Collections.synchronizedList(new ArrayList<>())` or use `CopyOnWriteArrayList` for read-heavy concurrent access.

---

## Quick Summary

- Use `ArrayList` for fast indexed reads; `LinkedList` for frequent inserts/removals.
- Prefer `Iterator` when you must remove while iterating; prefer `ListIterator` when you need bidirectional traversal or in-place updates.

