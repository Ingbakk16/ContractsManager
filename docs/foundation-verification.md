# Foundation verification record

Verified locally on 2026-11-11 with Java 21 and Docker Desktop 29.9.3.

| Check | Result |
|---|---|
| `./mvnw --batch-mode verify` | Passed: formatting, unit/context/ArchUnit, PostgreSQL IT, SpotBugs |
| PostgreSQL persistence IT | Passed, 1 test, zero failures/errors/skips |
| Docker unavailable | Isolated Bubblewrap process with `/run` hidden: explicit Docker-environment error, zero skips, Maven exit 1 |
| `docker build -t contracts-manager:local .` | Passed; Java process as non-root UID 10001 |
| Compose config | Passed with `.env.example` and separate `foundation-verification` project |
| Compose build/startup | PostgreSQL healthy; `dev-postgres` app started on 8090; HTTP request returned expected default-security 401 |
| Production without database variables | Startup failed, exit 1; PostgreSQL driver rejected unresolved URL |
| Gitleaks 8.24.3 Git-history scan | Passed with redaction |
| Trivy 0.61.1 Maven dependency audit | Scanned `pom.xml`; no HIGH/CRITICAL findings |
| Workflow/Dependabot YAML | All three YAML files parsed locally |
| Documentation and diff review | Local links resolve; `git diff --check` passes; existing model changes are formatting/import cleanup, not business behavior |

The Compose test used a separate project and retained its
`foundation-verification_postgres_data` volume. Test containers and Compose
services created for verification were stopped; no database volume was deleted.
Local `.env` was not created or overwritten. Example credentials were used only
for the isolated local test database.

GitHub Actions runs, repository-plan capabilities, branch protection, and native
security feature enablement still require verification in GitHub. No remote
settings, deployments, image publishing, or feature endpoints were added.
Successful local checks do not imply production readiness: migrations and
product security are still gated by the roadmap.
