# Product scope

Status: agreed MVP target. Business features below are not yet implemented. See [the roadmap](roadmap.md) for delivery gates.

## People, portfolios, and access

- **User:** a person's login identity, not a tenant and not an agency itself. Registration is independent of portfolio creation. A user with no memberships is valid.
- **Portfolio:** a collection managed by an individual property owner or agency.
- **Membership:** connects a user to a portfolio with a role. Users may belong to multiple portfolios using the same login.
- Creating a portfolio creates the creator's Owner membership in the same transaction.
- Owners invite employees by email. Invitations grant Manager access, expire, are single-use, can be revoked, and must match the accepting user's verified email. Existing users do not register a second account.

| Permission | Owner | Manager |
|---|---|---|
| Manage properties, tenants, contracts, rent approvals | Yes | Yes |
| Invite/remove employees and revoke invitations | Yes | No |
| Change portfolio settings | Yes | No |

Portfolio deletion, ownership transfer, and read-only roles are deferred. Roles are per portfolio, never global. All operations must verify membership; UUID knowledge is not authorization.

## Rental management

- A **Property** is one independently rentable unit: a house or apartment. Building/unit hierarchies are deferred.
- A **Tenant** is the renter's business record; no login is required.
- Each **RentalContract** links exactly one property and exactly one tenant, in the same portfolio as the contract.
- A property has at most one rental covering any given period. Preserve past contracts and allow future contracts without overlap. Enforce this even under concurrent requests and date edits.
- Contracts have explicit start/end dates, rather than a duration limited to one or two years. Define permitted edits and termination behavior before implementation.
- Monthly rent is positive decimal money denominated in **ARS or USD** per contract. No currency conversion.

## Rent adjustments

Support fixed rent or automatic **IPC-based calculation**, with other indices deferred. An adjustment interval is measured in months, such as every three months; the current `actualizationDate` field is not an adequate schedule.

```text
Adjustment due --> Fetch published index data --> Calculate proposal
                           |                           |
                    Awaiting data                Pending approval
                                                       |
                                            Owner/Manager approves
                                                       |
                                            Record rent adjustment
```

Never invent missing index values. Preserve the source, reference months, input values, effective date, previous/new amounts, and approver/time. Duplicate approval must not apply the increase twice. Currency does not implicitly determine the adjustment method.

## Identity and frontend

Target: public registration and login, logout, current-user details, email verification, password recovery, and secure server-side sessions. Registration creates only a user; portfolio creation and invitation acceptance are separate authenticated actions.

The React/Next.js/TypeScript frontend is deferred until the backend is tested, documented, and deployed to staging.

## Decisions required before dependent features

Before contract lifecycle work: date inclusivity, same-day handover, future-contract states, effective termination boundary, allowed edits, and tenant/property archival rules.

Before IPC work: authoritative series/provider, index-versus-percentage formula, reference months, publication delay, adjustment anchor dates, month-end handling, precision/rounding, source corrections, and whether late approvals apply retroactively. Approval is manual; retroactivity has **not** been agreed.

This scope does not assert legal compliance. Payments, invoicing, deposits, documents/signatures, other indices, currency conversion, and building hierarchies are outside the initial MVP.
