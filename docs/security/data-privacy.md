# Data Privacy & Telemetry Minimization

This document outlines privacy principles, sensitive data protection, incident masking, and audit logging policies for `tracelyt-alerting`.

> **Important Notice:** This document establishes engineering design principles for data privacy. Tracelyt does not claim formal certification or compliance with GDPR, HIPAA, SOC 2, or ISO 27001 based solely on the existence of these architectural guidelines.

---

## 1. Privacy Principles in Alerting

Alerting systems synthesize telemetry from application logs, metrics, and distributed traces. This telemetry often contains sensitive personal data (PII) such as IP addresses, usernames, session IDs, and email addresses. Tracelyt enforces strict minimization rules:

1. **Data Minimization in Notifications:** External notifications (Slack channels, SMS, emails) are frequently distributed broadly to on-call teams. Notification payloads must contain summarized metadata and links to secure internal dashboards rather than dumping raw logs or full stack traces into third-party messaging systems.
2. **Annotation Redaction:** Alert rule annotations (e.g., `runbook_url`, `description`, `summary`) must be scanned and filtered to prevent hardcoding static API tokens, passwords, or connection strings.
3. **No Unrestricted Telemetry in Coordinator Logs:** The alerting engine itself must **never** write raw query result payloads or telemetry contents into its own operational log files.
4. **Immutable Audit Trails:** Rule modifications, silence creations, incident acknowledgements, and manual overrides must be recorded in access audit logs for administrative accountability.

---

## 2. Redaction & Masking Rules

| Data Field | Sensitivity Level | Masking Policy | Permitted Destinations |
|---|---|---|---|
| **Incident Title / Summary** | Low / Medium | Scrubbed of tokens, passwords, and authorization headers. | Alerting UI, Notification Intents |
| **Alert Labels** | Medium | Low-cardinality metadata only (service, region, environment). | Alerting UI, Notification Intents, Metrics |
| **Raw Log Snippets** | High (May contain PII) | Truncated and masked (replace emails/credit cards with `[REDACTED]`). | Internal Alerting UI (Authenticated Only) |
| **Notification Provider Secrets** | Critical | Stripped entirely from Alerting; owned by `tracelyt-notifications`. | Never exposed to Alerting |

---

## 3. Data Retention & Archival

- **Active Incidents:** Maintained in persistent storage for operational access during triage.
- **Closed Incidents & History:** Subject to tenant data retention lifecycles (default: 90 days). Closed incidents older than the retention window are either permanently pruned or archived to cold object storage.
