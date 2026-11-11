# Local environment and deployment

## Current status

No staging or production environment is provisioned. The current backend is a skeleton. The foundation is repairing local containers and establishing verification; it does not enable release automation or make the application production-ready.

## Local development

Use JDK 21, Maven Wrapper, Docker Engine, and Compose v2. PostgreSQL is the reference database for development/integration testing; H2 may remain optional and disposable, not compatibility evidence.

For a fresh local database:

```bash
cp .env.example .env # Only if .env does not already exist
# Edit .env and choose a local-only password.
docker compose --env-file .env config --quiet
docker compose --env-file .env up --build -d --wait
docker compose logs app
docker compose down # Never add --volumes to troubleshoot
```

Local credentials live in ignored `.env`, never production credentials. Existing
PostgreSQL volumes retain their original credentials: use those in `.env`, since
POSTGRES_* only initializes a new volume. PostgreSQL 15 and `postgres_data` are
retained deliberately; do not upgrade the major version without a migration plan.
Never reuse production data locally or remove existing volumes to resolve errors.
Compose binds database 5432 and application 8090 to loopback and explicitly uses
`dev-postgres`; its schema `update` is scaffolding, not a safe migration strategy.

Application and container routing will use port **8090** consistently. Compose must explicitly select PostgreSQL development settings and wait for database health. `depends_on` alone does not establish database readiness. Schema auto-creation is acceptable only for explicitly disposable environments; the persistent MVP requires migrations.

## Target environments
### Database verification and migration gate

`./mvnw verify` runs `PostgresPersistenceIT` against a fresh PostgreSQL 17.6
Testcontainers database. It persists and reloads the existing User/Contract
relationship. Its `integration` profile and dynamic connection settings isolate it
from development databases; Hibernate `create-drop` is test-only. Docker is
required: unavailable Docker fails the build, never skips this test. H2 context
tests are a fast feedback loop, not proof of PostgreSQL compatibility.

Flyway is not installed and no business-schema migrations exist in this
foundation. Before retaining MVP data or deploying to staging/production, review
the agreed business model, introduce versioned Flyway migrations, test migration
from an empty database and supported previous versions, and use Hibernate
`validate`. Do not treat the current entities or automatic development schema
updates as a production schema contract.


| Concern | Local | Staging | Production |
|---|---|---|---|
| Runtime | Local JVM or Docker | Versioned Docker image | Same tested image digest |
| Database | Local PostgreSQL | Isolated managed PostgreSQL | Separate managed PostgreSQL |
| Configuration | Ignored local environment file | Platform secrets/settings | Separate platform secrets/settings |
| Data | Synthetic | Synthetic or sanitized | Real, access-controlled |
| Schema | Disposable only until migrations land | Flyway + validation | Controlled Flyway + validation |
| Access | Local ports | HTTPS, restricted operations | HTTPS, private DB where supported |

Use managed container hosting plus managed PostgreSQL to minimize maintenance. **Render is a candidate only**: confirm pricing, region, private networking, backup/restore capabilities and budget before choosing a provider. No provider account or purchase is authorized by these docs.

## Future release workflow

```text
PR --> Verify and review --> Merge --> Build/publish SHA image
                                              |
                                              v
                                  Staging migration and deploy
                                              |
                                       Smoke checks
                                              |
                                    Production approval
                                              |
                                   Promote same image digest
```

Use GHCR if retaining GitHub-hosted delivery. Restrict publishing/deployment credentials and production environment approvals. Separate migration control from routine application startup when deploying multiple instances. Use backward-compatible expand/contract migrations: an application rollback does not reverse a database change. Retain a known-good image and document rollback commands before first release. Do not rebuild a different artifact for production.

## Production prerequisites
### Production environment contract (not a deployment)

Set `SPRING_PROFILES_ACTIVE=prod` and supply non-empty
`SPRING_DATASOURCE_URL` (a `jdbc:postgresql://...` URL),
`SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` through platform
secrets. There are no production credential defaults. The PostgreSQL driver is
explicit, so an H2 URL cannot silently select H2. Missing database settings fail
startup; even with settings, an empty database fails schema validation until
migrations exist. Do not combine production and development profiles. Production
disables debug, SQL logging, H2 console, and public OpenAPI UI/docs by default.


- Reviewed Flyway migrations for the agreed model; Hibernate `validate`, no automatic schema updates.
- Explicit production profile and required database URL/username/password; no root/default-password fallbacks, H2, debug, or SQL logging.
- HTTPS, secure cookies/CSRF, email delivery, portfolio isolation and authentication abuse controls.
- Non-root application container; least-privilege database/network/service access.
- Restricted Actuator health/readiness and metrics; avoid exposing configuration or credentials.
- Structured redacted logs, request correlation, errors/latency/database alerts, retention policy.
- Automated database backups with defined recovery objectives and retention. Restore into an isolated environment and verify records/application behavior before launch and periodically afterward.
- Deployment, smoke-test, incident, rollback, and restore runbooks tested in staging.

Frontend hosting is deferred. Prefer serving Next.js and `/api` under one public origin; finalize request/session topology before authentication integration rather than assuming CORS solves browser security.
