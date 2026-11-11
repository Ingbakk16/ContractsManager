## Outcome and scope

What changed and why? Link the issue/OpenSpec change when applicable.

## Verification

Commands, acceptance scenarios, and results:

- [ ] `./mvnw verify` passes, including PostgreSQL integration tests (Docker required).
- [ ] Container build and relevant runtime smoke checks pass.
- [ ] Relevant validation/security/portfolio isolation/concurrency tests pass, or are not applicable (explain).
- [ ] Migration and rollback compatibility reviewed, or no schema change.
- [ ] Documentation/OpenAPI updated where applicable; planned features are labeled.
- [ ] No secrets, production data, unrelated changes, or destructive volume operations.
- [ ] CI/security checks pass and diff has been reviewed.
- [ ] Staging smoke evidence included once staging exists, or staging does not exist.

## Risks and follow-up

Unresolved decisions, operational impact, and any explicitly agreed follow-up:
