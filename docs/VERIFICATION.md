# Verification — November 2, 2026

**Historical initial H2 release. The hosted application has since been upgraded to persistent PostgreSQL. See [POSTGRES_VERIFICATION.md](POSTGRES_VERIFICATION.md) for the current configuration and restart-persistence results.**

## Deployment

- Public URL: https://bharath-stockroom.onrender.com
- Source: `bharathgudla20/Java_Learning`, separate `stockroom` branch.
- Application commit: `7a565ef36c3a355eb67233f234f36b48c8ec4bda`.
- Render deploy: `dep-dbkc2n6gekts73fc0r1g`; terminal state **live**.
- Free Docker web service in Singapore. No paid service or database was created. Existing Launchpad service and branches were not changed.
- Hosted profile: `demo`, using temporary H2 in-memory storage. Restarts clear all data and sessions. It is not a persistent PostgreSQL deployment.
- Service uses Render's default port readiness check. The application's `/actuator/health` endpoint was separately verified over HTTPS.

## Automated checks performed

1. Local Maven verification: 18 integration tests, 0 failures, 0 errors.
2. Actual Render Docker build: `mvn verify` ran all 18 tests again, 0 failures, 0 errors, BUILD SUCCESS.
3. Frontend syntax: `node --check src/main/resources/static/app.js` passed.
4. End-to-end HTTP smoke script passed against both localhost and the deployed HTTPS URL. The script creates two disposable demo workspaces and checks health, unauthenticated rejection, missing-CSRF rejection, sample seeding, product creation, server-calculated totals, request idempotency, cross-workspace denial, cancellation stock restoration, terminal-state rules, demo reset and logout.

Run the smoke script with Python 3's standard library:

```text
python scripts/smoke-test.py https://bharath-stockroom.onrender.com
```

Use this only on this portfolio demo or a development instance you control. It creates disposable sample data; rate limits apply.

## Browser checks performed

- Local welcome page and desktop dashboard visually inspected.
- Created an order through the UI and observed order counts, revenue, inventory value and activity update from the API.
- Cancelled that order through its confirmation dialog.
- Verified inventory pagination controls and searched for `mouse`, obtaining the expected single result.
- Opened the public HTTPS app, started a new isolated demo, and verified the populated dashboard and temporary-storage notice.

## Limits of verification

- Responsive CSS is included; this delivery did not run a separate mobile device/browser matrix.
- Tests use H2. PostgreSQL-specific behavior, database migrations, backups, and multi-instance sessions have not been verified.
- No load test or performance benchmark was run, and no real customer traffic/revenue is claimed.
- GitHub Actions workflow is provided. Docker's test gate was directly verified; a GitHub Actions success result is not asserted here.
- Sign-up/login password behavior is covered by the integration suite; no external email delivery or password recovery is implemented.
- Free hosting has cold starts, usage limits, and no uptime guarantee. Demo data is explicitly temporary.
