# Failure Model & Reliability Architecture

This document defines failure scenarios, recovery owners, retry safety semantics, and circuit breaker policies for `tracelyt-alerting`.

> **Status Notice:** This document establishes design specifications for reliability and failure handling. No resilience libraries (Resilience4j, Sentinel) or retry engines are implemented in this repository foundation.

---

## 1. Failure Scenario Matrix

| Scenario | Expected Behavior | Recovery Owner | Safe to Retry? | Mark Unknown? | State Transition Allowed? | Audit History Required? |
|---|---|---|---|---|---|---|
| **Query Engine Unavailable** | Evaluation skipped; circuit breaker trips to prevent thread starvation; log operational warning. | `tracelyt-alerting` | Yes (exponential backoff) | Yes (after grace window) | No (freeze current state) | Yes (system event) |
| **Storage Backend Overloaded** | Query timeout reached (10s); evaluation terminates; record timeout metric. | `tracelyt-query-engine` | No (avoid query retry storm) | No (keep last state) | No | Yes |
| **Evaluator Worker Node Crash** | Remaining workers reclaim expired leases; in-flight queries re-evaluated from last persisted checkpoint. | Kubernetes / Alerting | Yes | No | Yes (upon re-eval) | Yes |
| **Scheduler Queue Saturated** | Load shedding drops low-priority `INFO` rules; `CRITICAL` rules prioritized. | `tracelyt-alerting` | No (run on next cycle) | No | No | Yes |
| **Notification Service Down** | Notification intents buffered in persistent outbox; alert remains `FIRING`. | `tracelyt-notifications` | Yes (bounded outbox) | No | Yes (Alert state updates) | Yes |
| **Rule Update Mid-Evaluation** | In-flight evaluation finishes using initial rule version; subsequent evaluation loads new version. | `tracelyt-alerting` | No | No | Yes | Yes (version change) |
| **Incident State Conflict** | Optimistic lock failure on concurrent update; worker re-reads latest incident state and reapplies diff. | `tracelyt-alerting` | Yes (immediate replay) | No | Yes | Yes |
| **Network Partition (Split-Brain)** | Worker unable to renew database lease steps down; partition leader continues evaluation. | `tracelyt-alerting` | No | Yes | No | Yes |

---

## 2. The Danger of Unbounded Retries in Alerting

Unlike standard transactional business applications, **retrying analytical alert queries can trigger catastrophic cascading failures**:
- If `tracelyt-query-engine` is slow because storage I/O is saturated, retrying timed-out alert queries immediately adds more concurrent queries to an already failing storage cluster.
- **Rule:** Timed-out alert evaluations must **never** retry immediately. The evaluation is marked as timed out, and the check simply waits for its next scheduled evaluation cycle.

---

## 3. Graceful Degradation & Load Shedding

Under extreme CPU or memory pressure:
1. **Tier 1 (Shedding Non-Critical Work):** Defer background analytical trace audits and low-severity (`INFO`) alert evaluations.
2. **Tier 2 (Preserving Firing States):** Maintain active firing alerts and incident records; do not auto-resolve alerts when evaluation queries fail.
3. **Tier 3 (Operational Health Warning):** Emit platform health alarms alerting SREs to degraded evaluation capacity.
