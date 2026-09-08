Phase 8, Topic 4: **Spring Data JPA & Hibernate Basics** — `@Entity`, `@Repository`, JPQL, save/find/delete. This is where your app finally talks to a real database — and it's one of the most hands-on-coding-heavy topics in interviews, since almost every backend task involves persisting data.

### Why this matters

Nearly every product-company backend interview eventually touches "how do you save/fetch data from a database in Spring Boot?" JPA and Hibernate let you work with **Java objects** instead of writing raw SQL for every operation — but understanding what's happening underneath is exactly what separates "I copy-pasted this" from "I understand this."

---

### 1. The Big Picture — JPA vs Hibernate (clear this up first, always confused)

**JPA (Java Persistence API)** is a **specification** — a set of interfaces and rules describing how Java objects should map to database tables. It's just a contract; it has no actual implementation.

**Hibernate** is the most popular **implementation** of that JPA specification — the actual code that does the work of translating your Java objects into SQL and back.

**Analogy:** JPA is like an electrical socket standard (defines the shape/rules). Hibernate is an actual manufacturer making plugs that fit that standard. Spring Data JPA sits on top of both, giving you an even simpler way to use them.

```
Your Code → Spring Data JPA (convenience layer) → JPA (specification) → Hibernate (implementation) → Database
```

**Object-Relational Mapping (ORM) — the core concept these all serve:** ORM is the technique of mapping Java **objects** to database **tables**, so you interact with your data as regular Java objects (`Order`, `Customer`) instead of writing SQL strings everywhere.

---

### 2. `@Entity` — mapping a class to a database table

**Definition:** `@Entity` marks a Java class as a **JPA entity** — meaning Hibernate will treat it as a representation of a database table, with each instance representing one row.

java

```
@Entity
@Table(name = "orders")   // optional — defaults to the class name if omitted
class Order {

    @Id                                          // marks the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // auto-increment, handled by the DB
    private Long id;

    @Column(name = "item_name", nullable = false)   // maps to a specific column, with constraints
    private String item;

    private int quantity;   // no @Column needed — defaults to field name "quantity"

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters, setters
    public Order() { }   // JPA REQUIRES a no-arg constructor
}
```

**Key annotations to know:**

AnnotationPurpose`@Entity`Marks class as mapped to a table`@Table(name = "...")`Customizes table name (optional)`@Id`Marks the primary key field`@GeneratedValue`Auto-generates the ID (e.g., auto-increment)`@Column`Customizes column name/constraints (optional if defaults are fine)

**Interview trap — why a no-arg constructor is required:** Hibernate creates entity instances internally using **reflection** (it needs to instantiate the object first, *then* populate fields from the database row) — without a no-arg constructor, Hibernate has no way to create the empty object to populate.

#### Relationships between entities (good to know, commonly asked)

java

```
@Entity
class Order {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne                        // many Orders belong to one Customer
    @JoinColumn(name = "customer_id")
    private Customer customer;
}

@Entity
class Customer {
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "customer")  // one Customer has many Orders
    private List<Order> orders;
}
```

`@OneToMany`, `@ManyToOne`, `@OneToOne`, `@ManyToMany` — these map relational foreign keys into navigable Java object references.

---

### 3. `@Repository` — the data access layer

**Definition:** `@Repository` marks a class/interface as belonging to the **persistence layer** — responsible for talking to the database. In Spring Data JPA, you usually don't write implementation code at all — you just declare an **interface**, and Spring generates the implementation automatically at runtime.

java

```
interface OrderRepository extends JpaRepository<Order, Long> {
    // That's it. No implementation needed!
}
```

**What `JpaRepository<Order, Long>` gives you for free (this is the "magic" interviewers want you to explain, not just use):**

- `Order` = the entity type this repository manages
- `Long` = the type of that entity's primary key (`@Id` field)

Just by extending this interface, you automatically get methods like:

java

```
orderRepository.save(order);            // INSERT or UPDATE
orderRepository.findById(5L);            // SELECT by primary key
orderRepository.findAll();               // SELECT all rows
orderRepository.deleteById(5L);          // DELETE by primary key
orderRepository.count();                 // SELECT COUNT(*)
orderRepository.existsById(5L);          // check existence
```

**How does Spring actually implement this interface with no code written (the key "how does this magic work" question)?** At startup, Spring Data JPA uses a **dynamic proxy** — it generates an actual implementing class **at runtime** based on the interface's method names and generic types, and wires that proxy as the bean. You never see or write this generated class yourself.

#### Derived Query Methods — an even bigger "magic" feature

You can define custom finder methods just by naming them correctly — Spring **parses the method name itself** to build the query:

java

```
interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByItem(String item);                        // WHERE item = ?
    List<Order> findByQuantityGreaterThan(int quantity);         // WHERE quantity > ?
    List<Order> findByItemAndQuantity(String item, int qty);     // WHERE item = ? AND quantity = ?
    List<Order> findByItemContaining(String keyword);            // WHERE item LIKE %?%
    List<Order> findByCreatedAtAfter(LocalDateTime date);        // WHERE created_at > ?
    Optional<Order> findFirstByOrderByCreatedAtDesc();           // ORDER BY created_at DESC LIMIT 1
}
```

**Why this works (explain the mechanism, not just "it's magic"):** Spring Data JPA parses the method name at startup — it recognizes keywords like `findBy`, `And`, `GreaterThan`, `Containing`, `OrderBy` — and translates them into the equivalent SQL/JPQL query automatically. This is genuinely one of Spring's most powerful productivity features, but interviewers want to hear that you know it's **name-driven query generation**, not literal magic.

---

### 4. JPQL — Java Persistence Query Language

**Definition:** JPQL is a query language similar to SQL, but it operates on **entity objects and their fields**, not database tables and columns directly — it's database-agnostic (Hibernate translates it into the actual SQL for whatever database you're using).

java

```
interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.item = :item")
    List<Order> findOrdersByItemCustom(@Param("item") String item);

    @Query("SELECT o FROM Order o WHERE o.quantity > :qty AND o.item LIKE %:keyword%")
    List<Order> searchOrders(@Param("qty") int qty, @Param("keyword") String keyword);

    @Query(value = "SELECT * FROM orders WHERE item = ?1", nativeQuery = true)  // raw SQL, if truly needed
    List<Order> findByItemNative(String item);
}
```

**JPQL vs SQL — the key distinction (frequently asked):**

sql

```
-- SQL — refers to actual TABLE and COLUMN names
SELECT * FROM orders WHERE item_name = 'Laptop';

-- JPQL — refers to the ENTITY CLASS and FIELD names
SELECT o FROM Order o WHERE o.item = 'Laptop';
```

Notice: JPQL uses `Order` (the **class name**) and `o.item` (the **field name**), not `orders` (table) and `item_name` (column). This is what makes JPQL portable across different databases — you're querying your Java object model, and Hibernate handles translating it to whatever SQL dialect the underlying database needs.

**When to use derived query methods vs `@Query` (JPQL) vs native SQL — a good practical distinction to raise:**

- **Derived query methods** — best for simple, straightforward conditions (1-3 fields).
- **`@Query` with JPQL** — best when the query is complex (joins, custom projections) but you still want database portability.
- **Native SQL (`nativeQuery = true`)** — only when you need database-specific features JPQL can't express (e.g., a specific PostgreSQL function).

---

### 5. save / find / delete — the CRUD operations in detail

java

```
@Service
class OrderService {
    private final OrderRepository orderRepository;

    OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // CREATE or UPDATE
    public Order createOrder(Order order) {
        return orderRepository.save(order);   // if order.id is null → INSERT; if set → UPDATE
    }

    // READ (single)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // READ (all)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // DELETE
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
```

**Interview trap — `save()` does BOTH insert and update (a very commonly asked "gotcha"):** Hibernate decides based on whether the entity's `@Id` field is `null` (or unset) — `null` ID → treated as a new entity → `INSERT`. Non-null ID that already exists in the DB → treated as existing → `UPDATE`. This surprises people who expect separate `insert()`/`update()` methods.

**Why `findById()` returns `Optional<Order>`, not `Order` directly (worth explaining):** it forces you to explicitly handle the "not found" case instead of risking a silent `NullPointerException` — you must call `.orElseThrow()`, `.orElse()`, or check `.isPresent()` before using the value. This is a deliberate design choice tying back to Java's `Optional` class (good API design to avoid NPEs).

---

### 6. A Few More Important Concepts (good to know for depth)

**Transactions — `@Transactional`:**

java

```
@Service
class OrderService {
    @Transactional
    public void placeOrderAndUpdateInventory(Order order) {
        orderRepository.save(order);
        inventoryService.reduceStock(order.getItem(), order.getQuantity());
        // if EITHER operation fails, BOTH are rolled back — atomicity guaranteed
    }
}
```

**Why this matters:** without `@Transactional`, if `reduceStock()` throws an exception after `save()` already succeeded, you'd have an order saved with no corresponding inventory update — data inconsistency. `@Transactional` wraps the method in a database transaction, ensuring **all-or-nothing** execution.

**Lazy vs Eager Loading (a common follow-up on relationships):**

java

```
@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)   // default for @OneToMany — loads only when accessed
private List<Order> orders;

@ManyToOne(fetch = FetchType.EAGER)   // default for @ManyToOne — loads immediately with the parent
private Customer customer;
```

**Lazy** — related data is fetched **only when you actually access it** (can cause `LazyInitializationException` if accessed outside a transaction/session). **Eager** — related data is fetched **immediately**, alongside the parent entity, even if you never use it (can hurt performance if overused).

---

### Quick Comparison Table

ConceptPurpose`@Entity`Maps a Java class to a database table`@Id` / `@GeneratedValue`Marks and auto-generates the primary key`@Repository` + `JpaRepository`Data access layer, gives free CRUD methodsDerived query methodsAuto-generated queries from method namesJPQL (`@Query`)Entity-based query language, database-agnostic`save()`INSERT (new entity) or UPDATE (existing entity)`findById()`Returns `Optional<T>` — forces null-safety handling`@Transactional`Ensures multiple DB operations succeed or fail together
