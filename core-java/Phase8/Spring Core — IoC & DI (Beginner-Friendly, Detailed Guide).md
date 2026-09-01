## Spring Core — IoC & DI (Beginner-Friendly, Detailed Guide)

Welcome to Phase 8! This is a big shift from DSA into framework knowledge — but don't worry, Spring's core idea is actually simple once you see it with the right picture. Let's build it up slowly.

---

### Part 1: The Problem Spring Solves (start here, before any jargon)

Imagine you're building a food delivery app. You have a `OrderService` class that needs to send notifications, so it needs a `NotificationService`.

**The "normal" way you'd probably write this first:**

java

```
class OrderService {
    private NotificationService notificationService = new NotificationService();
    // OrderService is directly creating its own dependency
}
```

This looks fine... until your app grows. What if `NotificationService` itself needs a `SmsProvider` and an `EmailProvider`? What if you want to swap `NotificationService` for a `MockNotificationService` during testing? What if 50 other classes also need a `NotificationService` — do you create 50 separate instances?

**The core problem:** when a class creates its own dependencies using `new`, it becomes **tightly coupled** to that specific implementation. It's hard to test, hard to swap, and hard to manage at scale.

**Spring's big idea:** instead of each class creating what it needs, hand that responsibility over to a **container** — a central manager that creates objects and hands them to whoever needs them, already wired up and ready to use.

That's really the whole concept. Everything else in this topic is just *how* Spring implements that idea.

---

### Part 2: IoC — Inversion of Control (the concept)

#### Simple picture first

Think about a restaurant kitchen.

- **Without IoC:** every chef walks to the storeroom, picks their own ingredients, chops them, and prepares everything from scratch themselves, every single time.
- **With IoC:** there's a **kitchen manager** who already has all ingredients prepped, organized, and ready. When a chef needs tomatoes, they just ask the manager — they don't go source and prep tomatoes themselves.

**Definition:** Inversion of Control (IoC) is a design principle where the **control of creating and managing objects is transferred from your code to a container/framework**. Instead of your class saying "I will create what I need" (`new SomeClass()`), you say "I need *something* — please give it to me," and the framework figures out how to provide it.

The word "inversion" refers to exactly this flip: normally, *your code* controls object creation. With IoC, *the framework* controls it — control is inverted.

In Spring, this container is called the **ApplicationContext** (we'll get to it in detail below).

---

### Part 3: DI — Dependency Injection (how IoC is actually implemented)

**Definition:** Dependency Injection is the specific *technique* Spring uses to achieve IoC — dependencies (objects a class needs) are **"injected" into a class from the outside**, rather than the class creating them itself.

**Important distinction to remember for interviews:** IoC is the *principle* (the "what"). DI is the *implementation* (the "how"). You'll often hear people use them interchangeably, but technically DI is one way to achieve IoC.

#### The same example, now with DI

java

```
class OrderService {
    private NotificationService notificationService;

    // Spring "injects" the NotificationService here — OrderService never calls 'new'
    OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

Notice: `OrderService` no longer creates `NotificationService` itself. It just declares "I need one of these" (via the constructor), and something external hands it over, fully ready to use.

#### Three ways to do Dependency Injection in Spring

java

```
// 1. Constructor Injection (RECOMMENDED — most commonly used in real projects)
@Component
class OrderService {
    private final NotificationService notificationService;

    @Autowired   // optional here if there's only ONE constructor (Spring 4.3+)
    OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}

// 2. Setter Injection
@Component
class OrderService {
    private NotificationService notificationService;

    @Autowired
    void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}

// 3. Field Injection (easiest to write, but NOT recommended — explained below)
@Component
class OrderService {
    @Autowired
    private NotificationService notificationService;
}
```

**Why Constructor Injection is preferred (a very common interview question):**

1. **Immutability** — you can mark the field `final`, since it's set once in the constructor and never changed. Field injection can't do this.
2. **Testability** — you can create an `OrderService` in a unit test just by calling `new OrderService(mockNotificationService)`, without needing Spring at all.
3. **Fails fast** — if a required dependency is missing, the app won't even start (constructor can't be called), rather than crashing later with a confusing `NullPointerException` when the field is actually used.

---

### Part 4: `@Component` — telling Spring "manage this class for me"

**Definition:** `@Component` is an annotation that marks a class as a **Spring-managed bean** — meaning Spring will automatically create an instance of it and keep it in its container, ready to be injected wherever needed.

java

```
@Component
class NotificationService {
    void send(String message) {
        System.out.println("Sending: " + message);
    }
}
```

**What "bean" means (a term you'll see constantly in Spring):** a **bean** is simply an object that Spring creates, configures, and manages on your behalf. Once a class is annotated `@Component`, Spring will find it, create it, and store it in the container as a "bean" — ready to hand out wherever it's needed.

**How does Spring "find" it?** Through **component scanning** — during startup, Spring scans your project packages looking for classes annotated with `@Component` (and related annotations) and registers them automatically. You don't manually list every class Spring should manage.

**Related "stereotype" annotations (specialized versions of `@Component`, good to know they exist):**

java

```
@Service     // marks a service-layer class (business logic) — functionally identical to @Component, but communicates intent
@Repository  // marks a data-access layer class (talks to the database) — also adds automatic exception translation
@Controller  // marks a web layer class (handles HTTP requests)
```

**Interview point:** all of these are technically `@Component` under the hood (they're meta-annotated with `@Component`) — they exist purely to make code more **readable and self-documenting**, signaling *what role* a class plays in your application's architecture.

---

### Part 5: `@Autowired` — telling Spring "inject a dependency here"

**Definition:** `@Autowired` tells Spring: "find a matching bean in the container and inject it here automatically" — you don't write the wiring code yourself.

java

```
@Component
class OrderService {
    private final NotificationService notificationService;

    @Autowired
    OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

When Spring creates `OrderService`, it sees the constructor needs a `NotificationService`. It looks in its container, finds the `NotificationService` bean (because it was marked `@Component`), and passes it in automatically. You never wrote `new NotificationService()` anywhere — Spring handled the entire wiring.

#### How Spring matches the right bean — "by type" (very important, commonly tested)

By default, `@Autowired` matches **by type**. If there's exactly one bean of type `NotificationService` in the container, Spring injects it. No ambiguity.

**The tricky case — multiple beans of the same type (a classic interview question):**

java

```
interface PaymentService { void pay(); }

@Component
class CreditCardPayment implements PaymentService { public void pay() { } }

@Component
class UpiPayment implements PaymentService { public void pay() { } }

@Component
class OrderService {
    @Autowired
    private PaymentService paymentService;   // AMBIGUOUS! Which one does Spring inject?
}
```

This throws `NoUniqueBeanDefinitionException` at startup — Spring doesn't know which `PaymentService` you want. **Two ways to fix this:**

java

```
// Fix 1: @Qualifier — explicitly specify which bean by name
@Component
class OrderService {
    @Autowired
    @Qualifier("upiPayment")   // bean name defaults to class name, lowercase first letter
    private PaymentService paymentService;
}

// Fix 2: @Primary — mark one implementation as the "default choice"
@Component
@Primary
class CreditCardPayment implements PaymentService { public void pay() { } }
```

---

### Part 6: `@Bean` — for when you don't own the class

**Definition:** `@Bean` is used inside a `@Configuration` class to **manually define a bean**, typically for classes you didn't write (third-party libraries) where you can't just add `@Component` to their source code.

java

```
@Configuration
class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();   // a third-party class — you can't annotate its source with @Component
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());   // custom setup logic before it becomes a bean
        return mapper;
    }
}
```

**`@Component` vs `@Bean` — one of the most common interview questions in this topic:**

`@Component``@Bean`Placed onA class you wroteA method inside a `@Configuration` classUse caseYour own classesThird-party classes, or when you need custom construction logicDiscoveryAutomatic (component scanning)Explicit (you write the method yourself)Control over creationSpring calls the constructor directlyYou write the exact code that builds and returns the object

**Simple rule of thumb to remember:** *"Can I add an annotation directly to this class's source code? Use `@Component`. Can't touch the source (it's from a library), or need custom setup logic? Use `@Bean`."*

---

### Part 7: `ApplicationContext` — the container itself

**Definition:** `ApplicationContext` is Spring's **IoC container** — the central object responsible for creating beans, wiring their dependencies together, and managing their entire lifecycle (creation → use → destruction).

Going back to our kitchen analogy: if `@Component`/`@Bean` are "ingredients registered as available," `ApplicationContext` is the **kitchen manager itself** — the thing actually doing the work of creating, storing, and handing out those ingredients.

java

```
public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(MyApp.class, args);

    // Manually fetching a bean (rare in real apps — usually @Autowired does this for you)
    OrderService orderService = context.getBean(OrderService.class);
}
```

**What `ApplicationContext` actually does, step by step, when your app starts:**

1. **Scans** your packages for `@Component`-annotated classes (and processes `@Configuration` classes for `@Bean` methods).
2. **Creates** instances of all these classes (beans).
3. **Resolves dependencies** — figures out what each bean needs (via `@Autowired`) and injects them in the correct order.
4. **Manages the bean lifecycle** — by default, beans are **singletons** (only ONE instance per bean is created and shared everywhere it's needed, unless you configure otherwise).

**Interview point — Bean Scope (`singleton` is the default, worth knowing):**

java

```
@Component
@Scope("singleton")   // DEFAULT — one shared instance for the entire application
class NotificationService { }

@Component
@Scope("prototype")   // a NEW instance created every time it's requested/injected
class ShoppingCart { }
```

**Why singleton is the default and usually what you want:** most Spring beans (services, repositories) are stateless — they don't hold per-request data, so sharing one instance everywhere is efficient (no repeated object creation) and safe.

---

### Part 8: Putting It All Together — A Full Mini Example

java

```
// The dependency
@Component
class NotificationService {
    void send(String msg) {
        System.out.println("Notification sent: " + msg);
    }
}

// The class that depends on it
@Component
class OrderService {
    private final NotificationService notificationService;

    @Autowired
    OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    void placeOrder(String item) {
        System.out.println("Order placed for: " + item);
        notificationService.send("Your order for " + item + " is confirmed!");
    }
}

// A third-party bean, manually configured
@Configuration
class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// Entry point
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MyApp.class, args);
        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder("Laptop");
    }
}
```

**What happens when this runs, in plain words:**

1. Spring starts up and scans for `@Component` classes — finds `NotificationService` and `OrderService`.
2. It also finds `AppConfig` (a `@Configuration` class) and runs its `@Bean` method to create a `RestTemplate` bean.
3. It creates a `NotificationService` bean first (no dependencies needed).
4. It creates `OrderService`, and since its constructor needs a `NotificationService`, Spring automatically injects the one it already created in step 3.
5. Your `main()` method asks the container for the `OrderService` bean — fully built, dependencies and all — and calls `placeOrder()`.

You, the developer, never wrote a single `new NotificationService()` or `new OrderService(...)` — Spring handled all of it.
