# Missing, Delayed & Incomplete Telemetry Semantics

This document defines the semantics, state transitions, and evaluation policies when handling absent, delayed, stale, or partial observability data in `tracelyt-alerting`.

---

## 1. The Fundamental Distinction: Zero ≠ Missing ≠ Error

A frequent cause of alerting failure is conflating four fundamentally distinct telemetry states:

| Telemetry State | Mathematical Meaning | Example Scenario | Alerting Interpretation |
|---|---|---|---|
| **Healthy Non-Zero** | Value is present and within thresholds | CPU = 42% | Normal condition. |
| **Legitimate Zero** | Metric was sampled and has numerical value 0 | HTTP 500 error count = 0 | Normal condition; system is operating error-free. |
| **Missing / Absent** | No samples received for the series in the window | Service stopped emitting metrics; node powered off | Potentially catastrophic (dead service) OR normal (scale to zero). |
| **Evaluation Error** | Storage query timed out or returned partial error | ClickHouse node down or query engine overloaded | Infrastructure error; condition cannot be verified. |

> **Critical Rule:** Never coerce missing data to `0` by default. Treating missing metrics as `0` can silently blind an engineering team to dead services that have completely stopped reporting metrics.

---

## 2. Declarative `on_missing_data` Policies

Every alert rule must explicitly declare its behavior when a queried series yields no data points:

1. **`KEEP_LAST_STATE` (Default):** Retains the previous evaluation state (`NORMAL` or `FIRING`) for a bounded grace duration (e.g., 5 minutes) before escalating. Useful for telemetry with variable reporting intervals.
2. **`ALERT` (Deadman Switch):** Transitions the alert immediately to `FIRING` if no data arrives within the evaluation window. Essential for monitoring critical daemon heartbeats and agent liveness.
3. **`RESOLVE`:** Treats absence of data as normal recovery. Standard for log pattern alerts where zero matching log lines indicates healthy operation.
4. **`TRANSITION_TO_UNKNOWN`:** Explicitly marks the alert as `UNKNOWN` after a defined grace period, alerting operators to telemetry pipeline blindness without falsely claiming service health.

---

## 3. Delayed Telemetry & Late-Arriving Samples

Telemetry ingested over network pipelines encounters transit, batching, and compression delays.
- **Evaluation Offset:** Evaluators shift evaluation query windows back by a configurable safety offset (e.g., evaluating interval `[t - 5m15s, t - 15s]`).
- **Late-Arriving Reconciliation:** If late samples arrive after an evaluation has occurred, the subsequent evaluation incorporates the historical samples, self-correcting the alert state.
