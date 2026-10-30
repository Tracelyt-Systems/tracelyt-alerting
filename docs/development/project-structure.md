# Project Structure

This document outlines the directory structure, package layout, and design conventions of the `tracelyt-alerting` repository.

---

## 1. Directory Tree Overview

```text
tracelyt-alerting/
├── .github/                       # GitHub automation, issues, and CI/CD workflows
│   ├── ISSUE_TEMPLATE/            # Structured issue templates (bug reports, feature requests)
│   ├── workflows/                 # Pinned GitHub Actions workflows (CI, docs, dependency review)
│   ├── CODEOWNERS                 # Maintainer code ownership definitions
│   ├── PULL_REQUEST_TEMPLATE.md   # Standard checklist for incoming pull requests
│   └── dependabot.yml             # Automated dependency update configuration
│
├── assets/                        # Static assets (official Tracelyt logo)
│   └── tracelyt-logo.png
│
├── contracts/                     # Boundary declarations & external schema links (tracelyt-specs)
│   └── README.md
│
├── docs/                          # Comprehensive architectural and operational documentation
│   ├── README.md                  # Documentation index & reading paths
│   ├── architecture/              # Core architecture, boundaries, lifecycle, and storage strategy
│   │   ├── overview.md
│   │   ├── system-boundaries.md
│   │   ├── domain-model.md
│   │   ├── alert-evaluation.md
│   │   ├── alert-lifecycle.md
│   │   ├── incident-lifecycle.md
│   │   ├── notification-boundary.md
│   │   ├── state-management.md
│   │   ├── distributed-evaluation.md
│   │   ├── missing-data.md
│   │   ├── failure-model.md
│   │   ├── scalability.md
│   │   ├── technology-strategy.md
│   │   ├── language-boundaries.md
│   │   ├── storage-strategy.md
│   │   └── diagrams/
│   │       └── README.md
│   ├── decisions/                 # Architecture Decision Records (ADRs)
│   │   ├── README.md
│   │   ├── adr-template.md
│   │   └── 0001-adopt-java21-spring-boot-foundation.md
│   ├── development/               # Developer guides, testing strategies, coding standards
│   │   ├── local-setup.md
│   │   ├── project-structure.md
│   │   ├── coding-standards.md
│   │   ├── testing-strategy.md
│   │   ├── dependency-management.md
│   │   └── contribution-workflow.md
│   ├── security/                  # Threat model, data privacy, and secure development
│   │   ├── README.md
│   │   ├── threat-model.md
│   │   ├── tenant-isolation.md
│   │   ├── secure-development.md
│   │   ├── data-privacy.md
│   │   └── supply-chain.md
│   ├── operations/                # Production operations, observability, limits, capacity
│   │   ├── configuration.md
│   │   ├── observability.md
│   │   ├── capacity-planning.md
│   │   ├── disaster-recovery.md
│   │   ├── resource-limits.md
│   │   └── deployment-readiness.md
│   ├── standards/                 # Contract governance, compatibility, error formats
│   │   ├── contract-governance.md
│   │   ├── compatibility.md
│   │   ├── error-handling.md
│   │   ├── performance-testing.md
│   │   └── event-governance.md
│   └── roadmap/                   # Evolutionary roadmap and capability milestones
│       ├── README.md
│       └── architecture-evolution.md
│
├── src/                           # Java source and test code
│   ├── main/
│   │   ├── java/
│   │   │   └── io/tracelyt/alerting/
│   │   │       └── AlertingApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/
│       │   └── io/tracelyt/alerting/
│       │       └── AlertingApplicationTest.java
│       └── resources/
│           └── application-test.yml
│
├── tools/                         # Maintenance and verification scripts
│   ├── README.md
│   └── validate_docs.py
│
├── .checkstyle.xml                # Checkstyle rule definitions (120 char limit, import order)
├── .editorconfig                  # Editor indentation and line termination rules
├── .gitattributes                 # Git line ending normalization (LF)
├── .gitignore                     # Build, IDE, and temporary file exclusions
├── .dockerignore                  # Container build context exclusions
├── .java-version                  # Declared Java version (21)
├── .markdownlint-cli2.jsonc       # Markdown style configuration
├── .yamllint                      # YAML syntax and formatting configuration
├── .pre-commit-config.yaml        # Pre-commit hook configuration
├── Makefile                       # Developer convenience build targets
├── pom.xml                        # Maven project descriptor & dependency versions
├── mvnw / mvnw.cmd                # Apache Maven Wrapper scripts
├── CHANGELOG.md                   # Keep a Changelog release notes
├── CODE_OF_CONDUCT.md             # Contributor Covenant Code of Conduct
├── CONTRIBUTING.md                # Contribution guidelines and pull request flow
├── GOVERNANCE.md                  # Project governance, decision making, maintainer roles
├── LICENSE                        # Apache License 2.0 full text
├── MAINTAINERS.md                 # Current maintainer roster
├── NOTICE                         # Apache 2.0 attribution notice
├── README.md                      # Primary repository landing page
├── SECURITY.md                    # Vulnerability reporting and security policy
└── SUPPORT.md                     # Community channels and support guidelines
```

---

## 2. Java Package Layout

The root Java package is `io.tracelyt.alerting`.

Future packages should adhere to explicit layer boundaries:

| Package | Future Responsibility | Allowed Dependencies | Prohibited Content |
|---|---|---|---|
| `io.tracelyt.alerting` | Application main class, Spring Boot configuration, health probes | Spring Core, Actuator | Business logic, direct storage access |
| `io.tracelyt.alerting.rule` *(Planned)* | Alert rule entity definitions, validation, and versioning | In-memory models | Schedulers, network I/O |
| `io.tracelyt.alerting.evaluator` *(Planned)* | Threshold checking, sliding windows, and condition evaluation | Pure functions, math | Database transactions, notification calls |
| `io.tracelyt.alerting.lifecycle` *(Planned)* | Alert instance state tracking (`Normal` ➔ `Pending` ➔ `Firing`) | In-memory state machine | Downstream provider SDKs |
| `io.tracelyt.alerting.incident` *(Planned)* | Incident deduplication, grouping, and acknowledgement | In-memory hash trees | Direct webhook delivery |
