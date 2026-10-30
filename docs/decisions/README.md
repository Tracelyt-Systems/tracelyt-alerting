# Architecture Decision Records (ADRs)

This directory maintains Architecture Decision Records (ADRs) for the `tracelyt-alerting` subsystem.

## What is an ADR?

An Architecture Decision Record (ADR) captures an important architectural decision made along with its context, options considered, consequences, security implications, and revisit criteria.

## Process

1. Use [`adr-template.md`](adr-template.md) to draft a new decision record.
2. Propose the ADR via a GitHub Pull Request with the status `Proposed`.
3. Discuss and review with the repository maintainers and architecture team.
4. Once consensus is reached, the status is updated to `Accepted` (or `Rejected`) and merged into `main`.

## Decision Log

| ID | Title | Status | Date | Decision Summary |
|---|---|---|---|---|
| [ADR-0001](0001-adopt-java21-spring-boot-foundation.md) | Adopt Java 21 LTS and Spring Boot 3.4 Foundation | Accepted | 2026-10-30 | Selects Java 21 LTS and Spring Boot 3.4.x for alert lifecycle orchestration, configuration, and bootstrap foundation. |
