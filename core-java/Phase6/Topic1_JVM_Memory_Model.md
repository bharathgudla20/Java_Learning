# Phase 6, Topic 1: JVM Memory Model

## Why this matters

Interviewers ask this to check whether you understand where objects actually live, why `OutOfMemoryError` happens, and how garbage collection affects performance — all real concerns in production systems handling high traffic.

---

## 1. The Big Picture — JVM Memory Areas

When a JVM starts, it divides memory into distinct regions:

| Area | Stores | Shared across threads? |
|---|---|---|
| Heap | All objects, instance variables | Yes — shared |
| Stack | Method calls, local variables, references | No — one per thread |
| Metaspace | Class metadata, method info, static variables (Java 8+) | Yes — shared |
| PC Register | Address of currently executing instruction | No — one per thread |
| Native Method Stack | Native (non-Java) method calls | No — one per thread |

---

## 2. Stack — Per-thread, Method Execution

```java
public void methodA() {
    int x = 10;          // stored on the stack
    methodB();
}

public void methodB() {
    int y = 20;           // separate stack frame
}
```

Each thread gets its own stack. Every method call pushes a stack frame onto that thread's stack. The frame holds local variables, parameters, and the return address. When the method returns, its frame is popped off.

**Interview connection:** deep recursion without a base case exhausts the stack and causes `StackOverflowError`.

```java
public void recurse() {
    recurse();
}
```

---

## 3. Heap — Shared, Where Objects Actually Live

```java
Person p = new Person("Bharath");
// "p" (the reference) lives on the stack
// the actual Person object lives on the heap
```

All objects — regardless of which thread created them — live in the heap, shared across all threads. This is exactly why shared objects need synchronization, tying back to the concurrency topics.

If the heap fills up and cannot grow, you get:

- `OutOfMemoryError: Java heap space`

---

## 4. Heap Internals — Young Generation and Old Generation

The heap is not one flat block. It is divided based on the generational hypothesis: most objects die young.

```text
┌─────────────────────────────┬───────────────────┐
│         Young Generation     │   Old Generation  │
│  ┌───────┬─────────┬─────────┐│                   │
│  │ Eden  │ Survivor│ Survivor││    (Tenured)      │
│  │       │   S0    │   S1    ││                   │
│  └───────┴─────────┴─────────┘│                   │
└─────────────────────────────┴───────────────────┘
```

### Young Generation

Small and collected frequently.

- `Eden space` — every new object is created here first.
- `Survivor spaces (S0, S1)` — objects that survive a GC cycle in Eden are moved here.
- Two survivor spaces are used alternately, so one is always empty at any time.

### Old Generation

Large and collected less often.

Objects that survive multiple young-generation GC cycles are promoted or tenured into the old generation. These are usually long-lived objects such as caches or singletons.

### Flow

```text
new Object() → Eden → (survives GC) → Survivor (S0/S1)
                     → (survives multiple GCs) → Old Generation
```

---

## 5. Metaspace (Java 8+)

Before Java 8, class metadata lived in a fixed-size area called `PermGen`, which frequently caused:

- `OutOfMemoryError: PermGen space`

Java 8 replaced the old `PermGen` scheme with `Metaspace`.

### Key difference

- `PermGen` was part of the heap.
- `Metaspace` lives in native, off-heap memory.
- Metaspace grows dynamically by default, bounded only by available OS memory unless you cap it with `-XX:MaxMetaspaceSize`.

| Version | Memory area | Location | Size behavior |
|---|---|---|---|
| Before Java 8 | `PermGen` | Part of the heap | Fixed at startup |
| Java 8+ | `Metaspace` | Native/off-heap memory | Grows dynamically by default |

---

## 6. Garbage Collection Basics

GC automatically reclaims heap memory occupied by objects that are no longer reachable.

### Minor GC

- Cleans the Young Generation
- Fast and frequent
- Usually low pause time

### Major/Full GC

- Cleans the Old Generation, often the whole heap
- Slower and less frequent
- Can cause noticeable pause times or stop-the-world behavior

### Stop-the-world

This important term means that during certain GC phases, all application threads pause so the GC can safely identify and reclaim garbage without objects changing underneath it.

Minimizing stop-the-world pause time is a major focus of modern GC algorithms.

### What makes an object garbage?

Roughly, an object is garbage when it is no longer reachable from any GC root, such as:

- stack variables
- static fields
- active threads

```java
Person p = new Person("Bharath");
p = null;  // now the Person object has no reachable reference
           // it becomes eligible for GC (not necessarily collected immediately)
```

---

## 7. Quick Comparison Table

| Area | Stores | Per-thread or shared? | Common error on exhaustion |
|---|---|---|---|
| Stack | Local variables, method frames | Per-thread | `StackOverflowError` |
| Heap | Objects | Shared | `OutOfMemoryError: Java heap space` |
| Metaspace | Class metadata | Shared | `OutOfMemoryError: Metaspace` |

---

## Quick Summary

- The stack stores method frames and local variables for one thread.
- The heap stores all objects and is shared across threads.
- Young generation is for short-lived objects; old generation is for long-lived objects.
- `Metaspace` replaced `PermGen` in Java 8+.
- GC reclaims unreachable objects, and major GC can pause the application.
