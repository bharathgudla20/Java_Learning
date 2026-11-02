# Persistent PostgreSQL storage

The hosted Stockroom application uses PostgreSQL 17 on Neon, in a dedicated Stockroom project. The Render application and the database are separate services. Restarting or redeploying the application does not delete the database.

## What is retained?

- Registered accounts and password hashes.
- Products, prices, stock balances, and archived products.
- Orders and their original price snapshots.
- Workspace activity.

Sample demo workspaces deliberately expire after two hours and can be reset by their visitor. Use **Sign in → Create a workspace** for lasting storage. Registering creates a separate workspace; it does not copy changes from a sample demo. HTTP sessions currently live in the application, so a restart may require signing in again. Credentials and business records remain in PostgreSQL.

## Configuration

The hosted service selects `SPRING_PROFILES_ACTIVE=postgres`. This profile requires explicit database credentials and never falls back to an embedded database.

| Variable | Purpose |
|---|---|
| JDBC_DATABASE_URL | Pooled PostgreSQL JDBC URL for normal application traffic |
| MIGRATION_DATABASE_URL | Direct PostgreSQL JDBC URL for Flyway migrations |
| DB_USER | PostgreSQL role |
| DB_PASSWORD | Secret role password |

Credentials are managed in Render environment variables. Local credentials are in the Git-ignored `.env.postgres`; test-branch credentials are in the ignored `.env.postgres-test`. Neither file belongs in Git or source ZIPs.

For local PostgreSQL startup from the project folder:

```powershell
./scripts/start-postgres.ps1
```

This helper loads `.env.postgres`, selects the PostgreSQL profile, and allows the session cookie over local HTTP only. Hosted cookies remain Secure. The ordinary `./mvnw.cmd spring-boot:run` command still uses local H2 unless the PostgreSQL profile is selected.

## Schema management

Flyway applies `src/main/resources/db/migration/postgres/V1__create_stockroom.sql` once. Hibernate uses `validate` and does not automatically recreate or update the PostgreSQL schema. Future schema changes must use additional versioned migrations; do not edit a migration that has already been applied.

The schema includes unique workspace/SKU and workspace/idempotency-key constraints, owner references, non-negative stock and money checks, indexes and the order-line collection table. Hikari uses at most five pooled application connections. Migration connections use the direct endpoint.

## Verification

All 18 integration tests were run against a separate, schema-only PostgreSQL branch. That branch has an expiration; it is not the live database. CI also defines a disposable PostgreSQL 17 service for this suite, with no cloud credentials.

The deployed application is checked with `scripts/smoke-test.py`. The two-stage `scripts/persistence-check.py` saves an account, product and order, then signs in again and verifies their IDs and balances after an application redeploy. Its generated test credentials must stay in an ignored `.deployment/` file.

## Free-plan boundaries

Neon is on the account's verified Free plan. Free compute/storage/transfer quotas still apply. Scaling database compute to zero does not mean deleting its stored records. Render's free application can also sleep, so a first request may be slow. This setup is suitable for a portfolio; backups, recovery processes and capacity planning are still needed before taking responsibility for real customer data.

Provider references: [Neon Free plan](https://neon.com/pricing), [Neon scale to zero](https://neon.com/docs/introduction/scale-to-zero), [Render environment variables](https://render.com/docs/configure-environment-variables).
