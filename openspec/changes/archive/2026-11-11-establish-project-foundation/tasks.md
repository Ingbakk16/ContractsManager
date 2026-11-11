# Tasks

## 1. Project documentation

- [x] 1.1 Add `README.md` describing current skeleton status, Java/Docker prerequisites, and documentation links; verify every local link resolves and no planned endpoint is represented as working.
- [x] 1.2 Add `docs/product.md` with the agreed user/portfolio/membership model, Owner/Manager permissions, single-property/single-tenant contracts, ARS/USD, and IPC/manual-approval scope; verify against design.md and explicitly list unresolved calculation/date rules.
- [x] 1.3 Add `docs/architecture.md` with feature-oriented Clean Architecture, dependency direction, composition root, DTO/JPA separation, security and API conventions; verify diagrams show inward source dependencies and distinguish target from current code.
- [x] 1.4 Add `docs/roadmap.md` with gated foundation, identity, portfolio, collaboration, rental-management, IPC, and release milestones; verify unresolved decisions precede dependent implementation and Next.js remains deferred.
- [x] 1.5 Add `docs/development-workflow.md` covering scoped changes, short-lived branches, vertical slices, review, definition of done, and OpenSpec use; verify each step has a concrete output and no requirement to write a proposal for trivial fixes.
- [x] 1.6 Add `docs/deployment.md` separating local setup from future staging/production, including immutable-image promotion, migration compatibility, secrets, backups and restore checks; verify hosting choice is marked provisional and no deployment is claimed operational.

## 2. Maven verification and test baseline

- [x] 2.1 Select supported compatible Spring Boot/OpenAPI versions, remove duplicate dependencies, use the validation starter, and pin new plugin versions; verify `./mvnw dependency:tree` has no duplicate declarations and compilation succeeds, recording choices in architecture documentation.
- [x] 2.2 Bind Spotless, SpotBugs, Surefire, and Failsafe into `verify`; verify a temporary formatting violation fails the check and document formatting, unit-test, and full-verification commands.
- [x] 2.3 Add an ArchUnit baseline over existing code for package cycles without empty architecture assertions; verify the test runs through Maven and document deferred module-boundary enforcement.
- [x] 2.4 Add a PostgreSQL Testcontainers `*IT` context/persistence smoke test with test-only schema creation and isolated settings; verify it exercises the current entities and fails rather than skips when Docker is unavailable.
- [x] 2.5 Document PostgreSQL versus optional H2 use and the later Flyway/validate adoption gate; verify production readiness is not claimed before migrations exist and no business-schema migration was invented here.

## 3. Local runtime and container baseline

- [x] 3.1 Correct the Dockerfile final Java stage, non-root runtime, and port 8090; verify image build and process startup run the application rather than `top`, updating README commands.
- [x] 3.2 Add PostgreSQL health checks, aligned port mapping, explicit PostgreSQL development configuration, and safe ignored environment-file handling to Compose; verify `docker compose config` and application/database startup without deleting existing volumes.
- [x] 3.3 Remove production fallback credentials and prevent accidental debug/H2 use in production configuration; verify missing required production database settings cause startup failure and document the environment contract.

## 4. GitHub automation

- [x] 4.1 Add PR/main CI with Java 21, Maven caching, `./mvnw verify`, and container-build verification, read-only permissions, pinned actions, and concurrency cancellation; verify YAML and equivalent local commands, documenting exact check names.
- [x] 4.2 Add secret scanning and dependency review/update automation with explicit repository-plan-compatible behavior; verify scanner configuration and document any GitHub-side enablement or unavailable enforcement rather than silently skipping it.
- [x] 4.3 Add a PR template aligned with definition of done and branch-protection setup instructions; verify documentation identifies required checks and explains that repository settings require a separate authorized action.

## 5. Foundation acceptance

- [x] 5.1 Run full Maven verification, Compose startup, and container smoke checks using documented commands; record results and blockers, confirming no test silently skips PostgreSQL verification.
- [x] 5.2 Review documentation links, actual-versus-target statements, environment safety, and complete diff; verify no feature implementation, external deployment, existing user-file overwrite, or database-volume deletion entered the change.

## Workflow follow-up

- Obtain review, configure GitHub branch protection only with authorization, and archive the foundation after completion.
- Start separate business-feature proposals; resolve their listed product decisions before implementation.
