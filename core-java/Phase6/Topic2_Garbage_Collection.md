### 1. Definition

**Garbage Collection (GC)** is the automatic process by which the JVM identifies objects in the Heap that are no longer reachable by any part of the running program, and reclaims the memory they occupy — without the programmer having to manually free memory (unlike C/C++'s `malloc`/`free`).

An object is considered **garbage** when there is no live reference chain from a **GC Root** (active thread stacks, static variables, JNI references) leading to it.

```java
Person p = new Person("Bharath");  // object created, reachable via 'p'
p = null;                          // no reference points to it anymore
                                    // → eligible for garbage collection
```

"Eligible" doesn't mean "collected immediately" — the JVM decides *when* to actually run GC.

---

### 2. Why GC Exists

Without GC, developers would have to manually track every object's lifetime and free it — a major source of bugs in C/C++ (dangling pointers, double-free, memory leaks). GC automates this, trading a small performance cost for memory safety and developer simplicity.

---

### 3. Types of GC Collectors — Definitions & Comparison

| Collector | Definition | Trade-off |
|---|---|---|
| **Serial GC** | A single thread performs all GC work; all application threads pause during collection. | Simple, low overhead, but pauses can be long. Good for small heaps/single-core machines. |
| **Parallel GC** | Multiple threads perform GC work simultaneously, but application threads still pause (stop-the-world). | Higher throughput than Serial, but pause times still noticeable. |
| **CMS (Concurrent Mark Sweep)** | Does most marking/sweeping concurrently with running application threads to reduce pause time. Deprecated in Java 9, removed in Java 14. | Lower pauses, but more CPU overhead and heap fragmentation. |
| **G1GC (Garbage First)** | Divides the heap into many equal-sized regions; collects the regions containing the most garbage first. **Default since Java 9.** | Balances throughput and pause time; predictable, tunable pauses. |
| **ZGC / Shenandoah** | Ultra-low-pause collectors designed for very large heaps (sub-millisecond pauses). | Best for latency-critical, huge-heap systems; more memory/CPU overhead. |

---

### 4. G1GC in Detail

**Definition:** G1 (Garbage First) divides the heap into many small, fixed-size regions (not one big Young block and one big Old block). Each region is dynamically assigned a role — Eden, Survivor, or Old — as needed. G1 tracks the amount of live/garbage data per region and **prioritizes collecting the regions with the most garbage first**, which reclaims the most memory for the least effort — hence the name "Garbage First."

```text
┌────┬────┬────┬────┬────┬────┬────┬────┐
│ E  │ O  │ E  │ S  │ O  │ E  │ O  │ E  │
└────┴────┴────┴────┴────┴────┴────┴────┘
E = Eden, S = Survivor, O = Old — roles assigned per region dynamically
```

Key terms:

- **Mixed GC** — a collection cycle that cleans young regions plus some old regions together (instead of a full separate "Old Gen only" collection).
- **Pause time goal** — configurable target: `-XX:MaxGCPauseMillis=200` tells G1 to *try* to keep pauses under 200ms. It's a goal, not a hard guarantee.

---

### 5. Minor GC vs Major/Full GC — Definitions

| Term | Definition |
|---|---|
| **Minor GC** | Collects garbage in the **Young Generation** (Eden + Survivor spaces) only. Frequent, fast, low pause impact. |
| **Major GC / Full GC** | Collects the **Old Generation** (sometimes the entire heap, including Young Gen and Metaspace). Less frequent, but pauses can be significantly longer. |

```java
// Simplified lifecycle example
Object obj = new Object();       // created in Eden
// Minor GC runs → obj survives → moved to Survivor space
// obj survives several more Minor GCs → promoted/"tenured" to Old Gen
// Eventually, if obj becomes unreachable, it's cleaned in a Major/Full GC
```

---

### 6. Tuning Flags — Definitions

| Flag | Meaning |
|---|---|
| `-Xms512m` | Initial heap size at JVM startup (512 MB) |
| `-Xmx2048m` | Maximum heap size the JVM can grow to (2 GB) |
| `-XX:+UseG1GC` | Explicitly selects the G1 garbage collector |
| `-XX:MaxGCPauseMillis=200` | Target maximum pause time goal for G1 |
| `-XX:+PrintGCDetails` | Logs detailed GC activity, useful for diagnosing issues |

**Production tip:** Setting `-Xms` and `-Xmx` to the **same value** is common practice — it prevents the JVM from spending CPU time dynamically resizing the heap while the application runs.

---

### 7. Memory Leaks in Java — Definition & Examples

**Definition:** A memory leak in Java occurs when objects that are no longer needed are still **reachable** through some reference chain, so the GC cannot legally reclaim them — even though the program logically doesn't need them anymore. Java doesn't leak the way C does (forgotten `free()`); it leaks because of **unintentionally retained references**.

**Example 1 — Growing static collection:**

```java
public class SessionManager {
    private static Map<String, Session> sessions = new HashMap<>();
    public void createSession(String id, Session s) {
        sessions.put(id, s);   // added, but NEVER removed
    }
}
```

Since `sessions` is `static`, it lives for the entire application lifetime, and its map keeps referencing every `Session` ever added — none of them can ever be garbage collected, even after the session ends. **Fix:** explicitly remove entries when sessions expire, or use a `WeakHashMap`/eviction-based cache (like Guava Cache or Caffeine).

**Example 2 — Unclosed resources:**

```java
FileInputStream fis = new FileInputStream("file.txt");
// use fis...
// forgot fis.close()  → underlying OS file handle stays open (resource leak)
```

**Fix — try-with-resources:**

```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // use fis
} // automatically closed even if an exception occurs
```

**Example 3 — Inner class holding hidden outer reference:**

```java
class Outer {
    class Inner { }   // non-static inner class implicitly holds Outer.this
}
```

If an `Inner` instance is stored somewhere long-lived (e.g., a listener list), it silently keeps its enclosing `Outer` instance alive too, even if nothing else needs `Outer` anymore. **Fix:** make the inner class `static` if it doesn't need access to the outer instance.

**Example 4 — Listeners/callbacks never unregistered:**

```java
button.addClickListener(myListener);
// if myListener is never removed, and button lives a long time,
// myListener (and whatever it references) can never be GC'd
```

---

### 8. How to Detect Memory Leaks in Practice

- **Heap dumps**: `jmap -dump:format=b,file=heap.hprof <pid>`, or auto-triggered via `-XX:+HeapDumpOnOutOfMemoryError`.
- **Profilers**: VisualVM, Eclipse MAT, JProfiler — analyze heap dumps to see which objects have unexpectedly high "retained size" or reference counts.
- **GC logs**: If Old Generation usage keeps climbing after every Full GC and never drops back down, that's a strong signal of a leak.

---

### 9. Interview Questions — With Answers

**Q1: What's the difference between Minor GC and Major/Full GC?**

**A:** Minor GC cleans only the Young Generation (Eden + Survivor spaces) — it's fast and frequent. Major/Full GC cleans the Old Generation (often the whole heap) — it's slower and less frequent, with longer pause times.

**Q2: Why is G1GC called "Garbage First"?**

**A:** Because it divides the heap into many small regions and always collects the regions containing the **most garbage first**, maximizing memory reclaimed per collection cycle while keeping pause times predictable.

**Q3: What's the default GC collector since Java 9?**

**A:** G1GC (Garbage First Garbage Collector). Before Java 9, it was Parallel GC.

**Q4: Can Java still have memory leaks despite having a garbage collector? How?**

**A:** Yes. GC only reclaims objects that are **unreachable**. If your code keeps unnecessary references alive (e.g., a growing static collection, unregistered listeners, unclosed resources), those objects remain reachable and can never be collected — this is a Java memory leak.

**Q5: Name 3 common causes of memory leaks in Java applications.**

**A:** (1) Static collections that keep growing without removal, (2) unclosed resources like streams/connections, (3) listeners or callbacks that are registered but never unregistered. (Non-static inner classes holding hidden outer references is a good 4th example.)

**Q6: What tools would you use to diagnose a memory leak in production?**

**A:** Take a heap dump (`jmap`, or automatically via `-XX:+HeapDumpOnOutOfMemoryError`) and analyze it with a profiler like Eclipse MAT or VisualVM. Also monitor GC logs — a steadily rising Old Gen usage after repeated Full GCs is a leak indicator.

**Q7: What does `-XX:MaxGCPauseMillis` actually guarantee?**

**A:** It's a **goal**, not a hard guarantee. G1 tries to meet this target pause time by adjusting how much work it does per cycle, but under heavy load it may exceed it.

**Q8: Why might production systems set `-Xms` and `-Xmx` to the same value?**

**A:** To avoid the overhead of the JVM dynamically resizing the heap at runtime, which can cause unpredictable pauses. Fixing both to the same value gives more consistent, predictable performance.