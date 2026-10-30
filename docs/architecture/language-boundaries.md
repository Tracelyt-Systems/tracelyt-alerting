# Multi-Language Boundaries & Monorepo Policy

This document establishes the multi-language policy, cross-language communication standards, and adoption prerequisites for `tracelyt-alerting`.

---

## 1. Single-Runtime Foundation Principle

To avoid premature complexity, `tracelyt-alerting` adopts **Java 21 LTS** as its sole initial runtime.

We strictly reject the practice of introducing multiple languages (e.g., adding an empty Go module or placeholder Rust crate) merely to create the appearance of advanced engineering. Every language introduced into the repository must serve a proven, measured requirement.

---

## 2. Language Adoption Gates

Before any secondary runtime (e.g., Go for evaluation workers or Rust for native parsing) may be introduced, the following gates must be satisfied:

1. **Empirical Bottleneck Evidence:** Profiling data demonstrating that the existing Java runtime cannot meet latency or throughput targets (e.g., GC pause times exceeding evaluation deadlines).
2. **Approved Architecture Decision Record (ADR):** A comprehensive ADR detailing the problem, alternatives, operational costs, and boundary contract.
3. **Language-Neutral Interface:** Cross-language integration must occur across well-defined network boundaries (gRPC/HTTP) governed by `tracelyt-specs`, never via brittle in-process FFI bindings.
4. **Complete CI & Tooling Infrastructure:** Dedicated CI workflows for linting, formatting, vulnerability scanning, and testing for the new language.

---

## 3. Boundary Rules Between Languages

- **No Shared Mutable Memory:** Out-of-process communication using versioned Protobuf or JSON schemas only.
- **Independent Failure Domains:** A failure in an analytical worker process must never crash the central lifecycle coordinator.
- **Contract Governance:** All cross-language message schemas belong in `tracelyt-specs`.
