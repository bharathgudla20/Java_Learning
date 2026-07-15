# Phase 5, Topic 3: Synchronization, Intrinsic Locks, and Deadlocks

When multiple threads share mutable data such as a counter, a list, or a database connection pool, things can become messy. If multiple threads try to read and write to the same memory location at the same time, it causes race conditions and corrupts your data. This is where synchronization comes in.

---

## Part 1: Intrinsic Locks and the synchronized Keyword

Every object in Java has an implicit internal lock associated with it, known as an intrinsic lock or monitor lock.

When a thread enters a block of code marked with the synchronized keyword, it automatically acquires that object’s intrinsic lock. No other thread can execute any synchronized block guarded by that same lock until the first thread releases it.

There are two primary ways to apply synchronization:

### 1. Synchronized Methods

```java
public synchronized void increment() {
    this.count++; // Locks the 'this' instance (the object itself)
}
```

- Target of the lock: the current object instance (`this`)
- If it is a `static synchronized` method, it locks the class-level blueprint object (`ClassName.class`)

### 2. Synchronized Blocks (Highly Preferred)

```java
public void increment() {
    synchronized (lockObject) {
        this.count++; // Only locks on 'lockObject'
    }
}
```

### Why synchronized blocks are better

Synchronizing an entire method forces thread lock overhead across the whole execution block, even if only one line of code actually mutates shared memory. Synchronized blocks let you lock only the specific critical section, keeping your code more efficient.

---

## Part 2: Deadlocks and the Circular Wait

While synchronization solves data corruption, overusing or incorrectly nesting locks can lead to a deadlock.

A deadlock occurs when two or more threads are blocked forever, each waiting for a lock held by the other.

### Classic deadlock scenario

1. Thread 1 acquires Lock A and sleeps for a millisecond.
2. Thread 2 acquires Lock B and sleeps for a millisecond.
3. Thread 1 tries to acquire Lock B, but it is blocked because Thread 2 holds it.
4. Thread 2 tries to acquire Lock A, but it is blocked because Thread 1 holds it.

Both threads are now stuck waiting forever.

---

## Part 3: Code Implementation (Deadlock and Fix)

Here is a complete runnable example showing a nested-lock deadlock and how to resolve it using consistent lock acquisition ordering.

```java
public class DeadlockDemo {
    private static final Object LockA = new Object();
    private static final Object LockB = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (LockA) {
                System.out.println("[Thread 1] Acquired LockA");
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                System.out.println("[Thread 1] Waiting to acquire LockB...");
                synchronized (LockB) {
                    System.out.println("[Thread 1] Successfully acquired LockB!");
                }
            }
        });

        // Deadlock version: acquires locks in the wrong order
        /*
        Thread thread2Deadlock = new Thread(() -> {
            synchronized (LockB) {
                System.out.println("[Thread 2] Acquired LockB");
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                System.out.println("[Thread 2] Waiting to acquire LockA...");
                synchronized (LockA) {
                    System.out.println("[Thread 2] Successfully acquired LockA!");
                }
            }
        });
        */

        // Fixed version: acquires locks in the same order as Thread 1
        Thread thread2Fixed = new Thread(() -> {
            synchronized (LockA) {
                System.out.println("[Thread 2] Acquired LockA");
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                System.out.println("[Thread 2] Waiting to acquire LockB...");
                synchronized (LockB) {
                    System.out.println("[Thread 2] Successfully acquired LockB!");
                }
            }
        });

        thread1.start();
        // thread2Deadlock.start();
        thread2Fixed.start();
    }
}
```

---

## Part 4: Interview-Style Questions and Answers

### Q1: What does reentrant lock mean, and is Java synchronized reentrant?

Yes. Java’s intrinsic locks are reentrant. Reentrancy means that if a thread already holds a lock, it can enter another synchronized block or method guarded by the same lock without deadlocking itself.

The JVM tracks this with a lock acquisition count. Each time the thread re-enters the same lock, the counter increases. It is fully released only when the count reaches zero.

### Q2: What are the four necessary conditions for a deadlock, and how do we prevent them?

The four conditions are:

1. Mutual exclusion: only one thread can hold a resource at a time
2. Hold and wait: a thread holds one resource while requesting another
3. No preemption: resources cannot be forcibly taken away
4. Circular wait: a cycle of threads waits on each other

The easiest prevention is to eliminate circular wait by enforcing a strict global lock ordering. For example, always acquire Lock A before Lock B.

### Q3: What is the difference between a class-level lock and an instance-level lock?

- An instance-level lock is acquired by a non-static synchronized method or block using `this` or an instance object. It restricts access only for that one object instance.
- A class-level lock is acquired by a `static synchronized` method or by synchronizing on `ClassName.class`. It applies globally across all instances of the class.

### Q4: Why is it bad practice to synchronize on String objects or primitive wrappers?

Using String literals or boxed primitive values as locks is dangerous because they may be shared by the JVM’s internal pools. Different parts of the application may end up synchronizing on the same object accidentally, causing surprising contention and deadlocks. Always use a dedicated private lock object such as:

```java
private final Object lock = new Object();
```

---

## Quick Summary

- Synchronization prevents race conditions by protecting shared resources.
- `synchronized` methods and blocks use intrinsic locks.
- Synchronized blocks are preferred because they lock only the critical section.
- Deadlocks happen when threads wait on each other in a circular chain.
- A consistent lock ordering helps prevent deadlocks.
