Phase 8, Topic 3: **REST APIs with Spring MVC** — `@RestController`, `@GetMapping`, `@RequestBody`, DTOs. This is the most practically important topic in the entire tracker for product companies — REST API design questions come up in nearly every backend interview, both as "explain this annotation" and as live coding rounds.

### Why this matters

Product companies build almost everything as REST APIs internally (microservices talking to each other) and externally (mobile apps talking to backend). Interviewers want to see you understand not just the annotations, but *why* REST conventions exist and how to structure a clean API.

---

### 1. `@RestController` — the entry point for APIs

**Definition:** `@RestController` marks a class as a **REST API controller** — its methods handle incoming HTTP requests and return data directly (usually as JSON), rather than returning a view/HTML page.

java

```
@RestController
@RequestMapping("/api/orders")
class OrderController {
    @GetMapping
    public String hello() {
        return "Hello from OrderController";
    }
}
```

**What `@RestController` actually is (a very common "what's the difference" question):** it's a combination of two annotations:

java

```
@Controller        // marks this as a Spring MVC controller (handles web requests)
@ResponseBody       // tells Spring: don't render a view, just serialize the return value directly into the response body (as JSON, by default)
```

**`@Controller` vs `@RestController` (frequently asked):**

java

```
@Controller
class WebController {
    @GetMapping("/home")
    public String home() {
        return "home";   // Spring looks for a VIEW named "home" (e.g., home.html) to render
    }
}

@RestController
class ApiController {
    @GetMapping("/home")
    public String home() {
        return "home";   // Spring returns the LITERAL STRING "home" as the response body
    }
}
```

`@Controller` is for traditional server-rendered web pages (returns a view name). `@RestController` is for APIs (returns actual data, auto-converted to JSON).

**`@RequestMapping` at the class level:** sets a common base path for all endpoints in that controller — here, every method's path gets prefixed with `/api/orders`.

---

### 2. HTTP Methods & Mapping Annotations

**Definition:** REST APIs use standard HTTP methods to represent different actions on a "resource" (like an order, a user, a product). Spring provides a dedicated annotation for each.

AnnotationHTTP MethodTypical Use`@GetMapping`GETRead/fetch data`@PostMapping`POSTCreate new data`@PutMapping`PUTUpdate/replace existing data (full update)`@PatchMapping`PATCHPartial update`@DeleteMapping`DELETEDelete data

java

```
@RestController
@RequestMapping("/api/orders")
class OrderController {

    @GetMapping                          // GET /api/orders
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")                 // GET /api/orders/5
    public OrderDTO getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping                         // POST /api/orders
    public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @PutMapping("/{id}")                // PUT /api/orders/5
    public OrderDTO updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        return orderService.updateOrder(id, orderDTO);
    }

    @DeleteMapping("/{id}")             // DELETE /api/orders/5
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
```

**Why REST uses specific HTTP methods instead of one generic endpoint (an important conceptual question):** REST is built on the idea that URLs represent **resources** (nouns, like `/orders`), and the HTTP method represents the **action** (verb) on that resource. This keeps APIs predictable and self-documenting — `GET /orders/5` obviously reads order 5; `DELETE /orders/5` obviously deletes it. Compare this to a single endpoint like `POST /doOrderStuff` with an "action" field buried in the body — much less clear and harder to secure/cache properly.

**PUT vs PATCH (commonly confused pair):** `PUT` typically replaces the **entire** resource (you send all fields, even unchanged ones). `PATCH` updates **only the fields you send** — the rest stay untouched.

---

### 3. Path Variables vs Request Parameters vs Request Body

**Three different ways data comes into a request — knowing when to use which is frequently tested:**

java

``` 
// @PathVariable — data embedded IN the URL path itself
@GetMapping("/{id}")
public OrderDTO getOrder(@PathVariable Long id) { }
// URL: GET /api/orders/5   → id = 5

// @RequestParam — data as URL QUERY parameters
@GetMapping
public List<OrderDTO> searchOrders(@RequestParam String status, @RequestParam(defaultValue = "10") int limit) { }
// URL: GET /api/orders?status=SHIPPED&limit=20   → status="SHIPPED", limit=20

// @RequestBody — data in the request BODY (typically JSON), for POST/PUT/PATCH
@PostMapping
public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) { }
// Body: { "item": "Laptop", "quantity": 1 }
```

**Rule of thumb (good to state explicitly):**

- **Path variable** → identifies **which** specific resource (`/orders/5` — order number 5).
- **Request parameter** → optional filters/options for a request (`?status=SHIPPED&limit=20`).
- **Request body** → the actual **data payload** being sent (creating/updating a resource).

---

### 4. `@RequestBody` — deserializing incoming JSON

**Definition:** `@RequestBody` tells Spring to take the incoming HTTP request's JSON body and **automatically convert it into a Java object** — Spring uses the Jackson library under the hood to do this conversion.

java

```
@PostMapping
public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) {
    System.out.println(orderDTO.getItem());   // Spring already parsed the JSON into this object
    return orderService.createOrder(orderDTO);
}
```

If the client sends:

json

```
{ "item": "Laptop", "quantity": 1 }
```

Spring automatically maps `"item"` → `orderDTO.setItem("Laptop")` and `"quantity"` → `orderDTO.setQuantity(1)`, using matching field names. This is called **deserialization** (JSON → Java object). The reverse — converting your returned Java object back into JSON for the response — is called **serialization**, and happens automatically too (that's what `@ResponseBody`, bundled inside `@RestController`, is doing).

**Interview trap — validation:** `@RequestBody` alone doesn't validate the data. You need `@Valid` alongside it:

java

```
@PostMapping
public OrderDTO createOrder(@Valid @RequestBody OrderDTO orderDTO) {
    return orderService.createOrder(orderDTO);
}

class OrderDTO {
    @NotBlank(message = "Item name is required")
    private String item;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
```

Without `@Valid`, invalid data (like a blank item name) would silently pass through to your business logic.

---

### 5. DTOs — Data Transfer Objects

**Definition:** A DTO is a plain object used **specifically to transfer data** between layers (like client ↔ API) — it's deliberately kept separate from your database **Entity** class, even if the fields look similar.

java

``` 
// Entity — represents the database table, used internally
@Entity
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String item;
    private int quantity;
    private String internalWarehouseCode;   // sensitive/internal-only field
    private LocalDateTime createdAt;
    // getters/setters
}

// DTO — represents what the API actually exposes to clients
class OrderDTO {
    private String item;
    private int quantity;
    // NO internalWarehouseCode — clients don't need or shouldn't see this
    // getters/setters
}
```

**Why not just return the Entity directly (the core interview question on this topic):**

1. **Security/data exposure** — Entities often contain sensitive or internal-only fields (internal codes, audit fields, other entities via relationships) that shouldn't be exposed to API clients.
2. **Decoupling** — if you change your database schema (rename a column, restructure a relationship), your API contract doesn't have to break for existing clients, since DTOs are a separate, controlled shape.
3. **Avoiding infinite recursion/lazy-loading issues** — Entities with bidirectional JPA relationships (e.g., `Order` → `Customer` → `List<Order>`) can cause serialization to spiral into infinite loops or trigger lazy-loading exceptions outside a transaction. DTOs sidestep this entirely by only including exactly the fields you choose.
4. **Tailored shapes per use case** — a "list all orders" endpoint might need a lightweight `OrderSummaryDTO`, while "order details" needs a fuller `OrderDetailDTO` — the same Entity can map to different DTOs depending on context.

**Mapping between Entity and DTO (a practical detail worth mentioning):** typically done manually (constructor/builder), or via a mapping library like **MapStruct** or **ModelMapper** in larger projects, to avoid writing repetitive conversion code by hand.

java

``` 
// Simple manual mapping example
OrderDTO toDTO(Order order) {
    OrderDTO dto = new OrderDTO();
    dto.setItem(order.getItem());
    dto.setQuantity(order.getQuantity());
    return dto;
}
```

---

### 6. Response Status Codes — `ResponseEntity`

**Definition:** `ResponseEntity<T>` lets you control the full HTTP response — status code, headers, and body — instead of just returning a plain object (which defaults to `200 OK`).

java

```
@GetMapping("/{id}")
public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
    OrderDTO order = orderService.getOrderById(id);
    if (order == null) {
        return ResponseEntity.notFound().build();          // 404
    }
    return ResponseEntity.ok(order);                        // 200 with body
}

@PostMapping
public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
    OrderDTO created = orderService.createOrder(orderDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);   // 201
}
```

**Common status codes worth knowing cold:** `200 OK` (success), `201 Created` (resource created), `204 No Content` (success, nothing to return — often used for DELETE), `400 Bad Request` (invalid input), `404 Not Found`, `500 Internal Server Error`.

---

### Quick Comparison Table

ConceptPurpose`@RestController`Marks class as an API controller returning data (not views)`@GetMapping`/`@PostMapping`/etc.Maps a method to a specific HTTP method + path`@PathVariable`Extracts values from the URL path (`/orders/{id}`)`@RequestParam`Extracts URL query parameters (`?status=X`)`@RequestBody`Deserializes the JSON request body into a Java objectDTOControlled data shape for API input/output, decoupled from the database Entity`ResponseEntity`Full control over status code, headers, and body
