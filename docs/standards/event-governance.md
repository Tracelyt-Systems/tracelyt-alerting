# Event Governance & Idempotency Standards

This document establishes the event envelope specifications, state machine event governance, and idempotency guarantees for `tracelyt-alerting`.

---

## 1. Event Envelope Specification

All domain events emitted by the alerting subsystem must adhere to a standardized, versioned envelope governed by `tracelyt-specs`:

```json
{
  "event_id": "evt_01J9X4M7P98QZ2K",
  "event_type": "ALERT_STATE_CHANGED",
  "schema_version": "1.0.0",
  "tenant_id": "tenant_prod_01",
  "project_id": "proj_billing",
  "aggregate_type": "INCIDENT",
  "aggregate_id": "inc_7f8e9d0a1b",
  "timestamp": "2026-10-30T10:15:00.123456Z",
  "idempotency_key": "idemp_8e7d6c5b4a_v3",
  "payload": {
    "previous_state": "PENDING",
    "current_state": "FIRING",
    "rule_id": "rule_db_connection_pool",
    "rule_version": 3,
    "severity": "CRITICAL"
  }
}
```

---

## 2. Idempotency Guarantees

Because event brokers and network transports guarantee at-least-once delivery, event consumers may receive duplicate event instances:
1. **Deterministic Idempotency Keys:** Every event envelope contains an `idempotency_key` derived deterministically from the entity ID, target state, and monotonic version number:
   $$\text{IdempotencyKey} = \text{Hash}(\text{entity\_id}, \text{state}, \text{version})$$
2. **Consumer De-duplication:** Consumers (such as `tracelyt-notifications`) use this key to ensure that duplicate received events do not result in repeated pager alerts or duplicate Slack messages.
3. **Monotonic Version Ordering:** If an event arrives with a version number lower than the entity's current recorded version, it is discarded as a stale out-of-order event.
