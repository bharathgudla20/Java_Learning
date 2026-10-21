# Launchpad - Job Application Tracker v2

A beginner-friendly Java 21 / Spring Boot project with HTML, CSS, vanilla JavaScript,
JPA, SQL, registration, session login, and private per-user applications.

## Run locally

1. Stop any previous running copy (Ctrl+C). Use one instance at a time.
2. Extract the supplied v2 ZIP and double-click START-HERE.cmd.
3. Open http://localhost:8080. Create an account, then sign in.
4. Add, edit, search, filter, and delete your own applications.

The ZIP includes job-application-tracker-2.0.0.jar. Java 21+ is required; Maven is not
needed to try the packaged app. The launcher chooses your installed JDK 21 when found.

To run from VS Code, open this folder containing pom.xml, let Java import Maven,
then Run JobTrackerApplication.main(). For tests, use Run Tests above the test classes.

To build from a terminal with Maven 3.9+ and JDK 21:

```powershell
mvn clean verify
java -jar target/job-application-tracker-2.0.0.jar
```

A built JAR is a snapshot. Rebuild after changing source code. Retained v1 JARs run
the older app, without these account features; the updated launcher uses v2.

## What changed in v2

- Registration with normalized email, password validation, and BCrypt password hashes.
- Spring Security session login/logout; HttpOnly, SameSite cookies and CSRF protection.
- User ownership enforced on list, search, summary, get, update, and delete.
- Login page, signed-in email, sign-out button, and session-expiry handling.
- Basic per-instance account-attempt throttling and restrictive browser security headers.
- PostgreSQL production profile, Flyway schema migration, health endpoint, non-root Docker image.
- Free Render deployment manifest, Neon setup guide, and a GitHub Actions test workflow.

## Learn and deploy

- **docs/FREE_DEPLOYMENT.md**: complete free-hosting steps, architecture, new file roles,
  environment values, limitations, and public verification steps.
- **docs/LEARNING_GUIDE.md**: original Java/JavaScript lessons, with v2 context at the top.
- **docs/sql-practice.sql**: SQL exercises. Run only against disposable learning data.
- **docs/VERIFICATION.md**: completed checks and checks still requiring provider access.

Free target: Render Free web service + Neon Free PostgreSQL. The web service may sleep,
so the first visit can take longer. Free quotas apply; this is a small learning/demo
application, not an unlimited always-on service. See official plan links in the guide.
A public service is not created merely by having render.yaml: deploy it in your own account.

## Data and database settings

Local development defaults to a file-backed H2 database at data/jobtracker.mv.db.
Tests use a separate in-memory database. The H2 web console is disabled by default.
The optional mysql profile is for local learning. The prod profile is PostgreSQL only.

Existing v1 H2 rows are preserved, with no automatic account assignment. They remain
inaccessible until deliberately migrated to their correct owner. The old database is
never uploaded with the source. Back it up while the app is stopped before upgrading.

Production uses SPRING_PROFILES_ACTIVE=prod and DB_URL, DB_USERNAME, DB_PASSWORD from
hosting environment settings. Do not commit credentials or database files.
Flyway creates a fresh PostgreSQL schema and Hibernate validates it. Never edit an
already-applied migration; add a new migration for future schema changes.

## API flow

1. GET /api/auth/csrf gets a CSRF token for the current session.
2. POST /api/auth/register accepts JSON email/password with that token.
3. POST /api/auth/login accepts form-encoded email/password with that token.
4. GET /api/auth/me returns the signed-in email, never the password hash.
5. /api/applications endpoints require login. Every write also requires a CSRF token.
6. POST /api/auth/logout ends the session; future API requests require login again.

The frontend's auth.js handles token fetching and session-cookie requests. After
login or logout, tokens can change; request a fresh token before the next write.

| Method | Application endpoint | Purpose |
|---|---|---|
| GET | /api/applications?search=java&status=APPLIED | Current user's matching applications |
| GET | /api/applications/summary | Current user's status counts |
| GET | /api/applications/{id} | Current user's single application |
| POST | /api/applications | Create an owned application |
| PUT | /api/applications/{id} | Update an owned application |
| DELETE | /api/applications/{id} | Delete an owned application |

Unknown and other users' IDs both return 404. No login gives 401 for protected API reads;
invalid or missing CSRF on a write gives 403. Invalid application fields give 400.

## Tests

mvn verify runs API lifecycle, validation, authentication, CSRF, password hashing,
logout, and cross-account isolation tests against H2. PostgresMigrationTest additionally
runs when POSTGRES_TEST_URL, POSTGRES_TEST_USER, and POSTGRES_TEST_PASSWORD are set.
Use only a disposable database: this test creates/migrates tables and clears test rows.
GitHub Actions supplies a temporary PostgreSQL 16 service for this test.

## Resume description

Job Application Tracker | Java, Spring Boot, Spring Security, JPA, SQL, JavaScript

- Built a full-stack tracker with CRUD, search, status filtering, and per-user dashboards.
- Added session authentication, BCrypt password hashing, CSRF protection, and ownership checks.
- Prepared PostgreSQL migrations, Docker packaging, and automated API/security tests.

Only claim a live deployment or real PostgreSQL verification after those steps have
actually completed. Add your own feature and explain the complete request flow before
presenting this project in interviews.
