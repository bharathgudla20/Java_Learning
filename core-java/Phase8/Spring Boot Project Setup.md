## Spring Boot Project Setup

Spring Initializr, Starters, application.properties

This is a much lighter, more practical topic than IoC/DI — it's about the *tooling* that gets a Spring Boot project running, and it's usually asked as quick-fire questions rather than deep theory.

### Why this matters

Before you can write any REST APIs or business logic, you need to know how a Spring Boot project is actually structured and configured. Interviewers ask about this to gauge real hands-on experience — not just theoretical knowledge — since anyone who's actually built a Spring Boot app knows these basics cold.

---

### 1. Spring Initializr — the project generator

**Definition:** Spring Initializr (start.spring.io) is a web-based tool (also built into IDEs like IntelliJ and VS Code) that generates a ready-to-run Spring Boot project skeleton — you pick your options, and it produces a downloadable zip with the correct folder structure, build file, and starter dependencies already wired up.

**What you configure when generating a project:**

| Option | Meaning |
|---|---|
| **Project** | Maven or Gradle (build tool) |
| **Language** | Java, Kotlin, or Groovy |
| **Spring Boot Version** | Which version of Spring Boot to use |
| **Group** | Reverse-domain package name, e.g., `com.bharath` |
| **Artifact** | Project/JAR name, e.g., `order-service` |
| **Packaging** | Jar (most common, self-contained) or War (deployed to external server) |
| **Java Version** | e.g., 17, 21 |
| **Dependencies** | The "starters" you want (Web, JPA, Security, etc. — see below) |

**Why this tool exists (the real interview point):** setting up a Java project manually — correct folder structure, build file, dependency versions that are compatible with each other — is tedious and error-prone. Spring Initializr eliminates that setup friction, and critically, it picks dependency versions that are **known to work together** (matched to the Spring Boot version you selected).

---

### 2. Project Structure — what you actually get

```text
order-service/
├── src/
│   ├── main/
│   │   ├── java/com/bharath/orderservice/
│   │   │   └── OrderServiceApplication.java   ← main entry point
│   │   └── resources/
│   │       ├── application.properties          ← configuration file
│   │       ├── static/                          ← static web assets (CSS/JS)
│   │       └── templates/                       ← server-rendered HTML templates (if any)
│   └── test/
│       └── java/com/bharath/orderservice/
│           └── OrderServiceApplicationTests.java
├── pom.xml (Maven) or build.gradle (Gradle)
└── mvnw / mvnw.cmd (Maven wrapper — lets others run the project without installing Maven)
```

**The main class (every Spring Boot app has exactly one of these):**

```java
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

**`@SpringBootApplication` — worth understanding what it actually is (a common "what does this annotation do" question):** it's a **combination of three annotations**:

```java
@SpringBootConfiguration   // marks this as a configuration class (specialized @Configuration)
@EnableAutoConfiguration   // tells Spring Boot to auto-configure beans based on dependencies on the classpath
@ComponentScan             // tells Spring to scan this package (and sub-packages) for @Component classes
```

This single annotation is why you don't need to manually configure a `DispatcherServlet`, a `DataSource`, or dozens of other beans — Spring Boot's **auto-configuration** looks at what's on your classpath (e.g., "I see `spring-boot-starter-web` — let me set up an embedded Tomcat server and a `DispatcherServlet` automatically") and configures sensible defaults for you.

---

### 3. Starters — dependency bundles

**Definition:** A Spring Boot "starter" is a **pre-packaged bundle of dependencies** for a specific type of functionality — instead of manually finding and adding 10 individual compatible libraries, you add **one** starter dependency and get everything needed for that feature.

```xml
<!-- pom.xml (Maven) -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
```

**Common starters (know what each one brings in — frequently asked):**

| Starter | What it provides |
|---|---|
| `spring-boot-starter-web` | Build REST APIs / web apps — includes embedded Tomcat, Spring MVC, Jackson (JSON) |
| `spring-boot-starter-data-jpa` | Database access via JPA/Hibernate |
| `spring-boot-starter-security` | Authentication & authorization |
| `spring-boot-starter-test` | Testing — includes JUnit, Mockito, AssertJ |
| `spring-boot-starter-actuator` | Production monitoring endpoints (health checks, metrics) |
| `spring-boot-starter-validation` | Bean validation (`@Valid`, `@NotNull`, etc.) |

**Why starters matter (the actual insight to explain):** `spring-boot-starter-web` alone pulls in roughly 20+ individual libraries (Spring MVC, Jackson, embedded Tomcat, validation libraries, etc.) — all at **versions guaranteed to be compatible with each other**. Without starters, you'd have to hunt down each library and manually verify version compatibility — a notorious pain point in the pre-Spring-Boot era, often called "dependency hell."

---

### 4. `application.properties` (and `.yml`) — configuration

**Definition:** `application.properties` (or `application.yml`) is where you configure your Spring Boot application's settings — database connections, server port, logging levels, custom app-specific values — without touching Java code.

```properties
# application.properties

# Server configuration
server.port=8081

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/orderdb
spring.datasource.username=root
spring.datasource.password=secret

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.org.springframework=INFO
logging.level.com.bharath=DEBUG

# Custom application property
app.notification.enabled=true
```

**Same config in YAML format (`application.yml` — an alternative, more structured syntax):**

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/orderdb
    username: root
    password: secret
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

app:
  notification:
    enabled: true
```

**`.properties` vs `.yml` — a common quick question:** functionally equivalent — `.yml` is more readable for deeply nested configuration (avoids repeating prefixes like `spring.datasource.` on every line) but is whitespace-sensitive (indentation errors break it). Most teams pick one and stick with it; either is acceptable to use.

#### Reading custom properties in your code

```java
@Component
class NotificationService {
    @Value("${app.notification.enabled}")
    private boolean notificationsEnabled;
}
```

`@Value` injects a single property value directly into a field.

**Better approach for multiple related properties — `@ConfigurationProperties` (a nice thing to mention as a follow-up):**

```java
@Component
@ConfigurationProperties(prefix = "app.notification")
class NotificationConfig {
    private boolean enabled;
    private String defaultSender;
    // getters/setters
}
```

This binds an entire group of properties (`app.notification.*`) into one strongly-typed object, instead of scattering `@Value` annotations everywhere — cleaner for larger configuration blocks.

#### Profile-specific configuration (worth knowing exists)

```
application.properties          ← common/default settings
application-dev.properties      ← dev-only overrides
application-prod.properties     ← production-only overrides
```

```properties
# activate a profile
spring.profiles.active=dev
```

**Why this matters:** lets you use different database URLs, logging levels, etc. for local development vs. production — without changing code, just switching which profile is active.
