# Storage Strategy & Candidate Evaluation

This document evaluates the persistence backends, storage paradigms, and migration strategies planned for `tracelyt-alerting`.

---

## 1. Candidate Storage Evaluations

| Storage Engine Paradigm | Candidates | Strengths for Alerting | Weaknesses / Trade-offs | Initial Recommendation |
|---|---|---|---|---|
| **Relational Database (RDBMS)** | PostgreSQL | ACID transactions, rich JSONB indexing for arbitrary alert labels, mature foreign key support for incident history, row-level locking. | Horizontal write scaling requires manual sharding or connection pooling. | **Primary Candidate** for Stage 3/4 rule definitions and incident management. |
| **Distributed SQL** | CockroachDB, YugabyteDB | Multi-region active-active replication, automatic sharding, resilient to regional node failures. | High operational resource overhead; overkill for single-region deployments. | Candidate for high-scale multi-region enterprise deployments. |
| **Key-Value Store** | Redis, Dragonfly | In-memory sub-millisecond lookups, sliding window sets for deduplication, native key TTLs for alert silences. | In-memory dataset constraints; requires secondary persistent store for audit history. | Candidate for ephemeral deduplication cache in Stage 6. |
| **Append-Only Event Store** | Kafka, Redpanda | Immutable event sourcing of every state transition; allows replaying incident timelines and audit logs. | Adds operational stateful cluster to maintain; complex partition consumer management. | Deferred to Stage 5/6 event-driven architecture. |
| **Object Storage** | S3, GCS, MinIO | Cost-effective long-term archival of historical incident post-mortems and raw evaluation records. | High latency for point lookups; unsuited for active incident state coordination. | Archival candidate for closed incidents older than 90 days. |

---

## 2. Premature Commitment Guard

In this setup foundation phase:
- **Zero Database Drivers:** No JDBC drivers, JPA starters, or database connection pools are included in `pom.xml`.
- **Interface Isolation:** When persistence is introduced in Stage 3, it will be abstracted behind clean repository interfaces (`AlertRuleRepository`, `IncidentRepository`), ensuring storage engines can be swapped or migrated without altering core lifecycle logic.
