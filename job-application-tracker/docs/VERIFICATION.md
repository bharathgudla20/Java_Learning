# Verification - deployed v2

Verified on 20-21 October 2026.

- Local build: 13 API/security tests passed; the PostgreSQL-only test was skipped locally.
- GitHub Actions: all 14 tests passed with no failures, errors, or skips, including
  Flyway V1, PostgreSQL 16.15 schema validation, and owned-record persistence.
- CI: https://github.com/bharathgudla20/Java_Learning/actions/runs/39635407530
- JavaScript syntax checks passed for app.js, auth.js, and login.js.
- Local packaged-JAR HTTP checks passed, including persistence across restart.
- Render built the Docker image, started the production profile, and reports the deployment live.
- Public HTTPS checks passed for health, registration/login, CRUD/search, private
  summary counts, CSRF rejection, cross-account access denial, and logout.
- The public login page was inspected in the browser.
- Synthetic application data was removed after the public test. Reserved example.com
  verification accounts remain private.
- Private environment files, node_modules, local database files, and deployment state
  are excluded from the ZIP and source upload.

Live website: https://bharath-launchpad.onrender.com
Deployed application-code commit: 2d6891d5766e4dfdcf7580a369feb5db2cab211e

Your original data folder was not uploaded. See DEPLOYED.md for the actual hosting steps,
provider dashboard link, current limitations, and the future update flow.
