# Topic 8 — Date & Time API (`java.time`)

## Why this topic matters for interviews

Before Java 8, date handling used `java.util.Date` and `Calendar` — both are immutable, not thread-safe, and have confusing month indexing (January = 0). Interviewers love asking *"why was `java.time` introduced?"* because it tests whether you understand immutability and legacy API design flaws.

Product companies also ask practical questions like calculating date differences, time zone conversions, and formatting — since these come up constantly in real systems (order timestamps, SLA deadlines, logs).

---

## 1. The Core Classes

Class | Represents | Example
--- | --- | ---
`LocalDate` | Date only, no time, no zone | `2026-07-14`
`LocalTime` | Time only, no date, no zone | `14:30:00`
`LocalDateTime` | Date + time, no zone | `2026-07-14T14:30:00`
`ZonedDateTime` | Date + time + time zone | `2026-07-14T14:30:00+05:30[Asia/Kolkata]`
`Instant` | A point on the timeline (machine timestamp, UTC) | `2026-07-14T09:00:00Z`
`Duration` | Time-based amount (hours, minutes, seconds) | `PT2H30M`
`Period` | Date-based amount (years, months, days) | `P1Y2M10D`

**Key design fact:** All these classes are **immutable and thread-safe**. Every "modifying" method (like `plusDays()`) returns a **new object** instead of changing the original.

```java
LocalDate today = LocalDate.now();
LocalDate nextWeek = today.plusDays(7);
// today is unchanged! nextWeek is a new object.
```

---

## 2. LocalDate — working examples

```java
LocalDate date = LocalDate.of(2026, 7, 14);   // year, month, day
LocalDate today = LocalDate.now();

date.getDayOfWeek();      // TUESDAY
date.getMonth();          // JULY
date.isLeapYear();        // false
date.plusMonths(2);       // 2026-09-14
date.isBefore(today);     // comparison, returns boolean
```

Note: unlike the old `Calendar` class, month here is **1-indexed** (`JULY` not `6`) — a classic gotcha interviewers probe.

---

## 3. LocalDateTime & ZonedDateTime

```java
LocalDateTime ldt = LocalDateTime.of(2026, 7, 14, 10, 30);

ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
ZonedDateTime nyTime = zdt.withZoneSameInstant(ZoneId.of("America/New_York"));
```

`LocalDateTime` has **no concept of time zone** — never use it to represent "the current moment" across systems. Use `Instant` or `ZonedDateTime` for that. This distinction is a very common interview trap.

---

## 4. Duration vs Period (frequently confused)

```java
// Duration — time-based, for hours/minutes/seconds
Duration d = Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 30));
System.out.println(d.toHours());   // 8

// Period — date-based, for years/months/days
Period p = Period.between(LocalDate.of(2020, 1, 1), LocalDate.of(2026, 7, 14));
System.out.println(p.getYears());  // 6
```

**Rule of thumb:** `Duration` = seconds/nanos precision → use with `LocalTime`, `LocalDateTime`, `Instant`. `Period` = calendar precision → use with `LocalDate` only.

---

## 5. Formatting & Parsing

```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
String formatted = LocalDate.now().format(fmt);       // "14-07-2026"
LocalDate parsed = LocalDate.parse("14-07-2026", fmt); // parse back
```

`DateTimeFormatter` is also thread-safe and immutable — unlike the old `SimpleDateFormat`, which is **not thread-safe** (a top interview gotcha: "why shouldn't you share a `SimpleDateFormat` instance across threads?").
