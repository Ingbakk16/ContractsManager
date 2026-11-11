# Design

## Context

See proposal.md for motivation. The project currently uses Java 21, Spring Boot 3.4.1, duplicated Web/JPA dependencies, a validation API without a provider, and one context-loading test. There are no controllers, repositories, CI workflows, or project documentation. The Dockerfile's last stage runs Ubuntu `top`, not Java. Application port 8090 differs from Compose's 8080 mapping. Development defaults to disposable H2; production validates a schema without migrations and defaults to root credentials.

## Goals / Non-Goals

**Goals:** Make the current skeleton reproducibly verifiable and document the agreed target without presenting planned features as implemented.

**Non-Goals:** No business endpoints, domain-model rewrite, external account administration, hosting provisioning, or release pipeline activation in this foundation change. Frontend implementation is deferred.

## Decisions

### 1. Durable documentation map

- `README.md`: actual project status, prerequisites, tested commands, and navigation.
- `docs/product.md`: agreed terminology, ownership boundaries, roles, and MVP scope.
- `docs/architecture.md`: target module boundaries, dependency direction, API conventions, security and persistence decisions.
- `docs/development-workflow.md`: requirement-to-PR process, commands, tests, quality checks, review checklist, branch protection setup.
- `docs/roadmap.md`: milestones with acceptance gates and unresolved business decisions assigned to their milestones.
- `docs/deployment.md`: development environment, staging/production target, configuration, future releases and operations.

Use English, consistent with this discussion. Link rather than duplicate authoritative sections. Clearly label current, agreed target, recommended, and unresolved items. A single sprawling README was rejected because operating instructions and product rules evolve separately.

### 2. Product baseline to document

Users register independently and later create portfolios or accept invitations. Creating a portfolio creates its Owner membership atomically. A user may belong to multiple portfolios. Owners manage employee access and portfolio settings; Managers manage properties, tenants, and contracts. Invitations grant Manager access; ownership transfer and portfolio deletion remain deferred.

Each contract links exactly one rentable property and one tenant, all in the same portfolio. Properties retain non-overlapping contract history; concurrency protection is mandatory in the eventual implementation. Contracts use explicit start/end dates, positive monthly rent, ARS or USD, and fixed or IPC-adjusted rent. Adjustment schedules use an interval in months rather than the existing ambiguous `actualizationDate`. IPC values are fetched automatically; proposals require Owner/Manager approval. Missing source data leaves proposals awaiting data. Preserve inputs, source, effective date, amounts, and approver history.

Do not silently resolve the index series/formula, reference months, rounding, publication lag, late-approval retroactivity, date inclusivity, or termination boundary. Resolve these before the relevant contract/adjustment implementation. No legal-compliance guarantees are implied.

### 3. Target architecture and technology

Keep a single Maven application and deployable modular monolith. Target feature modules: identity, portfolios, properties, tenants, contracts, and rent-adjustments. Each has domain, application, and adapters as needed; configuration is the outer composition root. Domain is plain Java; application owns use cases and ports; web and JPA adapters map explicit DTOs/entities. Do not move the current model in this tooling change.

Java 21, supported Spring Boot, REST/OpenAPI, PostgreSQL, Spring Data JPA, Flyway, Bean Validation, Spring Security, and Spring Session JDBC form the target backend. Choose exact compatible dependency versions during implementation and record them. Prefer constructor injection, immutable boundary records, behavior-oriented domain methods, explicit transactions, and no generic base-service/repository abstractions. Add ArchUnit checks over existing code for cycles now and enforce inward dependencies when new modules appear; no claim that unimplemented boundaries have been tested.

Authentication uses server-side sessions and adaptive password hashing, not custom JWTs. Include CSRF, secure HTTP-only cookies, email verification, recovery, rate limiting, and backend-enforced per-portfolio authorization in later security work. Email service and final Next.js-to-backend request topology are deferred decisions, not foundation dependencies. React/Next.js/TypeScript follows the backend MVP; same-origin routing is preferred.

### 4. Reproducible verification

Make `./mvnw verify` the authoritative command. Pin build-plugin versions. Bind Spotless checks, Surefire unit tests, Failsafe `*IT` integration tests, SpotBugs, and JUnit/ArchUnit checks to the lifecycle. Use AssertJ, Mockito selectively, and real PostgreSQL Testcontainers. Integration tests must fail clearly when Docker is unavailable rather than silently skip. A formatter application command is separate from verification.

Add a real PostgreSQL context/persistence smoke test for the existing skeleton, with test-only schema creation until the first business-schema migration change. Do not prematurely freeze the misleading current User/tenant model into production migrations. Clearly document that Flyway adoption and Hibernate validate mode are a prerequisite for persistent MVP deployments, not implemented by this foundation. Development may keep H2 explicitly optional, but PostgreSQL is the default documented integration path.

### 5. GitHub workflow

Use short-lived feature/fix/chore branches, focused PRs, squash merges, and a deployable `main`; avoid a long-lived develop branch. Meaningful features follow OpenSpec explore/propose/apply/review, while tiny fixes need proportionate documentation only.

Add `.github/workflows/ci.yml` for pull requests and pushes to main: Java 21, Maven caching, full verification, and a Docker build without publishing. Use read-only permissions by default, pinned action commits, cancellation of superseded runs, and untrusted-PR-safe triggers. Add explicit secret scanning and dependency review/update automation, documenting licensing/repository limitations rather than silently omitting checks. No deployment credentials are needed. Document stable required-check names and manual branch-protection setup; a YAML file alone cannot enforce merging rules.

Definition of done: acceptance scenarios, relevant unit/integration/API/security tests, tenant isolation checks when applicable, migration when applicable, updated docs/OpenAPI, green CI, and review. Staging smoke tests become mandatory once staging exists, not a fictional gate for this initial change.

### 6. Containers and deployment

Repair the Dockerfile to end in a Java 21 non-root runtime executing the application. Align on existing port 8090. Use Compose PostgreSQL health checks and explicit local credentials supplied via an ignored environment file with a safe example. Do not interpolate production fallback passwords or enable debug/SQL logging in production. Explicitly select the development profile in Compose; production must not accidentally inherit H2.

Managed container hosting plus managed PostgreSQL is the deployment target. Render remains a candidate, not a committed purchase; verify region, cost, private connectivity, and backups before selecting it. Later use GHCR commit-SHA images, immutable-digest promotion to isolated staging/production, migration control, smoke tests, and production approval. Document HTTPS, secret management, restricted health/metrics, redacted structured logs, alerts, backups, and restore rehearsal. Kubernetes, Redis, brokers, and microservices are unnecessary initially.

## Risks / Trade-offs

- Docker-dependent verification adds setup cost -> document Docker requirements and provide a fast unit-test loop without weakening merge verification.
- Too many tools can slow a small project -> keep one authoritative command and no speculative framework abstractions.
- Repository plans may not support every GitHub security feature -> document a runnable alternative and the actual enforcement status.
- Existing local databases may contain user data -> no volume deletion or reset; migrations and preservation need explicit review before business-model changes.
- New docs can look like delivered features -> require a current-status section and milestone gates.

## Migration Plan

Implement documentation first, then Maven verification and smoke tests, then repair local containers, then add CI and document repository settings. Validate clean checkout instructions and existing working-tree preservation. No production migration or provisioning occurs. Roll back tooling through a reviewed Git revert if needed; never delete database volumes as a rollback procedure.

## Open Questions

- Exact Spring Boot/plugin versions: verify support and compatibility during dependency cleanup.
- Hosting vendor, email provider, and frontend request topology: select before security integration or deployment, not during this foundation.
- Contract boundary and IPC calculation decisions listed above: resolve in their feature proposals before implementation.
