# Phase 5, Topic 4: `ExecutorService` & Thread Pools

## Why this matters

Creating a new `Thread` for every task is expensive (OS-level resource) and gives you no control — no limit on how many run at once, no easy way to get a return value back, and no clean shutdown. `ExecutorService` solves all of this by managing a pool of reusable threads.

---

## 1. The Problem With Raw Threads

```java
for (int i = 0; i < 10000; i++) {
    new Thread(() -> doWork()).start();  // BAD — 10,000 threads!
}
```

This can crash the JVM with `OutOfMemoryError` because each thread costs OS memory (stack space, roughly 1 MB by default) and context-switching between thousands of threads hurts performance badly.

**The fix:** reuse a fixed, controlled number of threads that pick up tasks from a queue.

---

## 2. Creating an `ExecutorService`

```java
import java.util.concurrent.*;

ExecutorService executor = Executors.newFixedThreadPool(4);

executor.submit(() -> System.out.println("Task running on " + Thread.currentThread().getName()));

executor.shutdown();  // no new tasks accepted; lets running ones finish
```

**Interview trap:** forgetting `shutdown()`. If you never call it, the pool's threads stay alive and the JVM will not exit even after `main()` finishes.

---

## 3. Common Types of Executors

Factory method | Behavior | Use case
---|---|---
`newFixedThreadPool(n)` | Fixed number of threads, tasks queue up if all busy | Predictable, steady workload
`newCachedThreadPool()` | Creates new threads as needed, reuses idle ones, no upper limit | Many short-lived tasks, bursty workload
`newSingleThreadExecutor()` | Exactly one thread, tasks run sequentially in order | Tasks that must run one at a time, in order
`newScheduledThreadPool(n)` | Runs tasks after a delay or periodically | Cron-like or recurring jobs

```java
ExecutorService cached = Executors.newCachedThreadPool();
ExecutorService single = Executors.newSingleThreadExecutor();

ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
scheduler.scheduleAtFixedRate(() -> System.out.println("tick"), 0, 1, TimeUnit.SECONDS);
```

**Good follow-up to know:** `newCachedThreadPool()` can be dangerous in production because with no upper bound on threads, a sudden burst of tasks can still create unlimited threads and blow up memory. `newFixedThreadPool` is usually safer for production because it is bounded.

---

## 4. `Runnable` vs `Callable`

```java
// Runnable — no return value, can't throw checked exceptions
Runnable task1 = () -> System.out.println("no result");

// Callable — returns a value, CAN throw checked exceptions
Callable<Integer> task2 = () -> {
    return 10 + 20;
};

Future<Integer> future = executor.submit(task2);
Integer result = future.get();  // blocks until result is ready
```

| Type | Return value | Checked exceptions | Submitted via |
|---|---|---|---|
| `Runnable` | No (`void run()`) | Cannot throw | `execute()` or `submit()` |
| `Callable<V>` | Yes (`V call()`) | Can throw | `submit()` only |

---

## 5. `Future` — Getting Results Back

```java
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

System.out.println("Doing other work...");
Integer result = future.get();          // blocks until done
Integer result2 = future.get(2, TimeUnit.SECONDS);  // blocks with timeout, throws TimeoutException
future.cancel(true);                    // attempt to cancel/interrupt
future.isDone();
future.isCancelled();
```

`future.get()` is a blocking call — the calling thread waits until the task completes. This is a key point: submitting does not block, but asking for the result does unless it is already done.

---

## 6. Shutdown — Proper Cleanup

```java
executor.shutdown();
// Graceful: stops accepting new tasks, finishes queued/running ones

executor.shutdownNow();
// Aggressive: attempts to stop all actively executing tasks,
// returns list of tasks that never started

executor.awaitTermination(5, TimeUnit.SECONDS);
// Blocks until all tasks finish, or timeout — used after shutdown()
```

A common production pattern:

```java
executor.shutdown();
try {
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
}
```

---

## 7. `invokeAll()` and `invokeAny()`

```java
List<Callable<Integer>> tasks = List.of(() -> 1, () -> 2, () -> 3);

List<Future<Integer>> results = executor.invokeAll(tasks);  // waits for ALL to finish
Integer fastest = executor.invokeAny(tasks);
```

These methods are useful when you want the pool to manage a batch of related tasks for you.

---

## Quick Interview Summary

- `new Thread()` per task is expensive and unbounded.
- `ExecutorService` reuses worker threads from a pool.
- Use `newFixedThreadPool()` for predictable production workloads.
- `Runnable` has no return value; `Callable` returns a value.
- `Future.get()` blocks until the result is available.
- Always call `shutdown()` to avoid keeping JVM threads alive.
- `shutdownNow()` is the aggressive version; `awaitTermination()` helps with clean shutdown.
