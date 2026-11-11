# Architecture and technology

## Current versus target

Current: one Maven/Spring Boot application under `com.example.demo`, with initial JPA entities and no business endpoints. Those entities are not the agreed domain design. Do not turn their placeholder fields into a production schema without a reviewed feature change.

Target: a **modular monolith**, one deployable backend, PostgreSQL, and a REST API. No microservices, Kubernetes, message broker, or Redis is required for the MVP.

## Target feature boundaries

```text
identity/          portfolios/        properties/
tenants/           contracts/         rentadjustments/

Within a feature, as needed:
  domain/
  application/
  adapter/web/
  adapter/persistence/

configuration/     # outer composition root
```

Source dependencies point inward:

```text
Web adapter ----------> Application ----------> Domain
Persistence adapter --> Application ports ----> Domain
Configuration --------> Concrete adapters and use cases
```

- **Domain:** plain Java rules and state transitions; no Spring, JPA, HTTP, scheduler, or external-index client dependencies.
- **Application:** use cases, repository/index-provider ports, authorization orchestration, explicit transaction semantics.
- **Adapters:** controllers, transport DTOs, JPA entities, mapping, repository implementations, index retrieval, scheduling/retries.
- **Configuration:** chooses concrete implementations and wires dependencies. Inner code never calls a service locator or composition root.

Use interfaces for meaningful infrastructure boundaries, not for every class. Return explicit boundary data; never expose JPA entities or passwords through controllers. Do not create generic base services to hide business behavior. Cross-feature interactions use explicit application contracts rather than reaching into another feature's persistence adapter.

## Technology baseline

| Concern | Agreed choice |
|---|---|
| Runtime/build | Java 21, Maven Wrapper |
| Backend/API | Supported Spring Boot, REST/JSON, OpenAPI |
| Persistence | PostgreSQL, Spring Data JPA/Hibernate |
| Schema target | Flyway migrations, Hibernate `validate` |
| Validation | Bean Validation plus domain invariants |
| Authentication target | Spring Security, Spring Session JDBC |
| Tests | JUnit, AssertJ, Mockito selectively, MockMvc, Testcontainers |
| Quality target | Spotless, SpotBugs, ArchUnit |
| Packaging/CI | Docker, GitHub Actions |
| Frontend later | React, Next.js, TypeScript |

The foundation selects Spring Boot **4.1.2** (the current stable release listed by [Spring](https://spring.io/projects/spring-boot)) and springdoc-openapi **3.2.0** (the Boot 4 generation documented by [springdoc](https://springdoc.org/)). Java remains **21**, using Maven Wrapper **3.9.9**. Boot's parent pins its managed dependency and build-plugin versions; additional verification plugins will be pinned explicitly as those tasks land. Bean Validation uses `spring-boot-starter-validation`, including its implementation rather than only the API. Web and JPA starters are declared once each.

The Maven configuration is authoritative for installed dependencies; the table above is not a claim every target library is already installed.

## API and security target

- Version business routes under `/api/v1`; publish OpenAPI with documented error cases.
- Use request/response records where useful, paginated lists, consistent validation, and `ProblemDetail` errors.
- Registration creates only a user. Login creates a server-side session; logout invalidates it. No custom JWT system or external identity provider.
- Hash passwords with an adaptive Spring Security encoder; never return or log hashes, credentials, tokens, or session identifiers.
- Keep CSRF protection for cookie-authenticated changes; use secure HTTP-only cookies in production. Decide Next.js routing, SameSite, and CSRF delivery before authentication implementation. Prefer one public origin eventually.
- Persist sessions in PostgreSQL, verify email, support recovery, rate-limit authentication, and use generic authentication errors.
- Check portfolio membership and role for every operation. Scope queries to the authorized portfolio and reject cross-portfolio property/tenant references.
- Invitation/recovery tokens are hashed at rest, expiring, single-use, and accepted transactionally.

## Clean-code and testing rules

Use constructor injection, stateless singleton beans, small intention-revealing methods, immutable boundary data, and domain behavior instead of unrestricted setters. Use decimal money and an injected `Clock`; avoid floating-point financial calculations. Document exported contracts and non-obvious decisions, not redundant implementation narration. Avoid Lombok `@Data` on JPA entities.

Tests follow arrange/act/assert and cover behavior rather than incidental implementation. Include domain boundaries, PostgreSQL constraints, HTTP validation, authentication, portfolio isolation, and concurrent conflict scenarios as features land. ArchUnit starts with a non-empty package-cycle baseline; domain/application boundary rules become enforceable when those packages exist. A test over nonexistent packages does not prove Clean Architecture.

## Database adoption gate

The foundation's `ArchitectureTests` uses ArchUnit **1.4.1** to import production
classes and check cycles between existing packages. It explicitly requires the
entity and enum packages to be present, so an empty import cannot pass as
architecture evidence. Run it with `./mvnw -Dtest=ArchitectureTests test`.
Feature-module and inward-dependency enforcement remains deferred until the
target domain/application/adapter packages exist; this baseline does not prove
those future boundaries.

PostgreSQL is the reference database. Optional H2 is only a disposable convenience, not compatibility evidence. Foundation smoke tests may create/drop schemas in isolated Testcontainers databases. The current legacy model must not be frozen into production migrations merely to pass a build.

Before any persistent MVP deployment, introduce reviewed Flyway migrations for the agreed model, use Hibernate `validate`, and remove schema auto-update/create from persistent environments. Review existing data and cascade/orphan-removal behavior; never delete volumes as a shortcut.
