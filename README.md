# Contracts Manager

Backend-first rental portfolio management for property owners and real-estate agencies.

## Current status

This repository is an early Spring Boot skeleton, not a functional rental-management product. It contains the application entry point and initial JPA entities. Registration, portfolios, employees, properties, tenant management, contract operations, and IPC adjustments are **planned**, not implemented. The current `User`/contract relationship does not yet represent the agreed product model.

The foundation change is establishing build checks and local runtime configuration. No staging or production deployment exists yet.

## Prerequisites

- JDK 21; check `java -version` and set `JAVA_HOME` if necessary.
- Git and the included Maven Wrapper; no global Maven installation is required.
- Docker Engine with a running daemon and Docker Compose v2. PostgreSQL integration tests require Docker and must not silently skip it.
- Network access for Maven dependencies and container images.

## Verification and container smoke check

```bash
./mvnw verify
docker build -t contracts-manager:local .
docker run --rm --name contracts-manager-smoke -p 127.0.0.1:8090:8090 contracts-manager:local
```

The current default development profile uses disposable H2. The image runs Java
as UID 10001 and listens on 8090. A generated Spring Security login is not product
authentication. Stop the foreground smoke container with Ctrl+C. See the local
environment guide for PostgreSQL Compose setup.

## Documentation


- [Product scope and business decisions](docs/product.md)
- [Architecture and technology choices](docs/architecture.md)
- [Development workflow and verification](docs/development-workflow.md)
- [Backend MVP roadmap](docs/roadmap.md)
- [Local environment and deployment plan](docs/deployment.md)
- [Foundation verification results](docs/foundation-verification.md)

Commands and environment setup are documented in those guides. Their current/target labels are intentional: a planned feature is not a delivered feature.
