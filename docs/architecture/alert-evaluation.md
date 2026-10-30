# Alert Evaluation Architecture

This document details the scheduling mechanics, evaluation windows, jitter allocation, backpressure controls, and fairness policies planned for `tracelyt-alerting`.

> **Status Notice:** This document establishes design specifications for evaluation scheduling. No actual scheduler workers, quartz jobs, or background threads are implemented in this repository foundation.

---

## 1. Scheduling Mechanics & The "Thundering Herd" Hazard

In naive alerting systems, all rules configured with a 60-second interval trigger on the exact minute boundary (`:00.000`). This produces severe architectural hazards:
- **Storage Saturation:** Thousands of concurrent queries hit `tracelyt-query-engine` at `:00.000`, exhausting query connection pools and saturating storage I/O.
- **Worker Starvation:** Alert evaluators experience 100% CPU spikes for 5 seconds followed by 55 seconds of complete idle.

### Solution: Deterministic Scheduling Jitter
Alert rule evaluation times are deterministically dispersed across their interval window using a consistent hash of the rule identifier:

$$\text{Offset} = \text{Hash}(\text{RuleID}) \pmod{\text{Interval}}$$

This spreads evaluation load uniformly across time, maintaining smooth CPU utilization and steady query throughput.

---

## 2. Evaluation Windows & Sliding Ranges

```text
Time Line:  [----------------- Evaluation Window -----------------] NOW
            |                                                      |
            |<------------------- e.g., 5 minutes ---------------->|
                                                                   ^
                                                            Trigger Moment
```

- **Evaluation Window:** The sliding duration of historical telemetry analyzed by the query (e.g., `5m`).
- **Evaluation Interval:** The cadence at which the check is re-executed (e.g., `60s`).
- **Telemetry Ingestion Lag:** Recent telemetry may arrive with 5–15 seconds of pipeline lag. The evaluation window must include a safe ingestion offset (e.g., query range `[now - 5m15s to now - 15s]`) to avoid false alarms triggered by late-arriving samples.

---

## 3. Evaluation Fairness & Admission Control

To ensure multi-tenant stability and prevent noisy-neighbor degradation:
1. **Per-Tenant Evaluation Budgets:** Each tenant is allocated a bounded concurrency quota (e.g., maximum 20 concurrent query evaluations).
2. **Evaluation Deadlines (Timeouts):** Every evaluation query dispatched to `tracelyt-query-engine` carries a hard deadline (default: 10 seconds). If a query exceeds its deadline, the evaluation is marked as `TIMEOUT` and retried on the next scheduled cycle.
3. **Backpressure & Load Shedding:** If the evaluator's internal worker queue exceeds capacity, low-priority rules (e.g., `INFO` tier) are gracefully deferred to prioritize `CRITICAL` alerting evaluations.
