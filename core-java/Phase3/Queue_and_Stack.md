# Phase 3, Topic 4: Collections — Queue & Stack

## Part 1: Queue vs. Stack (The Core Ordering Rules)

### 1. Queue (FIFO: First-In, First-Out)
A queue works like a real-world line. The first element inserted is the first one removed.

- `add()` / `offer()` → insert at the back
- `remove()` / `poll()` → remove from the front

### 2. Stack (LIFO: Last-In, First-Out)
A stack works like a pile of plates. The last element inserted is the first one removed.

- `push()` → add to the top
- `pop()` → remove from the top

---

## Part 2: Key Implementations to Master

### 1. PriorityQueue
A `PriorityQueue` is a queue where elements are served based on priority rather than insertion order.

- It is backed by a binary heap.
- By default, it gives the smallest element first.
- Best for scheduling and top-k problems.

### 2. Deque and ArrayDeque
A `Deque` is a double-ended queue. It allows insertion and removal from both ends.

- `ArrayDeque` is the modern and faster alternative to `Stack`.
- It can be used as both a queue and a stack.

---

## Part 3: Code Implementation

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

public class QueueStackMastery {
    public static void main(String[] args) {
        // Queue using ArrayDeque
        Queue<String> printerQueue = new ArrayDeque<>();
        printerQueue.offer("Doc_1.pdf");
        printerQueue.offer("Doc_2.pdf");
        System.out.println("Queue Poll: " + printerQueue.poll());

        // Stack using ArrayDeque
        Deque<String> browserHistory = new ArrayDeque<>();
        browserHistory.push("Google.com");
        browserHistory.push("GitHub.com");
        System.out.println("Stack Pop: " + browserHistory.pop());

        // PriorityQueue
        Queue<Integer> pq = new PriorityQueue<>();
        pq.offer(40);
        pq.offer(10);
        pq.offer(30);
        System.out.println("PriorityQueue Poll: " + pq.poll());
    }
}
```

---

## Part 4: Interview Questions

### Q1: Why avoid `java.util.Stack`?
`Stack` is legacy and synchronized, which makes it slower in modern applications. The better choice is `ArrayDeque`.

### Q2: Difference between `add()` and `offer()`?
- `add()` throws an exception if it fails.
- `offer()` returns `false` instead.

### Q3: Difference between `remove()` and `poll()`?
- `remove()` throws an exception if the queue is empty.
- `poll()` returns `null`.

### Q4: What is the underlying structure of `PriorityQueue`?
It uses a binary heap, so insertion and removal take $O(\log n)$ time, while peek is $O(1)$.

### Q5: Can `PriorityQueue` or `ArrayDeque` contain `null`?
No. `PriorityQueue` does not allow `null` because comparisons would fail, and `ArrayDeque` does not allow it because it uses `null` internally as a marker.

---

## Quick Summary

- Use `Queue` for FIFO behavior.
- Use `Stack`/`Deque` for LIFO behavior.
- Use `PriorityQueue` when elements need priority-based processing.
- Prefer `ArrayDeque` over the old `Stack` class.
