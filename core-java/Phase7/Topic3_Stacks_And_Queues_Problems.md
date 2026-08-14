# Phase 7, Topic 3: Stacks & Queues — Problems

This topic covers the most common stack and queue interview patterns:

- Balanced Parentheses
- Next Greater Element
- LRU Cache

These three problems cover the most common stack/queue interview patterns — and LRU Cache specifically is one of the most frequently asked design questions at product companies like Amazon.

---

## Why this matters

Stack and Queue are not just data structures to know how to use — interviewers want to see you recognize when a problem's structure matches a stack or queue.

- Balanced Parentheses and Next Greater Element are classic stack-recognition problems.
- LRU Cache tests whether you can combine data structures to hit a specific time complexity requirement.

---

## 1. Balanced Parentheses

### Definition

Given a string of brackets (`()[]{}`), determine if every opening bracket has a matching closing bracket in the correct order. This is the classic use case for a stack (Last-In-First-Out matches how nested brackets must close).

```java
public boolean isValid(String s) {
    Stack<Character> stack = new Stack<>();
    Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');

    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c); // opening bracket -> push
        } else {
            if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                return false; // mismatched or nothing to match against
            }
        }
    }
    return stack.isEmpty(); // true only if everything was matched
}
```

### Walkthrough on `"{[()]}"`

- `{` → push → stack: `[{]`
- `[` → push → stack: `[{, []`
- `(` → push → stack: `[{, [, (]`
- `)` → pop `(`, matches ✅ → stack: `[{, []`
- `]` → pop `[`, matches ✅ → stack: `[{]`
- `}` → pop `{`, matches ✅ → stack: `[]`
- End: stack is empty → valid ✅

### Why it must be a stack, not a queue

The most recently opened bracket must be the first one closed — that is exactly LIFO behavior.

A queue works in FIFO order, which would check brackets in the wrong order.

### Interview trap

Do not forget the final `stack.isEmpty()` check.

A string like `"((("` would pass every individual character check but leave unclosed brackets on the stack.

---

## 2. Next Greater Element

### Definition

For each element in an array, find the next element to its right that is greater than it, or `-1` if none exists. This is efficiently solved with a monotonic stack.

### Brute force

Nested loop, `O(n^2)`.

For each element, scan right until you find a bigger one.

### Monotonic stack solution — `O(n)`

```java
public int[] nextGreaterElement(int[] arr) {
    int[] result = new int[arr.length];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices, kept in decreasing value order

    for (int i = 0; i < arr.length; i++) {
        while (!stack.isEmpty() && arr[stack.peek()] < arr[i]) {
            result[stack.pop()] = arr[i]; // arr[i] is the "next greater" for whatever's on top
        }
        stack.push(i);
    }
    return result;
}
```

### Walkthrough on `[4, 5, 2, 10, 8]`

- `i = 0 (4)`: stack empty → push `0` → stack: `[0]`
- `i = 1 (5)`: `arr[0] = 4 < 5` → pop `0`, `result[0] = 5` → push `1` → stack: `[1]`
- `i = 2 (2)`: `arr[1] = 5 > 2` → no pop → push `2` → stack: `[1, 2]`
- `i = 3 (10)`: `arr[2] = 2 < 10` → pop `2`, `result[2] = 10`; `arr[1] = 5 < 10` → pop `1`, `result[1] = 10` → push `3` → stack: `[3]`
- `i = 4 (8)`: `arr[3] = 10 > 8` → no pop → push `4` → stack: `[3, 4]`
- End: `result = [5, 10, 10, -1, -1]` ✅

### Why this is `O(n)`

Even though there is a nested `while` loop, each index is pushed once and popped at most once.

So the total number of push/pop operations is bounded by `2n`, not `n^2`.

This is the standard amortized analysis argument worth stating explicitly in interviews.

### Key idea to remember

The stack holds indices whose “next greater” has not been found yet.

Whenever a larger element appears, it resolves everything smaller that was sitting on the stack.

---

## 3. LRU Cache (Least Recently Used)

### Definition

A fixed-capacity cache that evicts the least recently used item when full and a new item needs to be inserted. It must support `get()` and `put()` in `O(1)` time each.

### The design insight

You need:

- fast lookup → `HashMap`
- fast reordering by recency → doubly linked list

Neither structure alone gives `O(1)` for both operations. That is why they are combined.

---

## Simplified approach using Java's `LinkedHashMap`

This is a good shortcut to mention, but interviewers usually want the manual version.

```java
class LRUCache extends LinkedHashMap<Integer, Integer> {
    private int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true); // true = access-order
        this.capacity = capacity;
    }

    public Integer get(int key) {
        return super.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        super.put(key, value);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
```

### Parameter breakdown

- `capacity`: initial bucket size
- `0.75f`: load factor
- `true`: access-order mode

When access-order is enabled:

- every `get()` or `put()` moves that entry to the end of the list
- the head is the least recently used item
- the tail is the most recently used item

`removeEldestEntry()` is automatically called by `LinkedHashMap` after insertion, and if it returns `true`, the eldest entry is removed.

---

## Manual implementation — HashMap + Doubly Linked List

This is what interviewers usually want to see you build.

```java
class LRUCache {
    class Node {
        int key, value;
        Node prev, next;

        Node(int k, int v) {
            key = k;
            value = v;
        }
    }

    private Map<Integer, Node> map = new HashMap<>();
    private int capacity;
    private Node head = new Node(-1, -1); // dummy head (most recently used side)
    private Node tail = new Node(-1, -1); // dummy tail (least recently used side)

    LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        remove(node);
        insertAtFront(node); // mark as most recently used
        return node.value;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            remove(map.get(key));
        }

        if (map.size() == capacity) {
            Node lru = tail.prev; // node just before dummy tail = least recently used
            remove(lru);
            map.remove(lru.key);
        }

        Node newNode = new Node(key, value);
        insertAtFront(newNode);
        map.put(key, newNode);
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertAtFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
}
```

### Why both structures are needed

- `HashMap` gives `O(1)` lookup by key.
- Doubly Linked List gives `O(1)` removal and insertion at the front.
- Dummy head and tail nodes eliminate edge-case null checks.

A singly linked list would require `O(n)` to find the node before the one being removed, which breaks the required complexity.

---

## Quick Comparison Table

| Problem | Data Structure Used | Time Complexity | Key Idea |
|--------|--------------------|-----------------|----------|
| Balanced Parentheses | Stack | `O(n)` | LIFO matches nested closing order |
| Next Greater Element | Monotonic Stack | `O(n)` amortized | Stack holds unresolved indices |
| LRU Cache | HashMap + Doubly Linked List | `O(1)` per operation | HashMap for lookup, DLL for recency order |

---

## Final Takeaway

These three problems are important because they represent the three most common interview patterns involving stack-like behavior:

1. Matching nested structure → Balanced Parentheses
2. Resolving values against previous elements → Next Greater Element
3. Handling recency and eviction → LRU Cache

If you can recognize when a problem needs LIFO ordering or a recency list, you are already thinking in the right interview direction.
