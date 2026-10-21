# Verification - account-enabled v2

Verified on 20-21 October 2026 with Java 21.0.11 and Maven 3.9.15.

- Local Maven verify passed 13 API/security tests. The PostgreSQL test was skipped locally.
- GitHub Actions subsequently passed all 14 tests, with zero failures/errors/skips.
- PostgreSQL 16.15: Flyway successfully applied V1, Hibernate validated the schema,
  and the owned-record persistence test passed.
- CI run: https://github.com/bharathgudla20/Java_Learning/actions/runs/39635407530
- Tested code commit: 544151928429543dfaf1f2449a78f8bfc346df86
- JavaScript syntax checks passed for app.js, auth.js, and login.js.
- Real HTTP checks passed for registration, login, session cookies, CSRF protection,
  account isolation, logout, and account/application persistence across restart.
- Packaged frontend files and the SQL migration were compared with current source.

The local HTTP check used a disposable database under target/accounts-http-test.
Your original data folder was not uploaded or used by those tests.

Remaining checks: the actual public deployment and browser interactions.
Docker was unavailable locally, so a full local Docker build was not run.
A successful build or GitHub test run does not mean that a public server is already live.
