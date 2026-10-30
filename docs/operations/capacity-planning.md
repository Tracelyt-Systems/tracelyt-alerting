# Capacity Planning & Workload Sizing

This document outlines the architectural variables, workload dimensions, and future benchmarking methodologies required to accurately size `tracelyt-alerting` deployments.

> **Important Notice:** No synthetic benchmarks, fake latency guarantees, or fictional evaluations-per-second numbers are included in this document. Real capacity metrics must be established through empirical benchmarking against actual production workloads in future milestones.

---

## 1. Why Alerting Sizing Differs from Web API Sizing

Sizing an alert evaluation subsystem cannot be modeled like a standard HTTP request-response service:
- **Continuous Background Workload:** Evaluation scheduling operates on a relentless 24/7 clock, regardless of human web interface usage.
- **Query Engine Dependency:** An evaluator node's throughput is heavily bounded by the latency of downstream queries returned by `tracelyt-query-engine`. If storage queries slow from 100ms to 2,000ms, evaluator worker threads remain blocked 20x longer, rapidly saturating thread pools.
- **Incident Flapping Amplification:** Flapping metrics (rapidly alternating between breach and recovery) generate an avalanche of state transitions, deduplication lookups, and notification intents.

---

## 2. Key Capacity Sizing Variables

A realistic capacity model must evaluate:

| Workload Dimension | Architectural Impact | Primary Resource Constrained |
|---|---|---|
| **Total Registered Rules** | Number of active alert rules scheduled across all tenants. | Scheduler memory, rule index heap allocation. |
| **Evaluation Frequency Distribution** | Proportion of rules running at 10s vs 60s vs 300s intervals. | Scheduler timer dispatch rate, queue throughput. |
| **Average Query Latency** | Time taken by `tracelyt-query-engine` to return telemetry. | Worker thread hold time, connection pool concurrency. |
| **High-Cardinality Groupings** | Number of unique time-series instances generated per rule query. | Evaluator heap memory (tracking state per series). |
| **Active Firing Alerts** | Total alert instances simultaneously in `FIRING` state. | In-memory incident deduplication table size. |
| **Notification Intent Egress** | Frequency of intent envelopes dispatched to `tracelyt-notifications`. | Outbox buffer throughput, network I/O. |
| **Tenancy Distribution** | Balance of rules across tenants (detecting hot tenants). | Fair-share queue partitioning. |

---

## 3. Workload Scaling Profiles

Deployments typically experience two distinct operating modes:

```text
1. Steady-State Operating Mode:
   - Predictable schedule of periodic rule evaluations.
   - Low alert firing rate (< 2% of rules firing simultaneously).
   - Low state transition volume; steady query throughput.

2. Major Outage / Incident Storm Mode:
   - Widespread infrastructure failure triggers hundreds of rules simultaneously.
   - Massive spike in state transitions (NORMAL ➔ PENDING ➔ FIRING).
   - High deduplication demand; notification intent rate surges.
   - Evaluator must not crash or drop critical alerts during an incident storm.
```

---

## 4. Benchmarking Methodology (Future Stages)

When evaluation execution implementations are ready for capacity validation in subsequent roadmap phases, benchmarking must adhere to:
1. **Isolated Benchmark Hardware:** Dedicated compute nodes with pinned CPU cores and zero network contention.
2. **Realistic Jitter & Lag Distributions:** Simulate real-world query latency distributions (long-tail P99 latency up to 5 seconds).
3. **Flapping Telemetry Generation:** Benchmark suites must include flapping metrics to measure how cleanly deduplication and pending duration filters prevent notification storms.
