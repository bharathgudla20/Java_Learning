> **Version 2 update:** This guide originally described the single-user v1 project.
> Registration, session authentication, CSRF protection, and per-user ownership are now
> implemented. Read FREE_DEPLOYMENT.md for the new flow. Use the v2 JAR and register/sign
> in before testing application endpoints. The H2 console is disabled by default. Local
> v1 rows are preserved but not automatically assigned to an account. Future-work mentions
> of adding authentication below describe the original learning sequence, not missing v2 code.

# Learn by following one application

You already studied a lot of Java. The next skill is connecting small pieces into
one working system. Read this project in the order below, and change one thing at a time.

## Lesson 1 — See the whole journey

Run the app using the README. Add one application, then open the browser's Developer
Tools (F12) → Network. Edit the application and click Update.

You should see `PUT /api/applications/1` (your ID may differ), a JSON request,
and a JSON response. A request is the browser asking the server to do something.
A response tells the browser what happened. HTTP is the communication protocol;
JSON is the data format. Neither is the database.

**Exercise:** Explain aloud where each piece lives: form, Java objects, saved rows.
Close the browser and reopen it. Why does your application remain?

## Lesson 2 — HTML, CSS, and JavaScript

Read `static/index.html`. A label names an input. Each input's `name` becomes a key
in `FormData`. The date input improves usability; required/maxlength catch common
mistakes before sending. Client validation can be bypassed, so the server validates too.

Read `static/styles.css`: CSS Grid arranges the dashboard and two-column workspace;
media queries rearrange them on a narrow screen. There is no frontend framework or build step.

Read `static/app.js` in this order:

1. `form.addEventListener('submit', ...)` listens for a form submission.
2. `preventDefault()` stops the browser's usual page reload.
3. `Object.fromEntries(new FormData(form))` collects the form into an object.
4. `JSON.stringify(body)` converts it to JSON text.
5. `api(...)` calls `fetch`, waits for HTTP, and checks `response.ok`.
6. `load()` gets the list and summary; `render()` creates DOM elements.

`async/await` makes asynchronous network code readable. `Promise.all` loads two
independent requests together. `textContent` displays user text without interpreting
it as HTML. `editingId` decides whether Save sends POST or PUT.

**Exercise:** Change the heading and a color. Add a note containing `<b>Hello</b>`.
It should display literally. Explain why `textContent` is useful here.

## Lesson 3 — REST controller and DTO

Read `ApplicationController.java`. `@RestController` makes return values become JSON.
`@RequestMapping` sets a common URL prefix. `@PostMapping` receives creation requests.
`@RequestBody` converts JSON to an `ApplicationRequest` object.
`@Valid` runs the constraints declared in that record.

An `ApplicationRequest` is a DTO (data transfer object). It describes what the user
can submit. It has no ID: a client cannot choose the database-generated ID by posting it.
A Java record is a compact, immutable data carrier; `company()` reads its component.
This uses newer syntax than Java 8, while the project still applies Java 8 streams/lambdas.

201 means created, 200 means success with a response, 204 means success without a body,
400 means invalid request, and 404 means a requested application does not exist.

**Exercise:** Submit an empty company through the PowerShell API example. HTML isn't
involved. Why does the server still return 400? Try an unknown status.

## Lesson 4 — OOP, dependency injection, and service

Read `ApplicationService.java`. It groups operations on applications. The controller
handles HTTP; the service handles application logic; the repository handles persistence.
This separation practices the Single Responsibility Principle from your pending SOLID topics.

The constructor asks for an `ApplicationRepository`. Spring creates the implementation
and supplies it. This is dependency injection: the service receives a collaborator
instead of building it with `new`. You do not need field `@Autowired` for this constructor.

`private final` protects the dependency reference from reassignment. It doesn't make
the repository or its database immutable. Spring services are singleton beans by
default, so avoid storing a current user's application in mutable service fields.

`get(id)` uses `Optional.orElseThrow(() -> ...)`. The lambda creates a custom exception
only when the row is absent. `@Transactional` makes the write operation a transaction:
successful changes commit together, and runtime failures normally cause rollback.

**Exercise:** Put breakpoints in controller `create`, service `create`, and entity
constructor. Submit the form in debug mode. Watch execution move through the layers.

## Lesson 5 — Entity, repository, and SQL

Read `JobApplication.java`. A Java object represents one application. `@Entity` tells
JPA to persist it; `@Table` chooses the table. `@Id` marks its primary key.
`@GeneratedValue` asks the database to generate unique IDs.
Private fields plus getters demonstrate encapsulation. `updateFrom()` controls changes.

`@Enumerated(EnumType.STRING)` stores `INTERVIEW` instead of an enum ordinal such as 1.
Adding an enum value later won't change the meaning of existing stored values.
`LocalDate` represents a calendar date without a time zone.

Read `ApplicationRepository.java`. `JpaRepository<JobApplication, Long>` uses generics
to say which entity and ID type it manages. Its inherited `save`, `findById`, `findAll`,
and `delete` methods avoid repetitive JDBC boilerplate. Hibernate turns persistence
operations into SQL. JPA is the specification; Hibernate is its implementation.

The custom `@Query` uses JPQL (entity names), not raw table names. Bound parameters
keep user input separate from query structure. Search and status filtering happen
in the database. An empty search matches everything. Sorting uses date and then ID
so entries with the same date have a consistent order.

**Exercise:** Open H2 Console. Run the SELECTs in `sql-practice.sql`. Compare the
`status, COUNT(*)` query with the Java dashboard counts. Watch generated SQL in the terminal.

## Lesson 6 — Collections, streams, and exceptions

`summary()` reads a `List<JobApplication>`, creates a stream, and uses `groupingBy`
with `counting` to build a map of status counts. `JobApplication::getStatus` is a
method reference. An `EnumMap<Status, Long>` holds all statuses, including zeros.
`getOrDefault` supplies 0 when a group has no applications.

This is a useful small Java 8 exercise. Loading all rows is fine for a small personal
tracker; for a large dataset, compute totals with SQL GROUP BY and add pagination.

Read `ApiExceptionHandler.java`. `@RestControllerAdvice` centralizes error responses.
The custom not-found exception becomes 404. Invalid fields become readable 400 messages.
The browser's `try/catch` displays those messages near the form.

**Exercise:** Delete a nonexistent ID through the API. Explain how a missing Optional
becomes an exception, then a JSON error, then text in the page.

## Lesson 7 — Tests and persistence

Run `mvn test`. `ApplicationApiTest` starts Spring and uses MockMvc to send simulated
HTTP requests through real controllers, services, and repositories. Tests cover CRUD,
summary counts, filters, validation, malformed requests, missing IDs, and static resources.
The in-memory test database is separate from your normal file-backed data.

**Exercise:** Temporarily change the company max length to 5, add a test with six
characters, and verify a 400 response. Restore the original limit afterwards.
Manually stop/restart the real server and confirm your saved entry remains.

## Seven study sessions (about 45–60 minutes each)

| Session | Read / do | Check your understanding |
|---|---|---|
| 1 | Run app; add, edit, search, delete | Show a request in Network tools |
| 2 | HTML + CSS | Explain input names and responsive Grid |
| 3 | JavaScript events + fetch | Explain POST versus PUT |
| 4 | Controller + DTO + errors | Demonstrate 400 and 404 |
| 5 | Entity + repository + SQL | Match an object to a database row |
| 6 | Service + Streams + tests | Explain groupingBy and constructor injection |
| 7 | Build one feature yourself | Demo it without reading a script |

## How this fits your tracker

The detailed phase sheets, rather than the inconsistent dashboard/all-roadmap status
cells, informed this project. It is practice for the topics you marked complete,
not proof of mastery. Your original workbook is unchanged.

| Tracker / additional skill | Where you practice it |
|---|---|
| Fundamentals: types, strings, conditions, methods, loops | Entity fields, trim, null handling, summary loop |
| OOP: objects, constructors, encapsulation, interfaces | Entity, service, repository interface |
| Exceptions, generics, List/Map, enums | Optional lookup, API advice, JpaRepository, EnumMap, Status |
| Java 8: lambdas, streams, method references, Optional, dates | Service summary/get, LocalDate |
| Spring IoC/DI, Boot setup, MVC, JPA, exception handling | Main app, configuration, constructor injection, API layers |
| SQL (your added skill) | H2 Console, WHERE, ORDER BY, GROUP BY, optional MySQL |
| HTML, CSS, JS (your added skills) | Accessible labels, responsive styling, DOM events and Fetch |
| SOLID (pending in tracker) | Separation of responsibilities; constructor dependencies |

A first CRUD app does not naturally require every interview topic. Arrays, linked
lists, trees, graphs, DP, manual threads, GC tuning, class loaders, advanced inheritance,
and every design pattern are not separate features here. Continue practicing them
with dedicated problems. HTTP requests can run concurrently, but this project does
not teach thread synchronization or guarantee against two tabs overwriting one record.

## Next features, one at a time

1. **Easy: add location.** Add entity field/getter, request constraint, form input,
   JavaScript field handling, and an API test. Follow the value through every layer.
2. **SQL relations: interview rounds.** Create a separate entity with application ID,
   date, round type, and feedback. Learn a foreign key and JOIN, then JPA relationships.
3. **Collections/I/O: CSV export.** Quote commas, quotes, and newlines correctly;
   protect cells beginning with spreadsheet formula characters. Add export tests.
4. **Database scale:** move summary aggregation to SQL; add paginated search.
5. **Concurrency:** add an entity `@Version` and handle conflicting edits with HTTP 409.
6. **Security:** add Spring Security and per-user ownership before public hosting.
   Learn sessions first; add JWT only when a real client/API requirement calls for it.

Microservices can wait until you can build and debug this complete application.

## Interview practice

- Describe the complete flow when Save is clicked.
- Why use a DTO instead of accepting an ID in the request?
- What does Spring inject, and who implements the repository interface?
- Why store enum names instead of ordinals?
- How do validation and database constraints differ?
- What happens when findById returns empty?
- Where does JPQL differ from SQL?
- What would you change for 100,000 applications or multiple users?

Before putting it on your resume, implement one extension yourself, explain its
tradeoffs, run the tests, and record a short demonstration with data you can share.
