# Development workflow

## Change lifecycle

| Step | Output |
|---|---|
| Explore | Agreed outcome, scope, unresolved decisions |
| Specify | Acceptance scenarios, API/data impact, explicit exclusions |
| Design | Small use cases, boundaries, migration and security decisions |
| Implement | One complete vertical slice with tests |
| Verify/refactor | Green checks and clear, minimal code |
| Review/merge | Focused PR, reviewed diff, passing required checks |
| Release later | Staging smoke evidence and production approval |

Use OpenSpec for meaningful features: explore, propose, review artifacts, apply tasks, verify, then archive. Do not implement unresolved rules or silently shrink acceptance criteria. Tiny fixes can use a clear PR description and tests without a heavyweight proposal. Preserve unrelated working-tree changes.

## Git conventions

- Keep `main` deployable; use short-lived `feat/...`, `fix/...`, or `chore/...` branches.
- One focused change per PR; no long-lived development branch.
- Prefer squash merges. Explain what changed, why, how it was tested, and migration/security impact.
- Never commit secrets, local environment files, generated build outputs, or production data.

## Verification contract

Authoritative command:

```bash
./mvnw verify
```

It must check formatting, compile, run unit/architecture tests, perform static analysis, and run real PostgreSQL integration tests. Docker is mandatory for the full command; unavailable Docker must fail rather than skip tests.

During development, use small failing tests, implement behavior, then refactor. Fast unit tests do not replace full verification. Keep CI and local Java version, Maven Wrapper, and lifecycle aligned.

```bash
./mvnw spotless:apply   # Format Java sources (changes files)
./mvnw spotless:check   # Check without modifying files
./mvnw test             # Formatting, compilation, and Surefire unit tests
./mvnw verify           # Also Failsafe *IT tests and SpotBugs analysis
```

Spotless **3.1.0** runs in `validate`, using Google Java Format **1.28.0**. Surefire and Failsafe are pinned to **3.5.4**; Surefire runs in `test`, Failsafe in `integration-test`/`verify`. SpotBugs **4.9.8.0** checks in `verify`. `PostgresPersistenceIT` verifies real PostgreSQL persistence; unavailable Docker fails the build.

## Definition of done

SpotBugs exclusions in `config/spotbugs-exclude.xml` cover only seven named
`UUF_UNUSED_FIELD` findings in the legacy `Contract` and `User` entities.
Both use JPA field access (`@Id` on a field), so Hibernate accesses these
persistent fields reflectively. Other fields and bug patterns remain checked.
Revisit these exclusions when the legacy entities are replaced by the agreed
domain/persistence model; do not extend them to hide unrelated findings.

- Acceptance scenarios fulfilled; no silent scope reduction.
- Relevant domain/application, PostgreSQL, HTTP validation, and security tests pass.
- Portfolio isolation, role checks, and concurrency are tested where applicable.
- Schema changes have reviewed Flyway migrations once persistent features begin.
- API/OpenAPI and operator/developer documentation reflect actual behavior.
- No credential/token/password exposure or accidental destructive cascade.
- Formatting, analysis, architecture tests, and required CI checks pass.
- Diff reviewed; operational risks and migration compatibility explained.
- Staging smoke tests pass once staging exists. They are not an invented gate for this initial local foundation.

## GitHub enforcement

CI configuration is not branch protection. After workflows exist and have run, an authorized repository administrator must enable protection/rulesets on `main`, require PRs and stable status-check names, block force pushes/deletion, and choose review requirements appropriate for team size. Avoid an impossible self-approval gate for a solo maintainer.

The `CI` workflow runs on PRs and pushes to `main`. Required check names are
`Maven verification` and `Container build`. Equivalent local commands are
`./mvnw --batch-mode verify` and `docker build -t contracts-manager:ci .`.
Actions are commit-pinned, permissions are read-only, and superseded runs cancel.
PR code never receives deployment credentials or image-publishing rights.
Until configured and observed in GitHub, merge enforcement is **not enabled**.
Publishing images and deployments are later work, not PR workflow side effects.

### Security checks and repository-plan limitations

`Security` runs `Secret scan` (Gitleaks, complete fetched Git history, redacted
findings) and `Dependency audit` (Trivy Maven manifest vulnerability scan, fails
on HIGH/CRITICAL findings) on PR/main and weekly. These container CLI scanners
need no paid GitHub security plan or scanner license; Actions compute/network
availability is still required. Scanner/database download failures fail the job,
not silently pass. Dependency audit reviews known vulnerabilities in the current
tree, not just additions; it is not a license-policy or exploitability guarantee.
Dependabot opens weekly Maven, action, and Docker update PRs; updates still need
review and green checks. Pinned action SHAs must be updated, not replaced by tags.

Equivalent local scans:

```bash
docker run --rm -v "$PWD:/repo:ro" zricethezav/gitleaks:v8.24.3 git /repo --redact --no-banner
docker run --rm -v "$PWD:/repo:ro" aquasec/trivy:0.61.1 fs --scanners vuln --severity HIGH,CRITICAL --exit-code 1 /repo
```

GitHub-native dependency review, secret scanning/push protection, and private
repository security features depend on visibility/plan and administrator
enablement. They are not claimed enabled here. An administrator should enable
available features and Dependabot alerts/security updates in repository settings.
The portable `Dependency audit` check is the baseline alternative to paid native
dependency review; it does not silently skip private repositories. Protect all
four check names (`Maven verification`, `Container build`, `Secret scan`,
`Dependency audit`) only after observing successful GitHub runs. No repository
settings are changed by this foundation.

### Administrator setup after the first successful workflow runs

1. In repository Settings, open Rules → Rulesets (or Branches → branch protection,
   depending on plan). Create a rule targeting `main`.
2. Require pull requests, block force pushes and branch deletion, and require
   conversations to be resolved. Choose review counts appropriate to the team;
   a solo maintainer cannot approve their own PR.
3. Require the exact observed checks: `Maven verification`, `Container build`,
   `Secret scan`, and `Dependency audit`. Confirm their workflow sources in the UI.
4. Decide whether branches must be up to date and who may bypass protection;
   avoid blanket administrative bypass. Save and verify with a test PR.
5. Enable available dependency alerts/security updates and secret protection.
   If the repository plan cannot enforce rules on a private repository, record
   that limitation and use a manual merge checklist; do not claim enforcement.

These are separate authorized administrator actions, not actions this change
performs. A PR template documents review expectations but cannot enforce them.
