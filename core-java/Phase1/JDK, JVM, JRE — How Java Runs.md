# JDK, JVM, JRE — How Java Runs

## 🛠️ JDK
**Java Development Kit**

Everything a **developer** needs. You install this when you want to **write and compile** Java code.

Contains:
- JRE
- Compiler (`javac`)
- Debugger
- Tools

## ▶️ JRE
**Java Runtime Environment**

Everything needed to **run** a Java program. End-users install this. No compiler included.

Contains:
- JVM
- Core Libraries (`java.lang`, `java.util`, …)

## ⚙️ JVM
**Java Virtual Machine**

The **engine** that runs bytecode. Makes Java platform-independent — "Write once, run anywhere."

Contains:
- ClassLoader
- Memory
- Execution Engine

## Relationship

`JDK ⊇ JRE ⊇ JVM` — each one contains the next.

## How your code actually runs — step by step

1. **You write code → `Hello.java`**
   - Plain text file. Human-readable Java source code.

2. **Compiler converts it → `Hello.class` (bytecode)**
   - The `javac` tool (part of JDK) compiles your code into **bytecode** — not machine code, not human code. It's a middle format only the JVM understands.

3. **ClassLoader loads the `.class` file**
   - The JVM's ClassLoader reads the bytecode into memory. It also loads the standard library classes your code uses (like `System.out`).

4. **JVM executes bytecode line by line**
   - The JVM's **Execution Engine** reads bytecode and converts it to machine code that *your specific OS and CPU* understands. This is why the same `.class` file runs on Windows, Mac, or Linux — each has its own JVM.

5. **JIT compiler speeds things up**
   - The **Just-In-Time (JIT) compiler** inside the JVM watches which parts of your code run frequently, and compiles those parts directly to fast native machine code — so repeat calls are much faster.

## Platform independence — the "write once, run anywhere" magic

Without JVM: You'd write separate code for Windows, Mac, Linux.

With JVM: You write Java once → compile to bytecode once → the JVM on *each* platform translates it.

The JVM is platform-specific, but your `.class` file is not.