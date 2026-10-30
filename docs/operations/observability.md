# Subsystem Observability & Telemetry

This document outlines the self-monitoring strategy, health indicators, performance metrics, and high-cardinality prevention rules for `tracelyt-alerting`.

> **Status Notice:** This document defines the observability design requirements for the alerting subsystem. The initial repository exposes only basic Spring Boot Actuator health endpoints (`/actuator/health`). No production Prometheus exporters, OpenTelemetry SDKs, or synthetic dashboards are implemented in this foundation phase.

---

## 1. The Recursive Telemetry Hazard

Because the alerting subsystem monitors the health of the entire observability platform, self-monitoring telemetry carries a critical architectural risk: **telemetry recursion**.
- If the alerting system emits high-volume metrics that trigger alert rules, an incident storm can cause the alerting engine to flood its own pipeline with evaluation events, creating an unbounded feedback loop.
- **Rule:** The alerting engine must maintain bounded, low-overhead operational metrics. Telemetry labels must strictly avoid high-cardinality values.

---

## 2. Cardinality Safeguards for Metric Labels

### Strictly Prohibited as Metric Labels:
- **Raw Alert Query Text:** Never use full query expressions or SQL/PromQL statements as label values.
- **Alert Annotations / Descriptions:** Never include user descriptions or dynamic error summaries in metric tags.
- **User / Operator IDs:** Never include user email addresses, responder IDs, or session tokens.
- **Arbitrary Tenant Input:** Unvalidated custom label values.

### Permitted Low-Cardinality Labels:
- `signal_type`: `metric`, `log`, `trace`, `composite`.
- `severity`: `critical`, `warning`, `info`.
- `status`: `success`, `condition_breached`, `timeout`, `missing_data`, `error`.
- `tenant_id`: Approved only if tenant count is bounded (< 1,000) or aggregated at organization tier.

---

## 3. Candidate Operational Metrics (Design Specification)

When future evaluation scheduling and incident management are introduced, the subsystem will emit:

| Metric Name | Type | Description | Permitted Labels |
|---|---|---|---|
| `tracelyt_alert_evaluations_total` | Counter | Total number of rule evaluations executed. | `signal_type`, `status` |
| `tracelyt_alert_evaluation_duration_seconds` | Histogram | Latency of evaluation cycles (including query time). | `signal_type`, `status` |
| `tracelyt_alert_instances_active` | Gauge | Count of alert instances currently in each lifecycle state. | `state` (`pending`, `firing`) |
| `tracelyt_alert_missed_evaluations_total` | Counter | Evaluations skipped due to worker queue saturation or backpressure. | `severity` |
| `tracelyt_incident_creations_total` | Counter | Count of new incident records generated from firing alerts. | `severity` |
| `tracelyt_notification_intents_total` | Counter | Notification intent envelopes emitted to `tracelyt-notifications`. | `event_type`, `severity` |
| `tracelyt_alert_suppressed_total` | Counter | Alert notifications suppressed by active silences or inhibition rules. | `reason` (`silence`, `inhibition`) |
| `tracelyt_alert_queue_depth` | Gauge | Current pending evaluation tasks in scheduler queue. | `priority` |

---

## 4. Health Probes & Readiness

In containerized environments (Kubernetes, Docker), the service provides:
- **Liveness Probe (`/actuator/health/liveness`):** Indicates whether the JVM process is alive and responsive.
- **Readiness Probe (`/actuator/health/readiness`):** Indicates whether the node has completed bootstrap, loaded tenant rule sets, and is ready to execute scheduled evaluations.
