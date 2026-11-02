# PostgreSQL upgrade verification — November 2, 2026

## Live configuration

- Website: https://bharath-stockroom.onrender.com
- Application source commit: `17c557aec021169a5c458603dc3ffe31ca6bd609`.
- Database: PostgreSQL 17.11 on a dedicated Neon Free project, Stockroom, in Singapore. Launchpad's project and data were not changed.
- Hosted Spring profile: `postgres`. Pooled JDBC application traffic; direct JDBC Flyway migrations. Database secrets are in Render environment variables and ignored local configuration, not Git.
- Schema: Flyway migration V1 applied successfully; Hibernate schema validation passed. The PostgreSQL profile has no H2 fallback.
- First PostgreSQL deployment: `dep-dbkckgcipgvc73cb4plg`, live.
- Deliberate same-code redeploy: `dep-dbkcmtl9fdbs73c1q3ug`, live.

## Tests completed

1. Default H2 integration suite: 18 tests, 0 failures, 0 errors after adding Flyway dependencies.
2. Separate real PostgreSQL schema-only test branch: migration V1 applied; all 18 integration tests passed with 0 failures and 0 errors. Includes simultaneous stock purchases, concurrent duplicate requests, cancellation, isolation and security checks. The test branch expires; it is not the live branch.
3. Deployed Docker build: all 18 default integration tests passed.
4. Frontend JavaScript syntax check passed.
5. Hosted HTTPS smoke script passed: database health, authentication, CSRF, product creation, order totals, request idempotency, workspace isolation, cancellation, stock restoration, demo reset and logout.
6. A generated verification account saved a new product and order through the public API. The service was deliberately redeployed without changing application code. After the replacement deployment became live, a fresh login found the same account ID, product ID, order ID, price, stock balance of five and order total of INR 246.90. Persistence across application replacement is verified.
7. Read-only JDBC inspection confirmed the live PostgreSQL version and successful Flyway history entry.

The generated verification credentials are stored only in the ignored `.deployment/persistence.json` on the task host. The account contains disposable sample information. No real user credentials or customer data were used for the checks.

## Behavior to understand

Registered accounts and their business data survive application restarts. Sample demo workspaces still expire after two hours and can be reset. Registration creates a separate persistent workspace; it does not migrate changes from an anonymous demo. Sessions remain in application memory, so users may need to sign in again after restart.

The old H2 demo's in-memory records were not exported into PostgreSQL. This upgrade starts a new persistent datastore. The previous initial-release report in VERIFICATION.md is historical and superseded by this report for the hosted configuration.

## Remaining limits

Free hosting/database quotas and cold starts apply. PostgreSQL schema migration and application-restart persistence are verified; this is not a backup/restore or disaster-recovery test. No production-scale load test or real customer traffic is claimed. CI contains both H2 and disposable PostgreSQL jobs; their GitHub run status was not independently asserted during this delivery.
