# Learn Stockroom and defend it in an interview

Work through one stage at a time. Explain the request flow aloud before copying or changing code. A useful project is one you can debug and extend without assistance.

## Stage 1 — Run it and follow a request

Open the folder in VS Code, run `./mvnw.cmd spring-boot:run`, visit localhost:8081 and enter a demo workspace. Open the browser Network panel. Find `/api/overview` and compare its JSON to the dashboard.

Read: `StockroomApplication`, `WorkspaceController`, `Contracts.Overview`.

Exercise: add a response field reporting the number of shipped orders. Update its DTO, service calculation, and frontend display.

Quiz: What starts the application? What is a REST controller? Who converts Java records into JSON? How does an HTTP request differ from a Java method call?

## Stage 2 — Spring Core and dependency injection

Read the constructors of `WorkspaceController` and `StockroomService`. Spring provides their dependencies; the controller does not call `new StockroomService(...)`.

Exercise: trace how `ProductRepository` reaches the service. Draw the bean dependency graph. Explain the difference between `@Service`, `@RestController`, and `@Bean` without saying they are all identical.

Quiz: What is a bean? How does constructor injection help tests? Why does a single constructor not need `@Autowired`? What does component scanning discover?

## Stage 3 — HTTP, DTOs and validation

Read `ProductInput`, `ProductView`, and the create-product endpoint. The input describes accepted values; the output describes exposed values. The entity describes database storage.

Exercise: add a maximum-length optional product description, keeping request, storage, and response concerns separate.

Quiz: When do we use 201, 400, 401, 403, 404 and 409? Why use a UUID path variable? Does a DTO perform database queries? Why is `BigDecimal` useful for money?

## Stage 4 — JPA and database queries

Read `Product`, `ProductRepository.search` and the product-list service. Learn `@Entity`, `@Id`, `@Column`, `@Version`, JPQL, `Pageable`, and transactions. Compare a repository-derived method name to the custom JPQL query.

Exercise: add a category to the seed data, then verify the filter and pagination. Explain why user search wildcard characters are escaped.

Quiz: JPA vs Hibernate? Entity vs DTO? `save` vs `flush`? What is dirty checking? Why turn off open-in-view? What changes when a database is external?

## Stage 5 — Transactions and correctness

Read `placeOrder`, `changeStatus`, and the concurrent-order tests. This is the strongest interview discussion in the project.

Exercise: set a product stock to one and have two clients order it simultaneously. One succeeds, one gets 409, final stock is zero. Run the automated concurrency test rather than relying only on manual timing.

Quiz: What would break without a transaction? What does pessimistic locking do? Why is Java `synchronized` insufficient across replicas? Why is an idempotency key scoped to the owner? Why reject a reused key with different input? Why snapshot prices?

## Stage 6 — Security and exception handling

Read `SecurityConfig`, `AuthController`, `AccountService`, `ApiErrors`, and the isolation/CSRF tests. Inspect the session cookie and CSRF request header in browser developer tools.

Exercise: attempt to patch another workspace's product ID and explain the 404. Remove the CSRF header in a test and explain the 403. Never disable CSRF simply to make a cookie-authenticated request succeed.

Quiz: Authentication vs authorization? Hashing vs encryption? Why BCrypt? Why rotate the session ID? Why not JWT here? What does HttpOnly prevent, and what does it not prevent? Why do parameterized queries and HTML escaping solve different problems?

## Stage 7 — Deploy and explain tradeoffs

Read the Dockerfile, CI workflow, application profiles, PostgreSQL migration and deployment notes. Explain why registered workspaces persist in PostgreSQL, why sample demos expire, and why a server restart can require signing in again without deleting business data.

Exercise: add one meaningful regression test, run `verify`, commit it, and observe the deployment. Practice tracing a startup failure from the first meaningful log error.

Quiz: What does a health check prove? Why must a container listen on `0.0.0.0`? Why use the host's `PORT`? Why does closing the browser not stop the server? Why does the free instance have a cold start?

## A five-minute interviewer demo

1. Explain the retailer's problem and enter a fresh demo workspace.
2. Show dashboard values and low-stock filtering.
3. Add a new SKU, then restock it with a reason.
4. Place a two-item order; show stock decreases and the new activity.
5. Cancel it; show stock returns exactly once.
6. Show the transaction/idempotency code and the concurrent overselling test.
7. State one limitation honestly: free-hosting cold starts, whole-workspace locking, or missing production account recovery. Explain the difference between a temporary sample workspace and a registered PostgreSQL workspace.

## Questions worth rehearsing

- Walk through one order from button click to database commit.
- Why did you choose a monolith instead of microservices?
- How would you support ten thousand products and millions of orders?
- What if the server commits the order but the browser loses the response?
- What if a cancellation races with shipment?
- What prevents one user reading another user's data?
- How would you migrate from H2 to PostgreSQL safely?
- What would you measure before optimizing performance?

Do not memorize a claim of scale. Identify the measured bottleneck, explain the change you would make, and acknowledge what has not been tested.

## Self-check answer hints

DI: the container supplies dependencies. DTO: the request/response data contract. Transaction: changes commit together or roll back together. Pessimistic lock: conflicting database transactions wait. Idempotency: retrying the same request does not apply its effect twice. Isolation: ownership comes from the authenticated session. CSRF: protects unsafe cookie-authenticated requests from cross-site submission. 409: a conflict with current application state. A health check is a readiness signal, not proof of every business workflow.
