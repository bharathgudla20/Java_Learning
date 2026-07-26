# Phase 6, Topic 3: ClassLoader Mechanism

## Why this matters

Every `.class` file must be loaded into JVM memory before it can run. Interviewers use this topic to test whether you understand how Java achieves security, avoids class duplication, and supports dynamic loading in frameworks such as Spring and Hibernate.

---

## 1. Definition

A **ClassLoader** is part of the JRE responsible for dynamically loading Java classes into JVM memory at runtime. It reads the `.class` bytecode and creates a corresponding `Class` object in **Metaspace**.

Classes are not loaded all at once. They are loaded **lazily**, the first time they are actually referenced.

```java
public class Demo {
    public static void main(String[] args) {
        System.out.println("Start");
        Helper h = new Helper();  // Helper is loaded here, not before
    }
}
```

---

## 2. The Three Built-in ClassLoaders

| ClassLoader | Loads | Location |
|---|---|---|
| **Bootstrap ClassLoader** | Core JDK classes like `java.lang.*`, `java.util.*` | `rt.jar` / core modules, written in native code |
| **Extension (Platform) ClassLoader** | Extension/platform classes | `jre/lib/ext` or platform modules in newer Java |
| **Application (System) ClassLoader** | Your application classes | Classpath (`-cp`, `CLASSPATH`) |

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());  // null
        System.out.println(Test.class.getClassLoader());     // AppClassLoader
    }
}
```

### Interview trap

Bootstrap ClassLoader is implemented in **native code**, not Java. So calling `.getClassLoader()` on a class it loaded returns `null`. That is expected, not an error.

---

## 3. Parent Delegation Model

**Definition:** When a class needs to be loaded, a ClassLoader does not try to load it itself first. It delegates the request to its parent, and only attempts to load it itself if the parent cannot find it.

```text
Application ClassLoader
        ↓ delegates to
Extension ClassLoader
        ↓ delegates to
Bootstrap ClassLoader
```

### Flow

1. Application ClassLoader receives a request to load `com.myapp.MyClass`.
2. It asks its parent (Extension ClassLoader) to try first.
3. Extension ClassLoader asks its parent (Bootstrap) to try first.
4. Bootstrap checks and fails.
5. Extension checks and fails.
6. Application ClassLoader finally loads it itself.

### Why this design exists

- **Security:** prevents a custom `java.lang.String` from overriding the real core class.
- **Avoids duplicate loading:** the same class is not loaded multiple times by different loaders.

```java
// This will NEVER override java.lang.String
package java.lang;
public class String { /* malicious code */ }
```

The real core `String` class wins because Bootstrap is consulted first.

---

## 4. Custom ClassLoaders

You can write your own ClassLoader by extending `ClassLoader` and overriding `findClass()`.

```java
class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassDataFromSomewhere(name);
        return defineClass(name, classData, 0, classData.length);
    }
}
```

### Common uses

- application servers isolating web apps
- plugin systems
- hot-reload tools

---

## 5. Class Loading Phases

Loading a class happens in three main phases:

| Phase | What happens |
|---|---|
| **Loading** | Reads the `.class` bytecode and creates a `Class` object in Metaspace |
| **Linking** | Verifies bytecode, prepares static fields, resolves symbolic references |
| **Initialization** | Executes static initializers and static field assignments |

```java
class Config {
    static int value = compute();  // assigned during initialization

    static int compute() {
        return 42;
    }
}
```

---

## 6. Quick Comparison Table

| ClassLoader | Loads | Written in | `getClassLoader()` result |
|---|---|---|---|
| Bootstrap | Core JDK classes | Native code | `null` |
| Extension | Extension classes | Java | `ExtClassLoader` instance |
| Application | Application classes | Java | `AppClassLoader` instance |

---

## Quick Summary

- ClassLoader loads `.class` files into Metaspace at runtime.
- Java uses the three built-in loaders: Bootstrap, Extension, and Application.
- Parent delegation ensures security and avoids duplicate class loading.
- Custom ClassLoaders are used in advanced systems such as servers, plugins, and hot-reload tools.
