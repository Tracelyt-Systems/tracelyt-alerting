# Threat Model: Tracelyt Alerting

This document provides a structured threat analysis for the future alerting subsystem within the Tracelyt Observability Platform.

> **Status Notice:** This document identifies architectural threats and planned mitigations. In the current foundational phase, no alert evaluators, incident APIs, or notification dispatchers exist. Documented mitigations represent design requirements for future implementation phases, not currently active runtime controls.

---

## 1. Assets & Trust Boundaries

### 1.1 Protected Assets
1. **Alert Rule Definitions:** Thresholds, queries, intervals, and enabled states.
2. **Incident & Alert State:** Active firing instances, severity states, acknowledgement history, and responder assignments.
3. **Execution Resources:** Scheduler queues, worker thread pools, memory, and query bandwidth with `tracelyt-query-engine`.
4. **Notification Routing Metadata:** Channel mappings, delivery policies, and tenant destination references.
5. **Audit History:** Historical records of who modified rules, acknowledged incidents, or created silences.

---

## 2. Threat Analysis Matrix

| ID | Threat Category | Attack Scenario | Impact | Proposed Mitigation (Future Implementation) | Owning Component | Residual Risk | Status |
|---|---|---|---|---|---|---|---|
| **TM-01** | Tenant Leakage | Attacker accesses or modifies alert rules / incidents belonging to another tenant. | Critical: Breach of customer isolation, data exfiltration. | Tenant scope extracted solely from verified JWT claims via `tracelyt-control-plane`. All database queries strictly parameterized with `tenant_id`. | `tracelyt-alerting` | Misconfiguration in multi-tenant queries. | Planned |
| **TM-02** | Rule Manipulation | Unauthorized actor tampers with alert thresholds to disable detection of an ongoing cyber attack. | Critical: Unmonitored production infrastructure breach. | Strict RBAC permissions; immutable rule versioning; audit event logged for every rule change. | `tracelyt-alerting` | Compromised user account with admin rights. | Planned |
| **TM-03** | Alert Flooding | Malicious or buggy rule triggers millions of alert evaluations per minute, flooding queues. | High: Worker thread exhaustion, denial of service for other tenants. | Per-tenant rule limits and evaluation rate quotas; bounded scheduler queues; load shedding. | `tracelyt-alerting` | High-priority legitimate alert floods during massive outages. | Planned |
| **TM-04** | Resource Exhaustion | Attacker creates rules with expensive sliding windows and regex expressions. | High: CPU core starvation, JVM out-of-memory crash. | Hard evaluation deadlines (10s max); maximum time window caps (e.g. 1 hour max); linear regex parsing (RE2). | `tracelyt-query-engine` & `tracelyt-alerting` | Complex valid queries consuming significant CPU. | Planned |
| **TM-05** | Suppression Abuse | Broad silence regex (e.g., `.*`) created, muting all critical infrastructure alarms. | High: Production outages go unnoticed by on-call engineers. | Disallow wildcard silences on `severity=CRITICAL`; require dual-approval or administrative role for broad silences; audit trail. | `tracelyt-alerting` | Misconfigured specific silence by authorized operator. | Planned |
| **TM-06** | Event Replay | Attacker replays stale alert state events to artificially reopen resolved incidents. | Medium: Operator confusion, paging on-call staff unnecessarily. | Idempotency tokens and monotonic event version sequence validation; reject events with stale timestamps. | `tracelyt-alerting` | Clock skew across distributed nodes. | Planned |
| **TM-07** | Duplicate Processing | Network retry causes duplicate incident creation or repeated notification triggers. | Low: Alert fatigue and duplicate tickets. | Deterministic deduplication keys (`GroupKey`); optimistic locking on incident state mutations. | `tracelyt-alerting` | Rare race condition on distributed cluster boundaries. | Planned |
| **TM-08** | Notification Abuse | Attacker configures alert notification destination pointing to unapproved external endpoints or SMS numbers. | High: Financial costs (SMS flooding), data exfiltration. | Notification channel destination allow-listing managed exclusively through `tracelyt-control-plane`. | `tracelyt-notifications` | Malicious tenant administrator. | Planned |
| **TM-09** | Secret Exposure | Database credentials or OAuth tokens logged in stack traces or exception payloads. | High: Credential theft. | RFC 9457 sanitized error responses; zero tokens in logs; Logback masking filters. | `tracelyt-alerting` | Unhandled exceptions in custom third-party integrations. | Planned |
| **TM-10** | Internal Service Abuse | Unauthorized internal process invokes alerting admin endpoints without validation. | High: Bypass of tenant boundary protections. | Mandatory mutual TLS (mTLS) and internal service authentication tokens. | `tracelyt-deploy` & `tracelyt-alerting` | Compromised node possessing valid mTLS certificate. | Planned |
| **TM-11** | Query Abuse | Malicious alert queries designed to overload downstream analytical storage backends. | High: Cluster-wide storage latency spike. | Query complexity estimation and timeout propagation to `tracelyt-query-engine`. | `tracelyt-query-engine` | Storage degradation under heavy valid query load. | Planned |
| **TM-12** | Supply-Chain Attacks | Malicious dependency version introduced via compromised upstream repository. | Critical: Remote code execution (RCE) on alerting coordinator nodes. | Weekly Dependabot scanning; automated GitHub Dependency Review; immutable SHA pinning for GitHub Actions. | Platform Engineering & Security Team | Undetected zero-day vulnerabilities in transitive libraries. | Implemented in CI |
| **TM-13** | Audit Tampering | Attacker deletes audit records of rule modifications to conceal sabotage. | High: Loss of forensic evidence and compliance breach. | Append-only audit log tables; administrative immutability; export to external archival store. | `tracelyt-alerting` | Compromised database root credentials. | Planned |
| **TM-14** | Race Conditions | Concurrent evaluators attempt to update the same alert state simultaneously. | Medium: State inconsistency and duplicate notifications. | Optimistic concurrency control using entity version columns (`version_id`). | `tracelyt-alerting` | High write contention on heavily flapping alert instances. | Planned |
