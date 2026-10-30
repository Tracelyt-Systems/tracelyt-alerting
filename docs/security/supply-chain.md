# Software Supply Chain Security

This document outlines the security controls, dependency validation, build integrity mechanisms, and CI hardening policies enforced for `tracelyt-alerting`.

---

## 1. Supply Chain Threats

Open-source distributed systems are vulnerable to supply-chain attacks, including:
1. **Compromised Transitive Dependencies:** Backdoors introduced into upstream third-party libraries.
2. **Typosquatting & Malicious Package Injection:** Submitting packages with names confusingly similar to standard libraries.
3. **Compromised CI Automation:** Malicious actions executing unvetted code in pull requests.
4. **Secret Leakage in Build Artifacts:** Inadvertently embedding build-time credentials into container images.

---

## 2. Implemented Safeguards

### 2.1 Immutable GitHub Action Pinning
All GitHub Actions in `.github/workflows/` are pinned to full 40-character commit SHAs rather than mutable branch or release tags (e.g., `@v4`). This prevents tag-mutability attacks.

Example:
```yaml
uses: actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683 # v4.2.2
```

### 2.2 Dependency Scanning & Automation
- **Dependabot:** Regularly scans Maven dependencies and GitHub Actions workflows, submitting automated PRs for patched versions.
- **Dependency Review Workflow:** Automated GitHub Dependency Review scans all PRs for vulnerable dependencies and non-compliant licenses prior to merge.

### 2.3 Strict Build Reproducibility
- The Apache Maven Wrapper (`mvnw`) with SHA-verified wrapper distribution ensures that every contributor and CI runner uses identical build tooling.
- Reproducible dependency resolution is maintained via parent BOM management without dynamic or snapshot versions in release branches.

---

## 3. Container & Artifact Provenance (Future Roadmap)

When production container images are introduced in later milestones:
- Base images will be restricted to official, signed minimal runtime images (e.g., Eclipse Temurin Alpine/Debian Distroless).
- Container images will be cryptographically signed using Sigstore Cosign.
- Software Bill of Materials (SBOM) will be generated in SPDX and CycloneDX formats for every published release.
