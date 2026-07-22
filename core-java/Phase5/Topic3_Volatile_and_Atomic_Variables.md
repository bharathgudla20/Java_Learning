# Phase 5, Topic 3: `volatile` and Atomic Variables

This topic is about **visibility**, which is a different problem from the race conditions covered in synchronization. `volatile` and atomic classes are useful when you need safe communication between threads without always using a full lock.

---

## Why This Matters

`synchronized` solves two problems at once:

- **Mutual exclusion:** only one thread can enter the critical section at a time.
- **Visibility:** changes made by one thread become visible to other threads.

Sometimes a program only needs visibility, not locking. That is where `volatile` and atomic classes come in.

---

## 1. The Visibility Problem

Consider this class:

```java
class Flag {
    private boolean running = true;

    public void stop() {
        running = false;
    }

    public void run() {
        while (running) {
            // do work
        }
    }
}
```

In a multithreaded setting, `run()` may loop forever. Thread A can call `stop()` and set `running` to `false`, while Thread B continues reading a stale copy of `running` from a CPU cache or register.

This is not a data-corruption race condition. It is a **visibility problem**: one thread's write is not guaranteed to become visible to another thread.

---

## 2. The Fix: `volatile`

```java
class Flag {
    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    public void run() {
        while (running) {
            // do work
        }
    }
}
```

`volatile` tells the JVM that reads and writes to the field must be visible across threads. A thread that reads the volatile field will not be allowed to keep using an arbitrarily stale value.

### Key interview line

> `volatile` guarantees visibility, but it does not guarantee atomicity for compound operations.

A volatile field is appropriate when a value is independently read or written, such as a stop flag or a safely published reference. It does not automatically make the object referenced by a volatile field thread-safe.

---

## 3. Why `volatile` Is Not Enough for Counters

```java
private volatile int count = 0;

public void increment() {
    count++; // Still broken with volatile
}
```

`count++` is a read-modify-write operation with three steps:

1. Read the current value.
2. Add one.
3. Write the new value.

`volatile` makes each individual read and write visible, but it does not make the entire sequence atomic. Two threads can both read `count = 5`, both calculate `6`, and both write `6`. One increment is lost.

The same issue applies to compound operations such as:

- `x++`
- `x += 1`
- `x = x + 1`
- check-then-act logic, such as `if (!initialized) initialize()`

### Rule to Remember

- `volatile` is suitable for a simple flag or a single read/write field.
- `volatile` is not sufficient for compound updates that depend on the previous value.

---

## 4. The `happens-before` Relationship

**Happens-before** is a Java Memory Model rule. If action A happens-before action B, then everything visible to A, including its writes to variables, is guaranteed to be visible to B.

A write to a volatile variable happens-before subsequent reads of that same variable by other threads according to the synchronization order. This is why the `running` flag works: the write of `false` becomes visible to the loop's later read.

Other operations that establish happens-before relationships include:

- Exiting a `synchronized` block happens-before another thread enters a later `synchronized` block guarded by the same lock.
- Calling `Thread.start()` happens-before actions in the started thread.
- Returning from `Thread.join()` happens-before the actions after the `join()` call.

---

## 5. The Real Fix for Counters: Atomic Classes

Use an atomic class for a counter that needs safe compound updates without an explicit lock:

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger count = new AtomicInteger(0);

public void increment() {
    count.incrementAndGet(); // Atomic and thread-safe
}
```

Common atomic classes include:

- `AtomicInteger`
- `AtomicLong`
- `AtomicBoolean`
- `AtomicReference<T>`

Atomic classes use **CAS (Compare-And-Swap)** operations under the hood. CAS is a hardware-supported operation that follows this idea:

1. Read the current value.
2. Compute the new value.
3. Write the new value only if nobody changed the value since it was read.
4. Retry if another thread changed it first.

This provides atomic updates without blocking a thread on a traditional lock.

### Common AtomicInteger Operations

```java
AtomicInteger a = new AtomicInteger(10);

a.incrementAndGet();      // Returns 11 after incrementing

a.compareAndSet(11, 20);  // Sets to 20 only if the current value is 11

a.getAndAdd(5);           // Returns the old value, then adds 5
```

`compareAndSet(expected, update)` returns `true` when the update succeeds and `false` when the current value is not equal to `expected`.

---

## 6. Quick Comparison

| Feature | `volatile` | `synchronized` | `AtomicInteger` |
|---|---|---|---|
| Guarantees visibility | Yes | Yes | Yes |
| Guarantees atomicity for compound operations | No | Yes | Yes |
| Blocks a thread | No | Yes, potentially | No, uses CAS retries |
| Best for | Flags and simple read/write fields | Complex critical sections | Counters and simple atomic updates |

---

## Interview Summary

- **Visibility** means that one thread can see another thread's latest write.
- **Atomicity** means that an operation happens as one indivisible unit.
- `volatile` provides visibility, but not atomicity for operations such as `count++`.
- `synchronized` provides both visibility and mutual exclusion.
- Atomic classes provide thread-safe operations such as increment and compare-and-set without explicit locking.
- Use `volatile` for simple state flags and atomic classes for compound updates to a single value.
