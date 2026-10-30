# API Compatibility & Versioning Policy

This document defines the semantic versioning rules, compatibility guarantees, and deprecation policies governing `tracelyt-alerting`.

---

## 1. Semantic Versioning

The subsystem follows [Semantic Versioning 2.0.0](https://semver.org/):
- **Major (vX.0.0):** Incompatible API changes, removals of supported condition operators, or breaking changes in notification intent event schemas.
- **Minor (v0.X.0):** Backward-compatible feature additions (e.g., new signal types, composite alert logic, new silence filters).
- **Patch (v0.0.X):** Backward-compatible bug fixes, security patches, and performance optimizations.

---

## 2. Breaking vs. Non-Breaking Changes

### Non-Breaking Changes (Allowed in Minor / Patch)
- Adding new optional fields to alert rule definitions with safe defaults.
- Adding new fields to notification intent payloads.
- Adding new supported condition operators (e.g., standard deviation bounds).
- Relaxing execution constraints (e.g., allowing longer evaluation windows).

### Breaking Changes (Requires Major Version Bump & Deprecation Cycle)
- Renaming or removing existing rule fields or notification intent attributes.
- Changing the unit or format of evaluation intervals (e.g., changing seconds to milliseconds).
- Removing support for a previously supported signal type or condition operator.
- Tightening mandatory validation rules that cause previously valid rules to fail pre-flight validation.

---

## 3. Deprecation Lifecycle

When an API field, parameter, or condition operator is designated for removal:
1. **Notice Phase:** Mark as `@Deprecated` in contract schemas (`tracelyt-specs`) and document in `CHANGELOG.md`.
2. **Runtime Warning:** Emit HTTP `Sunset` and `Deprecation` response headers (RFC 8594) when the deprecated capability is invoked.
3. **Grace Period:** Maintain backward compatibility for at least two minor releases (or 6 months) before removal in the next major version.
