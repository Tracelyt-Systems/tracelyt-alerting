# Technology Strategy & Decision Matrix

This document defines the evaluation criteria, trade-offs, and adoption status for programming languages, runtimes, and frameworks considered for `tracelyt-alerting`.

---

## 1. Technology Evaluation Matrix

| Technology | Proposed Role | Benefits | Alternatives Considered | Operational & Security Cost | Adoption Criteria | Decision Status |
|---|---|---|---|---|---|---|
| **Java 21 LTS** | Core alert lifecycle, rule APIs, incident state management, and bootstrap foundation | Native Virtual Threads (Loom), mature memory management, seamless integration with other Tracelyt services, strong typing | Go, Rust, Node.js | Moderate JVM memory footprint; robust automated CVE scanning | Baseline foundation | **`initial`** |
| **Spring Boot 3.4.x** | Application configuration, dependency injection, and health probes | Standardized actuator health endpoints, robust configuration binding, production-ready testing harness | Quarkus, Micronaut | Low runtime overhead when stripped of unnecessary starters | Baseline foundation | **`initial`** |
| **Apache Maven** | Reproducible build automation and dependency governance | Predictable BOM resolution, automated Checkstyle integration, vendor-neutral wrapper (`mvnw`) | Gradle | Low; requires strict dependency discipline | Baseline foundation | **`initial`** |
| **Go (Golang)** | High-concurrency evaluation workers | Sub-millisecond startup, minimal container memory footprint, native goroutines for high-frequency timer loops | Java Virtual Threads, Rust | Dual-language maintenance overhead, separate CI and linting toolchains | Worker thread saturation in Java | **`candidate` / `deferred`** |
| **Rust / C++** | High-throughput streaming condition evaluator | Maximum CPU efficiency, zero-cost abstractions, deterministic memory usage | Java Vector API, Go | High build complexity, steep learning curve, unsafe memory hazards (C/C++) | Measurable CPU bottleneck in condition evaluation | **`deferred`** |
| **Python** | Machine learning and AI-assisted investigation | Broad ML ecosystem, rapid data science experimentation | Java Deep Learning, Go | High memory consumption, runtime GIL constraints | Belong exclusively in `tracelyt-ai-investigator` | **`deferred` (External)** |
| **Kafka / Message Broker** | Event-driven alert state publishing | High-throughput event streaming, distributed replayability | In-memory outbox, transactional DB polling | Broker cluster operations, partition management | Stage 5/6 scale requirements | **`deferred`** |
| **Redis / Distributed Cache** | Sliding deduplication windows and distributed worker leases | In-memory sub-millisecond key-value operations, native TTLs | Relational database leases | Additional operational stateful service to manage | Multi-node worker scaling | **`deferred`** |

---

## 2. Decision Status Definitions
- **`initial`:** Selected and actively implemented in the current repository scaffold.
- **`candidate`:** Approved architectural candidate for future stages, requiring a formal ADR before implementation.
- **`deferred`:** Documented as a future possibility; barred from current implementation.
- **`rejected`:** Explicitly evaluated and rejected for this subsystem.
