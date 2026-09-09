# Phase 8, Topic 5: Exception Handling in Spring Boot — `@ControllerAdvice`, `@ExceptionHandler`, Error Responses

This topic is about turning messy Java exceptions into clean, consistent API error responses — a topic that's often underestimated but heavily valued in real production code reviews and system design discussions.

---

## Why this matters

A REST API that returns raw stack traces or inconsistent error formats is a red flag in interviews and in production. Interviewers want to see that you understand how to build a **centralized, consistent error-handling strategy** — not scattered `try-catch` blocks everywhere.

---

## 1. The Problem — Handling Exceptions Without Global Handling

**Without any exception handling**, if something goes wrong in your controller, Spring Boot returns a default, unhelpful, ugly error response:

```json
{
  "timestamp": "2026-09-09T10:15:30.00+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/orders/999"
}
```

This tells the client almost nothing useful — no indication of *why* it failed (was it a bad request? Not found? Server bug?), and it leaks internal details in some configurations (like stack traces, in dev mode).

### The naive fix — `try-catch` in every controller method

```java
@GetMapping("/{id}")
public ResponseEntity<?> getOrder(@PathVariable Long id) {
    try {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    } catch (OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
```

This approach does not scale. If you have 50 controller methods across 10 controllers, and each needs to handle "order not found," "invalid input," "unauthorized," and similar errors, you duplicate the same `try-catch` blocks everywhere. Error handling logic becomes scattered, inconsistent, and hard to maintain.

### Spring's solution

Centralize all exception handling in **one place** using `@ControllerAdvice` and `@ExceptionHandler`. Write the handling logic once, and it automatically applies to every controller in your application.

---

## 2. Custom Exceptions — the building block

Create your own exception classes for specific business error scenarios instead of relying on generic exceptions like `RuntimeException`.

```java
class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}

class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message);
    }
}
```

### Why extend `RuntimeException`?

Checked exceptions force every calling method to either catch them or declare `throws`, which gets noisy fast across a large codebase. `RuntimeException` is unchecked, so it does not have this requirement. Since these exceptions are meant to be caught centrally by `@ExceptionHandler` rather than handled locally, unchecked exceptions fit this pattern better.

### Using a custom exception in the service layer

```java
@Service
class OrderService {
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
}
```

The service layer just throws the exception. It does not know or care how the exception will eventually be turned into an HTTP response. That is a clean separation of concerns.

---

## 3. `@ExceptionHandler` — handling exceptions at the method level

`@ExceptionHandler` marks a method as the handler for a specific exception type. When that exception is thrown from a controller, Spring routes it to this method instead of allowing it to propagate as a raw error.

```java
@RestController
@RequestMapping("/api/orders")
class OrderController {

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);   // may throw OrderNotFoundException
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

If `getOrder()` throws `OrderNotFoundException`, Spring catches it automatically and routes it to `handleNotFound()`. You do not need to write a `try-catch` in `getOrder()` itself.

### Limitation

An `@ExceptionHandler` defined inside a controller only handles exceptions thrown within that same controller. If you have 10 controllers that need the same `OrderNotFoundException` handling, you would have to duplicate this method 10 times.

---

## 4. `@ControllerAdvice` — making it global

`@ControllerAdvice` marks a class as a **global exception handler**. `@ExceptionHandler` methods defined inside it apply to every controller in the application, not just one.

```java
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrder(InvalidOrderException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Catch-all fallback for anything not specifically handled
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

This one class now handles exceptions from every controller in your application — `OrderController`, `CustomerController`, `PaymentController`, and all others — without requiring controller-specific `@ExceptionHandler` methods or `try-catch` blocks.

### `@ControllerAdvice` vs `@RestControllerAdvice`

```java
@RestControllerAdvice   // = @ControllerAdvice + @ResponseBody
class GlobalExceptionHandler { }
```

The relationship is similar to `@RestController = @Controller + @ResponseBody`. If your handlers return data directly rather than view names, which is almost always the case for REST APIs, use `@RestControllerAdvice`. Then you do not need to add `@ResponseBody` to every handler method.

### Handler matching — most specific wins

If you throw `OrderNotFoundException` and have handlers for both `OrderNotFoundException` and the generic `Exception`, Spring picks the most specific matching type: `handleOrderNotFound()`, not the generic fallback.

---

## 5. Building a Consistent Error Response Structure

A standard error response shape ensures that clients always know what fields to expect regardless of which error occurred.

```java
class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;   // optional — which endpoint failed

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    // getters/setters
}
```

### Resulting JSON response

```json
{
  "status": 404,
  "message": "Order not found with id: 999",
  "timestamp": "2026-09-09T10:15:30"
}
```

### Why consistency matters

Frontend and mobile clients consuming your API can write one generic error-handling function that reads `status` and `message` from every failed response. They do not need custom parsing logic for every endpoint because each one returns errors in a different format. This is a real production concern, not just a nice-to-have.

---

## 6. Handling Validation Errors (`@Valid` failures)

Recall from Topic 3: `@Valid @RequestBody OrderDTO orderDTO` triggers validation. When validation fails, Spring throws `MethodArgumentNotValidException`. This exception is worth handling specifically because it contains field-level error details.

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationErrors(
        MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );
    return ResponseEntity.badRequest().body(errors);
}
```
