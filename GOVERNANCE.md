# Project Governance

This document describes the governance structure, decision-making processes, and maintainer responsibilities for `tracelyt-alerting`.

---

## 1. Project Philosophy

Tracelyt Alerting is developed as an open, community-driven observability subsystem under the Tracelyt Systems organization. We prioritize:
- **Separation of Concerns:** Clear boundaries between alert condition evaluation, lifecycle state tracking, and notification delivery.
- **Architectural Rigor:** Decisions are documented via Architecture Decision Records (ADRs) prior to adoption.
- **Security by Design:** Multi-tenant isolation and fail-safe defaults are mandatory for all components.

---

## 2. Roles & Responsibilities

### Contributors
Anyone who submits issues, participates in architectural discussions, or submits pull requests.

### Maintainers
Active project stewards responsible for:
- Reviewing and approving pull requests.
- Evaluating and merging Architecture Decision Records.
- Safeguarding code quality, security posture, and documentation integrity.
- Managing releases and issue triage.

Current maintainers are listed in [`MAINTAINERS.md`](MAINTAINERS.md).

---

## 3. Decision-Making Process

- **Standard Changes (Bug fixes, documentation improvements):** Approved by at least one maintainer via regular pull request review.
- **Architectural Changes (New dependencies, schema models, execution engines):** Require an ADR drafted in `docs/decisions/` and consensus from the maintainer team.
- **Shared Platform Contracts:** Governed by `tracelyt-specs`. Any cross-service interface must be submitted to that repository first.
