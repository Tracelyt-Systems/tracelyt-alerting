# Dependency Management & Governance

This document describes the dependency selection criteria, versioning policies, and security vulnerability response procedures for `tracelyt-alerting`.

---

## 1. Dependency Discipline

Every dependency introduced into `pom.xml` represents a permanent maintenance, security, and licensing commitment. Before proposing any new dependency, contributors and maintainers must answer:

1. **Strict Necessity:** Can this capability be provided cleanly with standard Java 21 LTS APIs or Spring Boot core?
2. **Maintenance Health:** Is the upstream project actively maintained, with regular releases, security patches, and broad community adoption?
3. **Transitive Footprint:** Does the library bring heavy transitive dependencies (e.g., logging bridges, multiple JSON engines, native C/C++ libraries)?
4. **License Compatibility:** Is the library licensed under Apache 2.0, MIT, BSD 2/3-Clause, or Eclipse Public License (EPL)? Copyleft licenses (GPL, AGPL) are **prohibited**.
5. **No Speculative Dependencies:** Do not add database drivers, message broker clients, cache libraries, notification SDKs, or scheduling engines in advance of actual implemented features.

---

## 2. Dependency Hierarchy & Versions

Dependency versions are managed through the Spring Boot Dependencies Bill of Materials (BOM):

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.3</version>
    <relativePath/>
</parent>
```

- Standard dependencies omit `<version>` tags, inheriting curated, vulnerability-scanned versions from the parent BOM.
- Explicit plugin versions are declared in `<properties>` to ensure deterministic build reproducibility.

---

## 3. Automated Security Scanning

1. **Dependabot:** [`.github/dependabot.yml`](../../.github/dependabot.yml) checks weekly for updates to Maven dependencies and GitHub Actions.
2. **Dependency Review Workflow:** [`.github/workflows/dependency-review.yml`](../../.github/workflows/dependency-review.yml) scans pull requests for introduced CVEs and prohibited licenses.
3. **Vulnerability Triage:**
   - **Critical / High CVEs:** Patched immediately via BOM update or emergency dependency override.
   - **Medium / Low CVEs:** Evaluated for reachability and updated in regular release cycles.
