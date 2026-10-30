# Multi-Tenant Isolation & Authorization

This document establishes the tenant isolation principles, project scoping rules, deduplication boundaries, and authorization interfaces governing `tracelyt-alerting`.

---

## 1. Core Principles of Multi-Tenancy

1. **Trusted Identity Context Only:** Tenant identity (`tenant_id`) and project scope (`project_id`) must originate exclusively from cryptographically verified claims injected by `tracelyt-control-plane` (or the API gateway). An untrusted client can **never** alter tenant scope by modifying JSON payload fields or URL parameters.
2. **Strict Boundary Scoping:** Alert rule creation, condition evaluation, incident deduplication, and silence matching must strictly enforce tenant and project isolation.
3. **No Cross-Tenant Grouping:** Alert grouping keys and deduplication fingerprints must **always** prefix the tenant and project identifier:
   $$\text{GroupKey} = \text{Hash}(\text{tenant\_id}, \text{project\_id}, \text{group\_by\_labels})$$
   Under no circumstances may alerts from different tenants be aggregated into the same incident or notification intent.
4. **Isolated Quotas:** Resource consumption (concurrent evaluation slots, maximum active rules) must be governed on a per-tenant quota basis to prevent "noisy neighbor" starvation.

---

## 2. Integration with `tracelyt-control-plane`

The alerting subsystem does not implement its own user accounts, identity providers, or credential vaults:
- **Authentication Context:** Inbound requests carry validated JWT claims or mTLS service identities.
- **Role-Based Access Control (RBAC):** `tracelyt-control-plane` defines permissions (`AlertRule:Create`, `AlertRule:Edit`, `Incident:Acknowledge`, `Silence:Create`). Alerting verifies these permissions before executing state mutations.
- **Fail-Safe Authorization:** If the control-plane or identity validator is temporarily unreachable, all administrative operations fail closed (deny access). Background scheduled evaluations continue using previously validated cached tenant policies.

---

## 3. Data Leakage Prevention in Errors & Search

- Error payloads (RFC 9457) must never disclose the existence or status of other tenants' rules or incidents.
- A request querying an incident ID belonging to another tenant must return `404 Not Found` (rather than `403 Forbidden`) to prevent enumeration attacks.
