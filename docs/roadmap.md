# Backend-first MVP roadmap

These are delivery gates, not claims of completed functionality. See [product scope](product.md) and [architecture](architecture.md).

| Milestone | Outcome | Acceptance gate |
|---|---|---|
| 1. Foundation | Documentation, Java/Maven checks, PostgreSQL test baseline, corrected containers, PR CI | Documented clean-clone verification and local startup succeed; required checks documented |
| 2. Identity | Register, login/logout, current user, sessions, verification and recovery | No portfolio required; secure cookies/CSRF, token and credential tests pass |
| 3. Portfolios | Create/list portfolios, atomic Owner membership, scoped authorization | User can have zero or multiple memberships; cross-portfolio access denied |
| 4. Collaboration | Manager invitations, acceptance, revocation, membership management | Verified recipient match; expiry, duplicate acceptance, revoked invitation and role tests pass |
| 5. Properties and tenants | Portfolio-scoped management | Validations and isolation pass; records cannot be linked across portfolios |
| 6. Contracts | One property/tenant, explicit dates, ARS/USD monthly rent, lifecycle | Historical/future rentals work; overlaps rejected under concurrent requests; date-boundary tests pass |
| 7. IPC adjustments | Fetch data, calculate proposals, Owner/Manager approval, audit history | Missing data handled; calculations reproducible; approvals idempotent and access-controlled |
| 8. Release readiness | Staging, health/metrics, release promotion, production runbooks | Deploy, smoke test, rollback rehearsal, backup and restore rehearsal succeed |
| 9. Frontend | React/Next.js/TypeScript integration | Intended users complete owner/employee workflows against the staging API |

## Dependencies and decisions

- Foundation does not implement business endpoints or finalize the current JPA model.
- Identity: select email delivery, verification/recovery behavior, Next.js/session routing and CSRF contract before implementation.
- First persistent feature: adopt Flyway and Hibernate validation using reviewed schema changes. Do not defer migrations until production.
- Collaboration depends on identity and portfolio authorization, not public agency-ID-based joining.
- Contracts: settle date inclusivity, same-day handover, state/expiry semantics, termination boundary, archival and allowed edits first.
- IPC: settle authoritative series/source, formula, reference periods, publication lag, schedule anchoring, rounding, source corrections, and late-approval retroactivity first. Manual approval is agreed; retroactivity is unresolved.
- Deployment: choose hosting region/vendor/cost and email provider before provisioning; the managed-hosting recommendation is not a purchase decision.

## Delivery shape

After foundation, deliver one vertical slice at a time with API, application/domain rules, persistence/migration, tests, and documentation together. Add architecture boundary checks as modules appear. Do not build every repository before delivering a working use case.

The frontend remains deferred until the backend API is tested, documented, and available in staging. API acceptance scripts and OpenAPI support backend verification before a UI exists.
