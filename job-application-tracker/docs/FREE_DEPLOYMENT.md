# Publish Launchpad for free: Render + Neon

The application has been prepared for this route. A public URL exists only after
your accounts are connected, the source is uploaded, and Render reports a successful deployment.
Do not interpret a configuration file or a successful local build as a live deployment.

## What the two providers do

```text
Browser (public HTTPS URL)
    -> Render: Java 21 + Spring Boot + HTML/CSS/JavaScript
    -> Neon: persistent PostgreSQL database
```

Render replaces the laptop running START-HERE.cmd. Neon replaces the local H2 file.
GitHub holds source code; Docker describes how to build/run it. Flyway creates the
database tables. The prod profile reads connection settings from environment variables.

## Free means limited, rather than an always-on production service

Checked on 20 October 2026:

- Render offers a Free web service and public onrender.com address. It sleeps after
  15 minutes without requests, and the next visitor waits for a cold start.
- Neon lists a $0 Free plan with no time limit and no credit card requirement,
  including 1 GB/project and 100 CU-hours/project/month. Quotas and provider terms apply.
- We use Neon because Render's own Free PostgreSQL expires after 30 days.
- Keep both accounts on Free. Do not select paid resources or enable paid upgrades.
  If your account's creation flow requires billing information, stop and review it
  rather than accepting charges. Some API-based Render creation flows require billing
  details even when the dashboard supports free deployment.

Sources: https://render.com/docs/your-first-deploy,
https://render.com/docs/free, https://neon.com/pricing,
https://api-docs.render.com/reference/create-service

## Step 1: try the updated app locally

1. Stop the previous app (Ctrl+C in its terminal). Run one instance at a time.
2. Back up your data folder while the old app is stopped. The supplied source backup
   contains code, not your database or passwords.
3. Run START-HERE.cmd from the new version's extracted folder. It uses the v2 JAR.
   Or run JobTrackerApplication.main() with Java 21 from VS Code.
4. Visit http://localhost:8080, choose Create account, and register a test account.
5. Sign in, create an application, and sign out. Sign in with a different account:
   the first account's entries must not be visible.

The old v1 JAR still represents the original app if you retained it. Do not deploy
that JAR. Deploy this source using its Dockerfile.

## Step 2: publish the source to GitHub

Create a separate repository named job-application-tracker, preferably private initially.
In VS Code, open this project, choose Source Control -> Initialize Repository,
review staged files, commit, then Publish to GitHub. Follow the GitHub sign-in flow.

Include pom.xml, src/, Dockerfile, .dockerignore, render.yaml, docs/, and .github/.
Exclude data/, target/, root JAR files, logs, .env files, and all credentials.
The supplied .gitignore does this for newly added files; always review staged files.
If a file was already committed, .gitignore alone will not remove it from Git history.

The GitHub Actions workflow runs Java tests plus a real disposable PostgreSQL migration
test. Wait for it to pass before deploying. Repository Actions permissions must allow
the workflow to run. A private repository may have its own Actions usage allowance.

## Step 3: create the free Neon database

1. Visit https://console.neon.tech and sign in/create your own account.
2. Select Free, create a project named launchpad, and choose a region near the server.
3. Open Connect and choose Java/JDBC if that option is available.
4. Use a direct (non-pooled) connection for this small app and its migrations.
5. Keep the hostname, database, username, and password ready in the provider dashboard.
   Do not paste the password into a public repository, screenshot, or chat.

The Java URL should look like:

```text
jdbc:postgresql://YOUR-HOST.neon.tech:5432/neondb?sslmode=verify-full
```

Replace YOUR-HOST and the database name with the real values from Neon. This is a
template, not an actual database. The leading jdbc:postgresql:// is required;
a postgresql://user:password@host/database URI is not the same format.
Use DB_USERNAME and DB_PASSWORD separately. Keep TLS certificate verification enabled.

## Step 4: deploy the free Render server

Option A: in https://dashboard.render.com choose New -> Blueprint, connect the repository,
and use the included render.yaml. Review that its web service plan is Free. The Blueprint
defines no paid database; Neon is supplied separately.

Option B: choose New -> Web Service, connect the repository, select Docker as the runtime,
use Dockerfile in the repository root, choose Free, and set health check /actuator/health.
If you put this project inside another repository, set Root Directory to its project
subfolder so Render can find pom.xml and Dockerfile. For a dedicated repository leave it blank.

Set these environment variables in Render's environment settings:

| Name | Value |
|---|---|
| SPRING_PROFILES_ACTIVE | prod |
| DB_URL | Your Neon JDBC URL with sslmode=verify-full |
| DB_USERNAME | Your Neon database username |
| DB_PASSWORD | Your Neon database password |

Render supplies PORT automatically. Do not set SPRING_DATASOURCE_URL to an old H2 URL.
Do not activate mysql together with prod.

Deploy. Docker compiles the source, runs the local integration tests, and packages
the JAR. On startup Flyway creates the PostgreSQL schema and Hibernate validates it.
Wait for Render to report Live. Open the actual URL Render provides; no URL in this
guide is a claim that a deployment has already been created.

## Step 5: verify the real public service

1. Open /actuator/health: it should return {"status":"UP"} without database details.
2. Open the home page: it should send an unauthenticated visitor to the login page.
3. Register account A, sign in, create an application, edit its status, and search.
4. In an incognito window, register account B. Its list and counts should be empty.
5. Request account A's application ID while signed in as B: the API should return 404.
6. Sign out. Protected API requests should return 401.
7. Redeploy the same version. You must sign in again, but saved applications should remain.

Do not point migration tests or cleanup code at your live database. Tests with
POSTGRES_TEST_URL delete their own disposable test records.

## What new files are doing

| File | Responsibility |
|---|---|
| AppUser / UserRepository | Store email and a BCrypt password hash |
| AccountService | Registration, email normalization, and current account lookup |
| SecurityConfig | Login/logout, session authentication, CSRF, protected routes, security headers |
| AuthController | Registration, session identity, CSRF token endpoint |
| AuthRateLimitFilter | Basic per-IP, per-instance throttling of account attempts |
| JobApplication.owner | Foreign-key relationship linking each row to an account |
| ApplicationRepository / Service | Scope all reads, writes, filters, and totals to the current account |
| login.html / login.js | Account forms and login requests |
| auth.js | Shared HTTP helper, including CSRF headers for writes |
| application-prod.properties | PostgreSQL, secure cookies, dynamic server port |
| V1__users_and_applications.sql | Versioned PostgreSQL schema |
| Dockerfile | Repeatable Java build and runtime, using a non-root user |
| render.yaml | Free web service settings and required environment names |
| .github/workflows/verify.yml | Tests on pushes/PRs, including real PostgreSQL in CI |

## Understand login and Save

```text
Register: JSON -> AuthController -> AccountService -> BCrypt hash -> app_users
Login: form data + CSRF -> Spring Security -> password check -> session cookie
Save: JSON + CSRF + session cookie -> controller -> service finds current user
      -> repository saves job application with that user's ID
List: service filters by current user -> JSON -> render() displays cards
```

The session cookie is HttpOnly; JavaScript never stores passwords or session tokens in
localStorage. In production it is Secure and sent over HTTPS. Each write first requests
a fresh CSRF token; the session cookie is sent automatically by the browser.

## Existing local entries and limitations

The old H2 file is not uploaded. Hibernate can add a nullable owner_id locally while
preserving old rows. Unowned rows remain inaccessible; registering an account does
not claim them. A deliberate, backed-up migration is needed to assign your old entries
to your own account. A fresh production schema requires every new row to have an owner.

This version is suitable for a small learning/demo deployment. It has no email
verification, password reset, shared/distributed rate limiting, or persistent sessions.
Redeployments and sleeping-server restarts can require signing in again. Sessions are
intended for a single server instance. Plan for pagination and database-side summary
aggregation if accounts accumulate many records. Arrange exports/backups appropriate
to your data; free-tier provider recovery limits are not a substitute for your own backup.

## Troubleshooting

- No public URL yet: provider accounts/source have not been connected or deployment is incomplete.
- Database URL errors: check jdbc: prefix, hostname, database, credentials, and TLS settings.
- 401: sign in again. 403 on a write: refresh and sign in so the CSRF/session can be renewed.
- Old records missing: check the account and filters; local H2 records were not transferred.
- Database file locked locally: stop the other local instance; do not delete the database.
- First visit slow: the free server and database may be waking up.
- Build failure: check Render build logs and GitHub Actions before changing production settings.
