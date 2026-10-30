# Configuration Architecture & Policy

This document establishes the configuration hierarchy, environment variable conventions, startup validation rules, and secret management policies for `tracelyt-alerting`.

---

## 1. Configuration Principles

1. **Explicit Property Names:** Use clear, namespaced hierarchical property keys (e.g., `tracelyt.alerting.max-concurrent-evaluations`).
2. **Documented & Safe Defaults:** Every configuration property must have a documented default that is safe for local developer workstations.
3. **Fail-Fast Startup Validation:** Configuration properties must be strongly typed and validated at application bootstrap using Hibernate Validator / Bean Validation. If required properties are missing or bounds are violated, startup must fail immediately with an explicit error.
4. **Zero Secrets in Repository:** Never commit real passwords, tokens, API keys, or connection credentials. Example files must use unambiguous placeholders (`CHANGE_ME_SECRET`).
5. **Environment Variable Overrides:** All configuration properties must be overridable via standard POSIX environment variables following Spring Boot relaxed binding rules (e.g., `TRACELYT_ALERTING_MAX_CONCURRENT_EVALUATIONS`).

---

## 2. Configuration Profiles & Hierarchy

Configuration is loaded through standard Spring Boot mechanisms:

```text
[application.yml (Base Defaults)]
       │
       ▼
[application-<profile>.yml (e.g., test, production)]
       │
       ▼
[Environment Variables (OS / Kubernetes Container Spec)]
```

### Profiles
- **`default` / `dev`:** Configured for local development. Embedded health endpoints on port `8084`.
- **`test`:** Used during Maven test execution ([`src/test/resources/application-test.yml`](../../src/test/resources/application-test.yml)).
- **`production`:** Active in containerized deployments. Enforces strict resource limits and sanitized error payloads.

---

## 3. Current Baseline Configuration

The current foundation defines only application and actuator health properties:

```yaml
server:
  port: 8084
  shutdown: graceful

spring:
  application:
    name: tracelyt-alerting
  lifecycle:
    timeout-per-shutdown-phase: 30s

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
```

---

## 4. Planned Future Configuration Parameters (Design Only)

When future evaluation and state persistence capabilities are approved and implemented, they will follow these documented naming conventions:

| Parameter Key | Environment Variable | Default | Description |
|---|---|---|---|
| `tracelyt.alerting.evaluation.max-concurrent` | `TRACELYT_ALERTING_EVALUATION_MAX_CONCURRENT` | `20` | Maximum rule evaluations running concurrently per node. |
| `tracelyt.alerting.evaluation.default-deadline` | `TRACELYT_ALERTING_EVALUATION_DEFAULT_DEADLINE` | `10s` | Maximum execution deadline per evaluation query. |
| `tracelyt.alerting.evaluation.telemetry-offset` | `TRACELYT_ALERTING_EVALUATION_TELEMETRY_OFFSET` | `15s` | Safety offset to account for ingestion pipeline lag. |
| `tracelyt.alerting.incident.reopen-window` | `TRACELYT_ALERTING_INCIDENT_REOPEN_WINDOW` | `15m` | Duration within which flapping alerts reopen existing incidents. |
| `tracelyt.alerting.limits.max-rules-per-tenant` | `TRACELYT_ALERTING_LIMITS_MAX_RULES_PER_TENANT` | `500` | Tenant quota cap on active alert rules. |

> **Notice:** The parameters above represent design requirements for future phases. They are not parsed or loaded by the current foundation code.
