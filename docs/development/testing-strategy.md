# Testing Strategy

This document outlines the testing pyramid, verification layers, and non-blocking testing principles for the `tracelyt-alerting` repository.

---

## 1. Testing Philosophy

The alerting subsystem is mission-critical: false negatives leave production incidents undetected, while false positives induce alert fatigue. Testing must be deterministic, comprehensive, and fast:

1. **Deterministic & Fast:** Baseline unit tests must execute in under 10 seconds locally without requiring network, containers, or background daemon processes.
2. **Zero External Dependencies for Smoke Tests:** Unit tests must never depend on live databases (PostgreSQL), Kafka brokers, or external notification channels (Slack/PagerDuty).
3. **No Mock Telemetry Presented as Real:** We do not generate synthetic query results or assert on fake telemetry data. Tests assert on actual component behavior, configuration loading, and contract adherence.
4. **Adherence to Shared Contracts:** Contract tests must validate serialization/deserialization against models defined in `tracelyt-specs`.

---

## 2. Testing Layers

| Layer | Purpose | Scope | Tooling | Execution Environment |
|---|---|---|---|---|
| **Unit Tests** | Verify isolated algorithms, threshold math, and state machines | Classes, pure functions, state-free logic | JUnit 5, AssertJ | In-process, `<forkCount>0</forkCount>`, fast |
| **Context Smoke Tests** | Verify Spring application context loading and bean wiring | `AlertingApplicationTest` | `@SpringBootTest` | Fast, in-process, tests port 8084 binding |
| **Contract Tests** *(Planned)* | Verify JSON/Protobuf schemas match `tracelyt-specs` | Rule & event DTO serialization | Jackson, JSONAssert | In-process, validates schema compatibility |
| **Adapter Integration Tests** *(Planned)* | Verify database persistence and transactional state | Repositories | Testcontainers (ephemeral PostgreSQL) | Isolated CI runners with Docker capability |
| **Reliability Tests** *(Planned)* | Verify failover, worker lease expiration, and missing data policies | Distributed recovery | Failure injection harness | Dedicated test cluster |

---

## 3. Current Implementation Status

In this foundation phase:
- **Implemented:** [`AlertingApplicationTest`](../../src/test/java/io/tracelyt/alerting/AlertingApplicationTest.java) verifies that the Spring Boot 3.4 context loads cleanly and binds required actuator endpoints.
- **Intentionally Omitted:** No database tests, no mock alert evaluations, and no artificial assertions (`assertTrue(true)`).

---

## 4. Running Tests

```bash
# Run tests via Maven Wrapper
./mvnw -B -ntp test

# Windows equivalent
mvnw.cmd -B -ntp test
```
