# State Management & Persistence Strategy

This document evaluates the persistence requirements, concurrency guarantees, and event-driven data models planned for `tracelyt-alerting`.

> **Status Notice:** This document establishes design specifications for state management. No databases (PostgreSQL, MySQL, Redis, MongoDB) or message brokers (Kafka, RabbitMQ) are connected in this foundation repository.

---

## 1. Subsystem State Categories

The alerting subsystem coordinates multiple distinct categories of state, each with different consistency and durability requirements:

| State Category | Durability Requirement | Consistency Model | Candidate Storage Pattern |
|---|---|---|---|
| **Rule Definitions & Versions** | High (Critical business logic) | Strong (ACID transactions) | Relational Database (PostgreSQL) |
| **In-Flight Evaluation State** | Moderate (Re-computable from telemetry) | Eventual / Leased | In-Memory / Distributed KV Cache (Redis) |
| **Incident & Alert Records** | High (Audit & compliance) | Strong consistency per incident | Relational Database / Distributed SQL |
| **Deduplication Windows** | Ephemeral (Sliding TTL windows) | Fast read-after-write | In-Memory Key-Value with TTL |
| **Silences & Maintenance Windows** | High (Administrative rules) | Strong consistency | Relational Database |
| **Evaluation & Audit History** | Archival (Historical analysis) | Append-only | Partitioned Analytical Table / Object Store |

---

## 2. Concurrency & Optimistic Locking

In multi-node evaluator environments, concurrent workers may attempt to evaluate the same rule or update the same incident:
- **Optimistic Locking:** Every state transition must verify entity version tags (e.g., `version_id`) using optimistic concurrency control (`UPDATE incidents SET status = :newStatus, version = version + 1 WHERE id = :id AND version = :currentVersion`).
- **Idempotency Keys:** State mutation events and notification intents carry deterministic idempotency keys (`idempotency_key = Hash(rule_id, trigger_timestamp, entity_version)`), preventing duplicate state updates when network retries occur.

---

## 3. Conceptual Event Categories

When event-driven pipelines are adopted in later roadmap stages, state transitions will emit standard events governed by `tracelyt-specs`:
- `RULE_CREATED` / `RULE_UPDATED` / `RULE_DISABLED`
- `EVALUATION_COMPLETED` (records latency, metrics checked, condition outcome)
- `ALERT_PENDING` / `ALERT_FIRED` / `ALERT_RESOLVED`
- `INCIDENT_CREATED` / `INCIDENT_ACKNOWLEDGED` / `INCIDENT_RESOLVED`
- `SILENCE_CREATED` / `SILENCE_EXPIRED`
- `NOTIFICATION_INTENT_REQUESTED`
