# Java Phase 3 — Topic 8: Java I/O Streams

## What is a Stream?

A stream is a channel through which data flows — like a water pipe. Data flows from a source (file, keyboard, network) through a stream into your program (input), or from your program out to a destination (file, screen, network) (output).

Two directions:
- Input Stream — data flows INTO your program (reading from file)
- Output Stream — data flows OUT of your program (writing to file)

---

## Two Families of Streams

### Byte Streams — raw binary data
Used for images, audio, video, any binary file.

| Class | Purpose |
|---|---|
| FileInputStream | Read bytes from a file |
| FileOutputStream | Write bytes to a file |
| BufferedInputStream | Buffered byte reading |
| BufferedOutputStream | Buffered byte writing |

### Character Streams — text data
Used for text files. Handles Unicode/encoding (UTF-8 etc.) automatically.

| Class | Purpose |
|---|---|
| FileReader | Read characters from a text file |
| FileWriter | Write characters to a text file |
| BufferedReader | Buffered line-by-line text reading |
| BufferedWriter | Buffered text writing |

> Rule: Always use Character Streams for text files. Use Byte Streams only for binary files like images.

---

## Why Buffered Streams?

FileReader reads one character per disk access. BufferedReader reads a chunk of data into memory at once, which is much faster.

```java
BufferedReader br = new BufferedReader(new FileReader("data.txt"));
String line;
while ((line = br.readLine()) != null) {
    System.out.println(line);
}
```

---

## Reading Files

### 1. Read line by line

```java
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

### 2. Read all at once (Java 11+)

```java
String content = Files.readString(Path.of("data.txt"));
List<String> lines = Files.readAllLines(Path.of("data.txt"));
```

### 3. Read bytes (binary files)

```java
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("image.png"))) {
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // process data
    }
}
```

---

## Writing Files

### 1. Write text

```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
    bw.write("Hello Bharath!");
    bw.newLine();
}
```

### 2. Append to file

```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt", true))) {
    bw.write("New entry");
    bw.newLine();
}
```

### 3. Write bytes

```java
try (BufferedOutputStream bos = new BufferedOutputStream(
        new FileOutputStream("output.png"))) {
    byte[] data = getImageBytes();
    bos.write(data);
}
```

---

## Try-with-Resources

Always use try-with-resources to avoid file handle leaks.

```java
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

---

## Summary

- Streams are used for input and output.
- Byte streams are for binary files.
- Character streams are for text files.
- Buffered streams improve performance.
- Use try-with-resources for safe file handling.
