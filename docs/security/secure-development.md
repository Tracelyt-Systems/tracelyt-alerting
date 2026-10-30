# Secure Development Lifecycle (SDLC)

This document establishes the security standards and secure development practices required for all code contributed to `tracelyt-alerting`.

---

## 1. Core Principles

1. **Defense in Depth:** Security controls must not rely on a single defensive layer. Input validation, admission quotas, tenant boundary checks, and database-level parameterization must operate in tandem.
2. **Secure by Default:** Default configurations must be the most secure. Relaxed settings (e.g., debug endpoints, expanded evaluation windows) must require explicit opt-in flags.
3. **Fail-Safe Defaults:** If a permission check or tenant token validation fails or experiences a timeout, the operation must fail closed (deny access).
4. **Zero Trust Inter-Service Communication:** Never assume requests arriving on internal network interfaces are trustworthy. Enforce mTLS and cryptographically signed context.

---

## 2. Secure Coding Guidelines

### 2.1 Input Validation & Query Sanitization
- All client inputs (threshold values, evaluation intervals, label selectors, query expressions) must be strictly validated against whitelist schemas.
- Unsanitized strings must never be concatenated into query strings or database queries. Parameterized queries or structured AST nodes must be used exclusively.
- Regular expressions supplied in rule queries or silences must be evaluated with bounded execution engines (such as RE2) to prevent Regular Expression Denial of Service (ReDoS).

### 2.2 Secret Handling
- **No Hardcoded Secrets:** Passwords, API tokens, encryption keys, and private certificates must never appear in source code, tests, configuration files, or Docker images.
- **Environment Variable Conventions:** Secrets must be injected at runtime via environment variables or secret managers (e.g., HashiCorp Vault, AWS Secrets Manager, Kubernetes Secrets).
- **Scanning:** All pull requests and commits are scanned via local tooling (`tools/validate_docs.py`) and CI workflows.

### 2.3 Safe Error Reporting
- Production error responses must adhere to RFC 9457 Problem Details.
- Database connection strings, SQL dialect errors, server IP addresses, internal hostnames, and Java stack traces must **never** be exposed in API responses.
- Sanitized user-facing error messages must provide actionable guidance without leaking infrastructure topology.

---

## 3. Dependency Review & Supply Chain Protection
- Only dependencies with compatible, non-copyleft open-source licenses are permitted.
- Automated dependency vulnerability checks run on every pull request via [`.github/workflows/dependency-review.yml`](../../.github/workflows/dependency-review.yml).
- GitHub Actions workflows are pinned to full immutable commit SHAs.

---

## 4. Vulnerability Disclosure & Triage
- Vulnerabilities must be reported confidentially through GitHub Private Vulnerability Reporting on the repository (see [`SECURITY.md`](../../SECURITY.md)).
- Do not create public issues, discussions, or pull requests for suspected vulnerabilities.
