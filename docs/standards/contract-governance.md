# Contract Governance & Schema Evolution

This document defines the schema governance process, protocol selection criteria, and request/response conventions for `tracelyt-alerting`.

> **Canonical Authority:** The [`tracelyt-specs`](https://github.com/Tracelyt-Systems/tracelyt-specs) repository is the sole canonical source of truth for approved platform contracts. No shared API models, gRPC schemas, or error envelopes may be independently invented or finalized in this repository without prior approval in `tracelyt-specs`.

---

## 1. Protocol Selection Criteria

Future alerting APIs will evaluate transport protocols based on explicit criteria:

| Criterion | HTTP / JSON (REST) | gRPC / Protocol Buffers | Event Bus (Kafka / Redpanda) |
|---|---|---|---|
| **External API (Web UI / Dashboards)** | Native browser compatibility; zero proxy needed. | Requires gRPC-Web proxy. | Unsuitable for direct user CRUD actions. |
| **Cross-Service Query (Alerting ➔ Query Engine)** | Simple, easy debugging via standard HTTP/2. | High-throughput, binary serialization for large series. | Unsuitable for synchronous request-response queries. |
| **Notification Intents (Alerting ➔ Notifications)** | Simple webhooks or internal REST endpoints. | High reliability for internal service calls. | Ideal for asynchronous, decoupled event streaming with at-least-once delivery. |
| **Client Code Generation** | OpenAPI 3.1 specifications. | Protoc compiler generates idiomatic client stubs. | AsyncAPI specifications. |

**Baseline Recommendation:**
- **External Configuration API:** HTTP/JSON adhering to OpenAPI 3.1.
- **Notification Intent Egress:** Asynchronous event streaming via Protobuf/JSON events adhering to schemas in `tracelyt-specs`.

---

## 2. API Conventions (Proposed Design)

When future contracts are drafted for review in `tracelyt-specs`, they must adhere to:

### 2.1 Timestamp Semantics
- Timestamps must always use UTC and ISO-8601 representation (e.g., `2026-10-30T10:15:00.000Z`) or explicit Unix epoch nanoseconds (`timestamp_ns`).
- Relative duration strings must use explicit units: `15s` (seconds), `5m` (minutes), `2h` (hours), `7d` (days).

### 2.2 Idempotency Tokens
- State mutation requests (e.g., creating silences or acknowledging incidents) must accept client-supplied `Idempotency-Key` headers to ensure safe network retries.

### 2.3 Correlation & Tenant Context
- `X-Tracelyt-Tenant-ID`: Cryptographically validated tenant scope passed by the API gateway or control plane.
- `X-Tracelyt-Project-ID`: Targeted project identifier.
- `X-Request-ID` / `traceparent`: Correlation identifiers propagated across all downstream queries and audit log records.
