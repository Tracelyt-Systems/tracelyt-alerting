# Contracts & Cross-Service Schema Governance

This directory documents the boundary integration between `tracelyt-alerting` and the canonical schema repository: [`tracelyt-specs`](https://github.com/Tracelyt-Systems/tracelyt-specs).

---

## Canonical Authority Notice

**The `tracelyt-specs` repository is the sole canonical source of truth for all shared Tracelyt contracts.**

The alerting subsystem must **not** independently invent conflicting schemas, ad-hoc JSON formats, or private gRPC definitions for:
- Alert rule definitions and versioning schemas.
- Alert condition evaluation representations.
- Alert lifecycle states (`Normal`, `Pending`, `Firing`, `Resolved`).
- Incident representations, deduplication keys, and timeline event models.
- Notification intent envelopes dispatched to `tracelyt-notifications`.
- RFC 9457 Problem Details error envelopes.
- Tenant context propagation (`X-Tracelyt-Tenant-ID`, `X-Tracelyt-Project-ID`).

---

## Planned Shared Contracts

| Contract Domain | Target File in `tracelyt-specs` | Purpose |
|---|---|---|
| **Alert Rule Schema** | `schemas/alerting/alert-rule.v1.json` | Declarative definition of metric/log/trace alert conditions and evaluation windows. |
| **Alert State Event** | `schemas/alerting/alert-state-event.v1.json` | Event payload emitted when an alert transitions between lifecycle states. |
| **Incident Envelope** | `schemas/alerting/incident.v1.json` | Aggregated incident model tracking associated alert instances and audit logs. |
| **Notification Intent** | `schemas/notifications/notification-intent.v1.json` | Approved event dispatched to `tracelyt-notifications` specifying recipient and message intent. |
| **Error Format** | `schemas/common/problem-details.v1.json` | RFC 9457 sanitized error representation. |
