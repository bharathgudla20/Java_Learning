# Stockroom

A full-stack inventory and order-management application by Bharath Gudla. Java 21 + Spring Boot 4.1.2 + Spring MVC + Spring Data JPA/Hibernate + Spring Security; responsive HTML/CSS/JavaScript frontend served by the same application.

## The problem

A small retailer needs to know what is on hand, place multi-product orders without overselling, and handle cancellations without losing track of inventory. Stockroom puts those operations in one workspace, with a dashboard, searchable inventory, order fulfillment, and an activity log.

## Try the app

Open the application and select **Explore live demo**. A new isolated workspace is created with 12 sample products and 14 example orders. Each visitor owns a different dataset; changing your demo does not change another visitor's inventory. Demo workspaces expire after two hours and are cleaned up periodically. The browser session expires after one hour of inactivity.

Sign-up and sign-in are also implemented. In the hosted free demo, **all data, including accounts, resets on server restart**. Use sample information. The banner in the app explains this. Local development uses a persistent H2 file database. An external PostgreSQL datasource can be configured with environment variables.

## What works

- Dashboard calculated from stored products and orders; seven-day UTC revenue chart, reorder alerts, retail stock value, and recent activity.
- Inventory creation; unique SKU per workspace; category/search/low-stock filtering; server-side pagination; stock adjustments with reasons; soft archival.
- Multi-item orders, server-side price calculation, price snapshots, stock checks, and atomic stock deduction.
- Idempotency keys: retries of the same order return the same order. A reused key with different input returns 409.
- State transitions: PLACED → SHIPPED or CANCELLED. Cancellation restores stock exactly once. Shipped/cancelled orders cannot switch to another terminal state.
- Session authentication, BCrypt password hashing, CSRF protection, secure production session cookies, same-origin frontend/API, and owner-scoped repository queries.
- Bean Validation, structured error responses, Actuator health endpoint, Docker build, CI, and integration tests.

## Run in VS Code on Windows

1. Install JDK 21 and VS Code's Extension Pack for Java.
2. Open **this folder**, the one containing `pom.xml`.
3. The included `.vscode/settings.json` points to this laptop's `C:\Program Files\Java\jdk-21.0.11`. Change that path on another machine. No global Java settings are modified.
4. In a fresh PowerShell terminal:

```powershell
./mvnw.cmd spring-boot:run
```

5. Open <http://localhost:8081>. Port 8081 avoids colliding with the earlier greeting project on port 8080.
6. Stop with **Ctrl+C** before running another instance. Alternatively use **Terminal → Run Task → Run Stockroom** or the **Run Stockroom** debug configuration.

On macOS/Linux, run `sh mvnw spring-boot:run` with JDK 21.

## Test and package

```powershell
./mvnw.cmd test
./mvnw.cmd verify
java -jar target/stockroom-1.0.0.jar
```

The integration suite checks real JPA transactions, simultaneous order placement, duplicate requests, stock rollback, cancellation, tenant isolation, authentication, CSRF, validation, password hashing, search, archival and demo reset. It runs on H2. PostgreSQL behavior is **not** claimed to be integration-tested by this suite.

The Dockerfile runs `mvn verify` before packaging, uses a separate JRE image, runs as a non-root user, and starts Java directly so shutdown signals reach it.

## Architecture

```text
HTML/CSS/JavaScript browser application
            │ same-origin JSON requests + session cookie + CSRF token
Spring Security filter chain
            │
AuthController / WorkspaceController  ← request and response DTOs
            │
AccountService / StockroomService     ← validation, business rules, transactions
            │
Spring Data repositories             ← owner-scoped queries and database locks
            │
Hibernate → H2 (local/demo) or configured PostgreSQL
```

Read [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for concurrency, tradeoffs, data model, and security decisions. Read [docs/LEARNING_GUIDE.md](docs/LEARNING_GUIDE.md) to learn the code in small steps. The app also serves `/guide.html` and an OpenAPI description at `/api-docs.json`.

## Deployment

The free portfolio service uses `SPRING_PROFILES_ACTIVE=demo`, giving each visitor a temporary, seeded workspace. No payment service or email provider is used. This is an interactive portfolio demonstration, not a production retailer's system.

Deploy as one Docker web service, using the repository root as the Docker context. Set:

```text
SPRING_PROFILES_ACTIVE=demo
```

Render supplies `PORT`; the app binds to `0.0.0.0`. Configure HTTP health checks at `/actuator/health`. Keep HTTPS enabled; the demo profile marks session cookies Secure. A free Render instance sleeps after inactivity, so the first visit may take about a minute. Hosting limits can change; consult the current provider documentation.

To use persistent PostgreSQL instead of the demo profile, **do not set the demo profile**. Set `JDBC_DATABASE_URL` to a JDBC PostgreSQL URL, plus `DB_USER` and `DB_PASSWORD`; set secure cookies for HTTPS and `EPHEMERAL_DEMO=false`. Secrets belong in the deployment dashboard, not source control. Before supporting real customer data, add managed schema migrations, backups, password reset/email verification, stronger abuse protection, and PostgreSQL-specific integration tests. Current schema management uses Hibernate `update` for learning convenience.

## Useful resume wording

> Built and deployed Stockroom, a full-stack inventory and order-management application using Java 21, Spring Boot, Spring Security, JPA/Hibernate, and JavaScript; implemented transactional stock updates, idempotent order creation, workspace isolation, and automated integration tests.

Only add the live link after its deployment has been verified. Do not claim real users, revenue, measured performance improvements, production scale, or PostgreSQL deployment unless you have that evidence. Be ready to explain and change the code yourself.

## Current boundaries

- No payment processing, external shipping integration, tax calculation, email delivery, or multi-user teams.
- Revenue is the sum of non-cancelled order totals, not settled payments or accounting revenue. Stock value is retail value, not cost basis.
- All prices are INR. The dashboard groups days in UTC.
- Whole-workspace pessimistic locking favors correctness and simple reasoning over high throughput. It is appropriate for this small portfolio workload, not a benchmarked scaling design.
- Workspaces are capped at 200 products and 300 orders; list-orders is bounded by that cap. Inventory pages contain eight products.
- Same-process HTTP sessions mean a restart signs users out. Horizontal scaling requires shared sessions or another session strategy.
- Public auth endpoints have a basic process-local rate limit. This is not distributed bot protection.

## Sources used for platform behavior

- [Spring Security CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html)
- [Render deployments](https://render.com/docs/deploys)
- [Render health checks](https://render.com/docs/health-checks)
- [Render free service limits](https://render.com/docs/free)
