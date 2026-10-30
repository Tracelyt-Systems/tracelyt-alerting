# System Boundaries & Ownership Matrix

This document defines the architectural responsibilities, allowed integrations, and prohibited coupling between `tracelyt-alerting` and the other Tracelyt platform repositories.

---

## 1. Platform Boundary Diagram

```mermaid
flowchart TD
    subgraph Governance["Governance & Contracts"]
        Specs["tracelyt-specs<br><b>(Canonical Shared Contracts)</b>"]
        CP["tracelyt-control-plane<br><b>(Tenants, Projects, Quotas, Policies)</b>"]
    end

    subgraph IngestionPipeline["Telemetry Path"]
        Ingestion["tracelyt-ingestion"] --> Pipeline["tracelyt-telemetry-pipeline"]
        Pipeline --> Storage[("Analytical Storage Backends")]
    end

    subgraph AlertingCore["Alerting Subsystem (This Repository)"]
        Alerting["tracelyt-alerting<br>(Rule Definitions, Evaluation Scheduling,<br>Lifecycle Transitions, Incident Deduplication)"]
    end

    subgraph QuerySubsystem["Query Subsystem"]
        QE["tracelyt-query-engine<br>(Bounded Analytical Queries)"]
    end

    subgraph Downstream["Consumers & Downstream Delivery"]
        Notif["tracelyt-notifications<br>(Slack, Email, PagerDuty, Webhook Transports)"]
        AI["tracelyt-ai-investigator<br>(Root-Cause Summaries & Context)"]
        UI["Tracelyt Web UI / Dashboard"]
    end

    Specs -.->|Contract Governance| Alerting
    Specs -.->|Contract Governance| QE
    Specs -.->|Contract Governance| Notif

    CP -->|1. Tenant Scope & Quota Policies| Alerting
    Alerting -->|2. Request Bounded Query| QE
    QE -->|Read Telemetry| Storage
    QE -->>|3. Return Time-Series / Log Counts| Alerting
    Alerting -->|4. Emit Notification Intent| Notif
    Alerting -->|5. Provide Read-Only Incident Context| AI
    UI -->|Read Incidents & Mute Rules| Alerting
```

---

## 2. Ownership Matrix

| Subsystem Repository | Allowed Interaction with Alerting | Prohibited Coupling |
|---|---|---|
| **`tracelyt-control-plane`** | Supplies cryptographically validated tenant context, project metadata, role-based access control (RBAC), and alert quota configurations. | Alerting must **not** manage user accounts, issue JWTs, or manage platform organization billing. |
| **`tracelyt-specs`** | Canonical source of truth for alert rule schemas, event envelopes, RFC 9457 error models, and notification intent contracts. | Alerting must **not** create conflicting private schemas or unpublished gRPC stubs. |
| **`tracelyt-query-engine`** | Executes bounded, authorized queries across metrics, logs, and traces requested by alerting workers. | Alerting must **not** implement its own storage drivers, PromQL/SQL parsers, or direct analytical storage queries. |
| **`tracelyt-ingestion`** | Ingests incoming telemetry over OTLP and platform protocols. | Alerting must **not** accept raw ingestion traffic or implement protocol decoders. |
| **`tracelyt-telemetry-pipeline`** | Processes, transforms, and writes telemetry to analytical storage. | Alerting must **not** act as an in-stream telemetry transformation pipeline. |
| **`tracelyt-notifications`** | Receives approved notification intents and handles delivery (email, Slack, webhooks, provider retries, rate limits). | Alerting must **not** import provider SDKs, store SMTP credentials, or execute external webhook HTTP calls. |
| **`tracelyt-ai-investigator`** | Receives read-only incident summaries and alert metadata for investigative analysis. | AI models must **never** sit in the deterministic alert evaluation path, alter production alert rules, or silently suppress alerts. |
| **`tracelyt-deploy`** | Manages container packaging, Docker Compose stacks, Helm charts, and Kubernetes deployment topology. | Alerting must **not** duplicate platform-wide deployment manifests or cloud provisioning code. |
