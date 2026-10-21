# Your live Launchpad application

Public website: https://bharath-launchpad.onrender.com

Deployed and verified on 21 October 2026. The server is running on Render's Free plan
using your configured Neon PostgreSQL database. No paid Render service was selected.

## Use it now

1. Open the public website.
2. Choose Create account and enter an email and a unique password of at least 12 characters.
3. Sign in, add an application, and try editing, searching, filtering, and deleting it.
4. Share the same public URL with other people. Each person registers their own account.
5. Sign out on shared devices.

You do not need START-HERE.cmd or VS Code for the public website. Your laptop may be
switched off. The local app and the hosted app use different databases; your old local
H2 records were not uploaded or automatically assigned to a cloud account.

## What was done, in order

1. Added AppUser, registration, BCrypt password hashing, and Spring Security session login/logout.
2. Added CSRF protection and a login page. The browser gets a session cookie after signing in.
3. Linked each application to its owner and checked ownership on every read, update,
   delete, search, and dashboard query.
4. Added a PostgreSQL production profile and versioned Flyway migration. Hibernate
   validates the schema when the production app starts.
5. Uploaded source to the launchpad branch of Java_Learning in job-application-tracker/.
   The existing main branch and learning files were preserved.
6. GitHub Actions ran all 14 tests successfully, including a real PostgreSQL migration test.
7. Created bharath-launchpad in Render's My Workspace, explicitly selecting Free.
   Docker built the source with Java 21 and ran its build-time tests.
8. Configured the production profile and database credentials privately through Render's
   environment settings. The local environment files, database files, and passwords
   were excluded from the source upload and downloadable ZIP.
9. Render started the JAR on its PORT value and provides the public HTTPS address.
   The app connects to PostgreSQL using TLS certificate/hostname verification.
10. Verified the live site: health, registration/login, CRUD, search, counts, CSRF,
    account isolation, and logout. The synthetic application used for verification
    was removed. Private reserved example.com verification accounts remain.

## Follow one public Save request

Browser form -> app.js -> auth.js adds the CSRF header -> public HTTPS request
-> Render -> Spring Security checks the session and CSRF token
-> ApplicationController -> ApplicationService finds the signed-in user
-> ApplicationRepository -> Hibernate/SQL -> Neon PostgreSQL.

The response goes back as JSON. load() requests your updated list and totals;
render() displays the cards. The browser's relative /api/... URLs work on the public
domain without hardcoding localhost.

## Where to manage and inspect it

- Source: https://github.com/bharathgudla20/Java_Learning/tree/launchpad/job-application-tracker
- Passed tests: https://github.com/bharathgudla20/Java_Learning/actions/runs/39635407530
- Render service: https://dashboard.render.com/web/srv-dbcep96ea8bs73fui3l0
- Public health: https://bharath-launchpad.onrender.com/actuator/health

The tested/deployed application-code commit is 2d6891d5766e4dfdcf7580a369feb5db2cab211e.
Later documentation-only commits do not change the running Java code.

## Change it later

Edit source, run tests, and push the launchpad branch. GitHub tests and Render's
automatic deployment are triggered by commits. Inspect both results; Render is
currently configured to deploy on commits, rather than waiting for GitHub checks.
Its Docker build runs tests before producing the runtime image.

Use new Flyway migration files for future database schema changes. Do not edit an
already-applied migration or replace the live database with a disposable test database.
Keep .env files and database passwords out of GitHub.

Render's Settings page can be used to set a custom health-check path to /actuator/health.
The service was created using the connector's default health-check configuration;
the endpoint itself has been checked successfully.

## Free-tier behavior and current scope

Render Free can sleep after 15 minutes without requests; a later visitor may wait
for a cold start. Database storage/compute quotas also apply. Your configured Neon
project was reused; its account plan was not changed.

This version has no email verification or password-reset flow. Sessions are stored
in the server's memory, so a restart can require signing in again; saved applications
remain in PostgreSQL. It is a small learning/demo release.

Plan references: https://render.com/docs/free and https://neon.com/pricing

For the reusable setup procedure and environment explanations, read FREE_DEPLOYMENT.md.
