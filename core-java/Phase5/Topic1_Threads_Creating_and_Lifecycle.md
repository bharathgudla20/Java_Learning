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

---

## The Cooking Analogy Code

Instead of boring data tasks, imagine you are preparing dinner. You want to **boil water** and **chop vegetables** at the same time, rather than waiting for the water to boil completely before you touch a vegetable. [[1](https://www.geeksforgeeks.org/java/multithreading-in-java/), [2](https://blog.devgenius.io/kotlin-asynchronous-programing-threads-callbacks-and-coroutines-for-beginners-0949299da503), [3](https://www.designgurus.io/answers/detail/what-is-concurrency-and-multithreading), [4](https://medium.com/@codecraftclub/simplifying-java-multithreading-runnable-interface-with-a-construction-analogy-56852d7c3df0)]

```java
// Step 1: Create a task by implementing the Runnable interface.
// This represents the work you want to do on a separate lane.
class KitchenTask implements Runnable {
    private String taskName;

    // Constructor to pass the name of the job
    public KitchenTask(String taskName) {
        this.taskName = taskName;
    }

    // This is the code that will run inside the separate thread
    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println(Thread.currentThread().getName() + " is doing: " + taskName + " (Step " + i + ")");

            try {
                // Pause for 500 milliseconds to simulate time passing
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Task was interrupted!");
            }
        }
        System.out.println("✅ " + taskName + " is FINISHED!");
    }
}

public class ThreadEasyExplanation {
    public static void main(String[] args) {
        System.out.println("🚀 Dinner preparation started by: " + Thread.currentThread().getName());

        // Step 2: Define your separate tasks
        KitchenTask task1 = new KitchenTask("Boiling Water");
        KitchenTask task2 = new KitchenTask("Chopping Vegetables");

        // Step 3: Create Thread objects and pass your tasks into them
        Thread thread1 = new Thread(task1, "Chef-Thread-1");
        Thread thread2 = new Thread(task2, "Chef-Thread-2");

        // Step 4: Start the threads!
        // This opens the new lanes. Do NOT call run() directly; call start().
        thread1.start();
        thread2.start();

        System.out.println("👋 The main program thread has finished assigning work!");
    }
}
```

### What You Will See in the Console

Because the threads run **at the same time**, the output mixes together. The exact order might change every time you run it because your computer shuffles the tasks dynamically. [[1](https://jenkov.com/tutorials/java-concurrency/index.html), [2](https://www.youtube.com/watch?v=SztE5W41on4&vl=en&t=50), [3](https://www.cis.upenn.edu/~bcpierce/courses/629/papers/Java-tutorial/java/threads/simple.html), [4](https://www.v2vedtech.com/uploads/user/content/220245-MSBTE-22412-Java(Unit%201).pdf), [5](https://math.hws.edu/eck/cs124/javanotes7/c12/s1.html)]

```text
🚀 Dinner preparation started by: main
👋 The main program thread has finished assigning work!
Chef-Thread-1 is doing: Boiling Water (Step 1)
Chef-Thread-2 is doing: Chopping Vegetables (Step 1)
Chef-Thread-2 is doing: Chopping Vegetables (Step 2)
Chef-Thread-1 is doing: Boiling Water (Step 2)
Chef-Thread-1 is doing: Boiling Water (Step 3)
Chef-Thread-2 is doing: Chopping Vegetables (Step 3)
✅ Boiling Water is FINISHED!
✅ Chopping Vegetables is FINISHED!
```
