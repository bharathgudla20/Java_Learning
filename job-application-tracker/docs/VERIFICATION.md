# Verification - account-enabled v2

Checks completed on 20-21 October 2026 using Java 21.0.11 and Maven 3.9.15:

- Maven verify succeeded: 13 integration/security tests passed, no failures or errors.
- One PostgreSQL-specific migration test was skipped locally because a PostgreSQL test
  server was not available. GitHub Actions supplies a disposable PostgreSQL 16 service.
- Tests cover API lifecycle, filtering, validation, anonymous access, real session login,
  password hashing, duplicate emails, CSRF, logout, and per-user record isolation.
- JavaScript syntax checks passed for app.js, auth.js, and login.js.
- Real HTTP test against the packaged v2 JAR passed: registration, login, identity,
  session cookies, CSRF rejection, cross-account read/update/delete denial, private
  dashboard totals, logout, and account/application persistence across server restart.
- PostgreSQL migration SQL passed a syntax run using H2 PostgreSQL compatibility mode;
  this is not a substitute for testing against real PostgreSQL.

Remaining verification depends on provider access: real PostgreSQL migration/JPA checks
and the public deployment. Docker and an automated browser are not installed locally,
so a full Docker build and browser interaction test were not run on this computer.
The deployment status must be checked separately; passing local tests does not mean
that a public service is already live.

The real HTTP test used target/accounts-http-test, not the original data folder.
Old local database records and the original v1 delivery archive were not uploaded.
