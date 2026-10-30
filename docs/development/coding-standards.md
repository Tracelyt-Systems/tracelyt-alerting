# Coding Standards & Quality Guidelines

This document details the code formatting, naming, import, and engineering hygiene standards enforced across the `tracelyt-alerting` repository.

---

## 1. Automated Quality Tooling

Code quality is enforced automatically via Checkstyle in local builds and CI:
- **Checkstyle Configuration:** [`.checkstyle.xml`](../../.checkstyle.xml)
- **Plugin Version:** `maven-checkstyle-plugin:3.6.0` running Checkstyle `10.21.4`.
- **Enforcement Command:**
  ```bash
  ./mvnw -B -ntp checkstyle:check
  ```
- **Rule:** Zero checkstyle warnings or errors permitted in PRs.

---

## 2. Java Guidelines

### 2.1 Indentation and Line Length
- **Indentation:** 4 spaces (no tab characters).
- **Maximum Line Length:** 120 characters. Long expressions must wrap onto subsequent lines with 8-space continuation indentation.
- **Line Endings:** Unix-style LF (`\n`), enforced via `.gitattributes` and `.editorconfig`.

### 2.2 Imports
- **No Wildcard Imports:** `import java.util.*;` is strictly prohibited. Every class must be imported explicitly.
- **Import Ordering:**
  1. Static imports.
  2. Standard Java libraries (`java.*`, `javax.*`).
  3. Third-party libraries (`org.*`, `com.*`).
  4. Tracelyt internal packages (`io.tracelyt.*`).
- **Unused Imports:** Must be removed prior to pushing code.

### 2.3 Naming Conventions
- **Classes & Interfaces:** PascalCase (e.g., `AlertingApplication`, `AlertLifecycleManager`).
- **Methods & Fields:** camelCase (e.g., `evaluateCondition`, `pendingDuration`).
- **Constants:** UPPER_SNAKE_CASE (e.g., `DEFAULT_EVALUATION_DEADLINE_SECONDS`).
- **Packages:** Lowercase alphanumeric without underscores (e.g., `io.tracelyt.alerting.rule`).

### 2.4 Defensive Programming & Null Safety
- Favor immutability: mark fields `final` wherever possible.
- Avoid passing or returning `null`. Use `java.util.Optional` for methods where a return value may legitimately be absent.
- Validate all incoming method arguments defensively using standard preconditions:
  ```java
  Objects.requireNonNull(ruleId, "ruleId must not be null");
  ```

---

## 3. Documentation & Comments
- Public interfaces, classes, and methods must have clean, descriptive Javadoc comments.
- Do not add redundant or obvious comments (e.g., `// Constructor`).
- Comments must explain *why* an architectural or performance decision was made, not simply restate what the code does.

---

## 4. Markdown & YAML Standards
- **Markdown:** Follow CommonMark / GFM rules. Limit line length to 120 characters where practical. Use fenced code blocks with language indicators.
- **YAML:** 2-space indentation. Must validate cleanly against [`.yamllint`](../../.yamllint).
