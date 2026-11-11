# Proposal

## Why

The repository is an initial Spring Boot skeleton without a dependable delivery workflow or durable documentation of the agreed rental-management product. Establish a reproducible backend foundation and written working agreements before implementing business features.

## What Changes

- Add a README and linked product, architecture, development-workflow, roadmap, and deployment documents, distinguishing agreed scope from recommendations and unresolved rules.
- Standardize Java 21, Maven Wrapper, PostgreSQL, and `./mvnw verify` for local and GitHub Actions verification.
- Plan formatting, static analysis, architecture checks, PostgreSQL integration tests, dependency/secret scanning, and container-build checks.
- Correct the container runtime and inconsistent ports, and document explicit environment configuration without production credential defaults.
- Document short-lived branches, pull requests, required checks, vertical-slice development, and definition of done.
- Describe later immutable-image promotion, staging, production approval, backups, and restore verification without enabling deployment now.
- Record the backend-first MVP roadmap, including portfolios, employee access, rental management, and IPC proposals requiring manual approval.

## Capabilities

### New Capabilities

None. This change establishes documentation and build tooling, not business behavior. `skip_specs: true` is intentional.

### Modified Capabilities

None. Business capabilities will receive separate specifications before implementation.

## Impact

Future implementation affects `README.md`, `docs/`, `.github/`, `pom.xml`, Docker configuration, development properties, and build-verification tests. It does not implement authentication, change the business model, publish images, deploy infrastructure, or configure GitHub repository settings without explicit authorization. Existing untracked `.opencode/` and `openspec/` content must be preserved.
