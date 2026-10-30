# Tracelyt Alerting Documentation

Welcome to the documentation for **Tracelyt Alerting** (`tracelyt-alerting`), the alert lifecycle and incident-state management subsystem of the Tracelyt open-source observability platform.

---

## Documentation Navigation

### 1. Architecture & Design
- [Architecture Overview](architecture/overview.md) — Role, core principles, and subsystem context.
- [System Boundaries](architecture/system-boundaries.md) — Matrix of ownership across Tracelyt repositories.
- [Domain Model](architecture/domain-model.md) — Alert rules, condition definitions, labels, and annotations.
- [Alert Evaluation](architecture/alert-evaluation.md) — Scheduling jitter, deadlines, backpressure, and evaluation fairness.
- [Alert Lifecycle](architecture/alert-lifecycle.md) — State transitions (`Normal`, `Pending`, `Firing`, `Resolved`) and controls.
- [Incident Lifecycle](architecture/incident-lifecycle.md) — Grouping, deduplication, incident creation, and resolution.
- [Notification Boundary](architecture/notification-boundary.md) — Interface with `tracelyt-notifications` and intent generation.
- [State Management](architecture/state-management.md) — Persistence requirements, idempotency, and transactional consistency.
- [Distributed Evaluation](architecture/distributed-evaluation.md) — Worker partitioning, sharding, and consensus considerations.
- [Missing & Delayed Data](architecture/missing-data.md) — Semantics of empty results, stale metrics, and telemetry lag.
- [Failure Model](architecture/failure-model.md) — Failure handling, retry hazard analysis, and graceful shutdown.
- [Scalability & Capacity](architecture/scalability.md) — Multi-dimensional workload capacity planning.
- [Technology Strategy](architecture/technology-strategy.md) — Technology evaluation matrix and statuses.
- [Multi-Language Boundaries](architecture/language-boundaries.md) — Language adoption gates and monorepo policies.
- [Storage Strategy](architecture/storage-strategy.md) — Storage candidate analysis (RDBMS, KV, time-series).
- [Architecture Diagrams](architecture/diagrams/README.md) — Mermaid visual sequence and trust boundary diagrams.

### 2. Architecture Decision Records (ADRs)
- [ADR Index](decisions/README.md) — Architecture decision catalog.
- [ADR Template](decisions/adr-template.md) — Standardized format for proposing new decisions.
- [ADR-0001: Adopt Java 21 LTS + Spring Boot Foundation](decisions/0001-adopt-java21-spring-boot-foundation.md) — Initial bootstrap foundation decision.

### 3. Developer Experience
- [Local Setup Guide](development/local-setup.md) — Prerequisites, build commands, and offline validation.
- [Project Structure](development/project-structure.md) — Repository layout and Java package boundaries.
- [Coding Standards](development/coding-standards.md) — Checkstyle rules, line limits, and formatting hygiene.
- [Testing Strategy](development/testing-strategy.md) — Test pyramid, fast unit tests, and non-blocking testing principles.
- [Dependency Management](development/dependency-management.md) — Dependency selection criteria, BOM management, and CVE triage.
- [Contribution Workflow](development/contribution-workflow.md) — Branching models, PR requirements, and review guidelines.

### 4. Security & Privacy
- [Security Architecture Overview](security/README.md) — Security posture and design objectives.
- [Threat Model](security/threat-model.md) — STRIDE-aligned threat analysis and planned mitigations.
- [Tenant Isolation](security/tenant-isolation.md) — Cryptographic tenant verification and cross-tenant isolation.
- [Secure Development Lifecycle](security/secure-development.md) — Secure coding guidelines and secret handling.
- [Data Privacy & Minimization](security/data-privacy.md) — PII masking, alert annotations, and audit logging.
- [Supply Chain Security](security/supply-chain.md) — Pinned GitHub Action SHAs, dependency scanning, and build integrity.

### 5. Operations & Production Readiness
- [Configuration Policy](operations/configuration.md) — Configuration principles, profiles, and startup validation.
- [Subsystem Observability](operations/observability.md) — Self-monitoring, recursive telemetry avoidance, and metrics.
- [Capacity Planning](operations/capacity-planning.md) — Sizing dimensions, workload profiles, and benchmark protocols.
- [Disaster Recovery](operations/disaster-recovery.md) — High-availability topology and failure recovery procedures.
- [Resource Limits & Quotas](operations/resource-limits.md) — Tenant quotas, evaluation deadlines, and memory budgets.
- [Deployment Readiness](operations/deployment-readiness.md) — Health probes, graceful shutdown, and container JVM tuning.

### 6. Standards & Governance
- [Contract Governance](standards/contract-governance.md) — Canonical authority of `tracelyt-specs` and protocol selection.
- [Compatibility Policy](standards/compatibility.md) — Semantic versioning and deprecation timelines.
- [Error Handling Standards](standards/error-handling.md) — RFC 9457 Problem Details and error taxonomy.
- [Performance Testing](standards/performance-testing.md) — Benchmarking methodology and percentile profiling.
- [Event Governance](standards/event-governance.md) — Event-driven schemas and idempotency tokens.

### 7. Roadmap
- [Milestone Roadmap](roadmap/README.md) — Project milestones overview.
- [Architecture Evolution](roadmap/architecture-evolution.md) — 7-stage evolution from foundation to advanced alerting.
