# Security Architecture & Policies

This directory outlines the security posture, threat modeling, tenant isolation controls, and supply-chain governance for `tracelyt-alerting`.

---

## Security Documentation Overview

| Document | Purpose |
|---|---|
| [Threat Model](threat-model.md) | STRIDE-aligned threat analysis covering 14 distinct alerting and incident risks. |
| [Tenant Isolation](tenant-isolation.md) | Cryptographic tenant identity verification, project boundaries, and cross-tenant protection. |
| [Secure Development](secure-development.md) | Defensive coding standards, input validation, and vulnerability triage processes. |
| [Data Privacy & Minimization](data-privacy.md) | PII redaction, sensitive attribute handling, alert annotations, and audit logging. |
| [Supply Chain Security](supply-chain.md) | Dependency pinning, GitHub Actions SHA immutability, and container provenance. |

---

## Reporting Vulnerabilities

If you discover a security vulnerability in this repository, please follow the coordinated disclosure policy in [`SECURITY.md`](../../SECURITY.md). **Do not open public GitHub issues for undisclosed security vulnerabilities.**
