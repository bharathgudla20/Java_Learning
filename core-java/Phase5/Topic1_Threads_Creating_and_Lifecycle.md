# Phase 5, Topic 1: Threads — Creating & Lifecycle

## Why this matters

Multithreading lets a program do multiple things “at once” by running several threads within a single process. Product companies test this because real systems such as web servers, order processing, and payment systems are inherently concurrent. Mishandling threads can cause race conditions, deadlocks, and data corruption in production.

---

## 1. Process vs Thread

- Process: independent program with its own memory space
- Thread: lightweight sub-unit of a process
- Processes are heavyweight and costly to create
- Threads are cheaper to create
- Processes do not share memory
- Threads share the same heap memory

A Java program itself runs on at least one thread — the main thread.

---

## 2. Two Ways to Create a Thread

### Way 1 — Extend Thread

```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Running: " + Thread.currentThread().getName());
    }
}

MyThread t = new MyThread();
t.start();   // NOT t.run()!
```

### Way 2 — Implement Runnable (preferred)

```java
class MyTask implements Runnable {
    public void run() {
        System.out.println("Running: " + Thread.currentThread().getName());
    }
}

Thread t = new Thread(new MyTask());
t.start();
```

Or with a lambda (since Runnable is a functional interface):

```java
Thread t = new Thread(() -> System.out.println("Running via lambda"));
t.start();
```

### Why Runnable is preferred

Java does not support multiple inheritance. If your class extends Thread, it cannot extend anything else. Implementing Runnable keeps your class free to extend other classes and separates the “task” from the “thread mechanism,” which is better design.

---

## 3. start() vs run() — the most common gotcha

```java
Thread t = new Thread(() -> System.out.println("Hello"));
t.run();     // ❌ runs on the CURRENT thread, no new thread created
t.start();   // ✅ creates a new thread, then calls run() on it
```

Calling run() directly is just a normal method call. It does not spawn a thread. This often confuses beginners and interview candidates.

---

## 4. Thread Lifecycle (states)

A thread moves through these states — the Thread.State enum:

```text
NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED
```

| State | Meaning |
|---|---|
| NEW | Thread object created, start() has not yet been called |
| RUNNABLE | Executing or ready to execute; the CPU scheduler decides when it runs |
| BLOCKED | Waiting to acquire a lock, such as when blocked on synchronized code |
| WAITING | Waiting indefinitely for another thread, for example via wait() or join() with no timeout |
| TIMED_WAITING | Waiting for a fixed time, such as sleep(ms) or join(ms) |
| TERMINATED | run() has completed |

```java
Thread t = new Thread(() -> {});
System.out.println(t.getState());  // NEW
t.start();
System.out.println(t.getState());  // RUNNABLE
```

---

## 5. sleep() vs join()

```java
// sleep() — pauses the CURRENT thread for a fixed time, doesn't release locks
Thread.sleep(1000);  // static method, throws InterruptedException

// join() — makes the CALLING thread wait until the target thread finishes
Thread t1 = new Thread(() -> { /* long task */ });
t1.start();
t1.join();   // main thread waits here until t1 finishes
System.out.println("t1 is done, continuing...");
```

### Key distinction

- sleep() pauses a thread regardless of others
- join() is about ordering — one thread waits for another to complete

---

## 6. Daemon Threads

```java
Thread t = new Thread(() -> { /* background task */ });
t.setDaemon(true);  // must be set BEFORE start()
t.start();
```

Daemon threads run in the background and do not prevent JVM shutdown. When all non-daemon (user) threads finish, the JVM exits even if daemon threads are still running. Garbage collection runs on a daemon thread.

---

## Quick Summary

- Use Runnable for better design
- Always use start() to create a new thread
- Understand the lifecycle: NEW → RUNNABLE → BLOCKED/WAITING/TIMED_WAITING → TERMINATED
- sleep() pauses; join() waits for completion
- Daemon threads are background threads that do not keep the JVM alive
