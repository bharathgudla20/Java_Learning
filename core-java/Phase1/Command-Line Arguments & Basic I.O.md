# Command-Line Arguments & Basic I/O (Input/Output)

This topic checks if you understand how a program interacts with the outside world (console, Operating System) and how Java handles data streaming.

---

## Part 1: Command-Line Arguments

### What is it? (In Simple Terms)
Imagine building a command-line tool or background script. Instead of running a program and waiting for it to ask, *"Hey, what is your name?"*, pass the information **right at program launch**, like handing a note to a runner before they start.

In your terminal:
```bash
java MyProgram Bharath TCS
```

### How Java Catches It: The `String[] args` Secret
The `args` array in `public static void main(String[] args)` is a basket catching whatever words you type after the program name.

- `args[0]` holds `"Bharath"`
- `args[1]` holds `"TCS"`

### Code Example
```java
public class TestArgs {
    public static void main(String[] args) {
        // Always check if arguments were actually passed!
        if (args.length > 0) {
            System.out.println("First Argument: " + args[0]);   // Bharath
            System.out.println("Second Argument: " + args[1]);  // TCS
        } else {
            System.out.println("No arguments passed.");
        }
    }
}
```

---

## Part 2: Basic Input / Output (I/O)

To get data from a user *while* the program is running, Java offers two main choices: **Scanner** and **BufferedReader**.

### 1. Scanner (The Friendly Easy Way)
`Scanner` is a high-level assistant. You say, *"Give me the next integer"* or *"Give me the next decimal"*, and it automatically parses the text.

**Best used for:** Small programs, primitive data parsing (`nextInt()`, `nextDouble()`), quick coding rounds.

```java
import java.util.Scanner;

Scanner sc = new Scanner(System.in);  // System.in means read from keyboard
System.out.println("Enter your target salary hike %: ");
int hike = sc.nextInt();
System.out.println("Target: " + hike + "%");
sc.close();  // Good practice to close resources!
```

### 2. BufferedReader (The Fast, Professional Way)
`BufferedReader` doesn't parse; it reads raw text in large chunks into memory buffer. Like a truck moving boxes at once instead of item-by-item. Reads everything as `String`; you must convert if needed.

**Best used for:** Competitive programming or massive input text, because it is **significantly faster** than Scanner.

```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// Notice it requires handling IOException
BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
System.out.println("Enter your dream company name: ");
String company = reader.readLine();  // Reads the whole line as text
```

### Scanner vs BufferedReader (Quick Comparison)

| Aspect | Scanner | BufferedReader |
|--------|---------|----------------|
| Buffer Size | 1KB | 8KB (much larger) |
| Speed | Slower | Faster |
| Parsing | Auto-parses (`nextInt()`, etc.) | Manual parsing required |
| Exception | Hides checked `IOException` | Forces handling `IOException` |
| Thread-Safe | No | Yes (synchronous) |

---

## Part 3: Crack-The-Interview Questions

### Q1: What is the main difference between Scanner and BufferedReader? When would you choose one?

**Your Answer:**
1. **Speed/Buffer Size:** `BufferedReader` has 8KB buffer vs Scanner's 1KB, making it significantly faster for large data.
2. **Data Parsing:** `Scanner` auto-parses via `nextInt()`, `nextDouble()`. `BufferedReader` only reads strings via `readLine()`, requiring manual `Integer.parseInt()`.
3. **Synchronization:** `BufferedReader` is thread-safe; `Scanner` is not.
4. **Exception Handling:** `BufferedReader` forces checked `IOException` handling; `Scanner` hides it.

**Choose based on use case:** Scanner for small programs/quick rounds; BufferedReader for competitive programming/massive input.

---

### Q2: What happens if you access `args[0]` but the user didn't pass command-line arguments?

**Your Answer:** It throws `ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0`. 

Prevention: Always check array length first:
```java
if (args.length > 0) {
    // safe to access args[0]
}
```

---

### Q3: What do `System.out` and `System.in` actually represent in Java?

**Your Answer:**
- `System` is a predefined class in the `java.lang` package.
- `out` is a static variable representing the **Standard Output Stream** (points to console). Instance of `PrintStream`.
- `in` is a static variable representing the **Standard Input Stream** (points to keyboard). Instance of `InputStream`.

---

### Q4: Why is `.close()` on Scanner/BufferedReader highly recommended? What if you don't?

**Your Answer:** When opening a stream (e.g., `System.in` or file), the OS allocates an input channel descriptor to the JVM. Without closing it, you create a **Resource Leak**. Over time, many open streams exhaust available file descriptors, degrading performance or crashing the application.

Good practice:
```java
Scanner sc = new Scanner(System.in);
try {
    // use sc
} finally {
    sc.close();
}
```

Or use try-with-resources (Java 7+):
```java
try (Scanner sc = new Scanner(System.in)) {
    // use sc
}  // auto-closes
```
