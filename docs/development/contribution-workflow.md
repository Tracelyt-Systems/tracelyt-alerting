# Contribution Workflow

This document details the development lifecycle, branch conventions, review criteria, and pull request guidelines for contributing to `tracelyt-alerting`.

---

## 1. Development Lifecycle

```text
[Fork / Branch] ──> [Local Build & Checkstyle] ──> [PR Submission] ──> [CI Validation] ──> [Maintainer Review] ──> [Merge]
```

---

## 2. Branching Strategy

- **Default Branch:** `main` (always protected; direct pushes disabled).
- **Feature Branches:** Use descriptive names prefixed by type:
  - `feat/<short-description>` (new capabilities)
  - `fix/<issue-number>-<short-description>` (bug fixes)
  - `docs/<short-description>` (documentation updates)
  - `refactor/<short-description>` (code hygiene or non-breaking refactors)

---

## 3. Pull Request Requirements

Before opening a pull request:
1. Ensure your branch is rebased on the latest `main`.
2. Run local validation:
   ```bash
   ./mvnw -B -ntp clean test checkstyle:check
   python tools/validate_docs.py
   ```
3. Complete all sections of the [Pull Request Template](../../.github/PULL_REQUEST_TEMPLATE.md).
4. Verify that no secrets, tokens, or local credentials are included in the diff.
5. If introducing architectural changes or new runtime tools, propose an ADR first.

---

## 4. Maintainer Review & Merge Policy

- Every pull request requires review and approval from at least one repository maintainer listed in [`MAINTAINERS.md`](../../MAINTAINERS.md).
- Automated CI checks (Build, Checkstyle, Tests, Documentation links) must pass completely.
- Commits are squashed and merged into `main` with a conventional commit message.
