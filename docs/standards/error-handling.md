# Error Handling Standards & Problem Details

This document defines the error representation, HTTP status mapping, error taxonomy, and sanitization rules for `tracelyt-alerting`.

---

## 1. RFC 9457 Problem Details Standard

All error responses returned by the alerting API must adhere to the [RFC 9457 Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457.html) specification using `Content-Type: application/problem+json`.

### Canonical Error Schema

```json
{
  "type": "https://tracelyt.io/errors/invalid-rule-syntax",
  "title": "Invalid Alert Rule Syntax",
  "status": 400,
  "detail": "The query expression failed validation: missing closing bracket at column 42.",
  "instance": "/rules/rule_8e7d6c5b4a",
  "code": "INVALID_RULE_SYNTAX",
  "timestamp": "2026-10-30T10:15:00.123Z",
  "invalid_params": [
    {
      "name": "query_expression",
      "reason": "Syntax error at character position 42"
    }
  ]
}
```

---

## 2. Standard Error Taxonomy

| Error Code | HTTP Status | Description | Actionable Guidance for Client |
|---|---|---|---|
| `INVALID_RULE_SYNTAX` | 400 Bad Request | Rule query parser failed on syntax or invalid threshold operator. | Correct syntax error at indicated line and column. |
| `INTERVAL_TOO_SHORT` | 400 Bad Request | Requested evaluation interval is below the minimum allowed limit (< 10s). | Increase evaluation interval to at least 10 seconds. |
| `UNAUTHORIZED` | 401 Unauthorized | Missing or expired authentication token. | Refresh credentials with `tracelyt-control-plane`. |
| `TENANT_ACCESS_DENIED` | 403 Forbidden | Authenticated identity lacks permission for requested tenant/project. | Request tenant role from project administrator. |
| `INCIDENT_NOT_FOUND` | 404 Not Found | Requested incident ID does not exist or belongs to another tenant. | Verify incident identifier and project context. |
| `TENANT_QUOTA_EXCEEDED` | 429 Too Many Requests | Tenant exceeded maximum active alert rules (500 rules cap). | Archive unused rules or request quota expansion. |
| `STORAGE_QUERY_TIMEOUT` | 504 Gateway Timeout | Downstream query engine failed to return telemetry within deadline. | Narrow evaluation window or add index-backed filters. |

---

## 3. Error Sanitization & Information Leakage Prevention

1. **No Stack Traces:** Java exceptions and stack traces must **never** be included in API response payloads in production environments.
2. **No Storage Dialect Exposure:** Raw database exceptions (e.g., PostgreSQL driver syntax errors, connection pool credentials, IP addresses) must be caught at the repository boundary and mapped to sanitized problem details.
3. **Cross-Tenant Masking:** Requests querying resources belonging to other tenants must return `404 Not Found` rather than `403 Forbidden` to prevent resource enumeration attacks.
