# Deployment Readiness & Runtime Operations

This document establishes the production deployment requirements, JVM tuning guidelines, graceful shutdown protocols, and container readiness criteria for `tracelyt-alerting`.

---

## 1. Container Readiness & Health Probes

In production environments (Kubernetes, Nomad, Docker Compose), instances must be configured with health probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8084
  initialDelaySeconds: 15
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8084
  initialDelaySeconds: 10
  periodSeconds: 5
  timeoutSeconds: 2
  failureThreshold: 2
```

- **Liveness:** Verifies JVM process health. Failure triggers container restart.
- **Readiness:** Verifies database connectivity and scheduler status. Failure temporarily removes the node from load balancer pools without killing the process.

---

## 2. Graceful Shutdown Protocol

When a container receives `SIGTERM`:
1. The readiness probe immediately transitions to `OUT_OF_SERVICE`.
2. Spring Boot's graceful shutdown phase initiates (configured via `server.shutdown: graceful`).
3. In-flight evaluations are granted a bounded grace period (30 seconds) to complete and persist active incident updates.
4. Active database leases are voluntarily released, allowing standby coordinator nodes to resume evaluation cycles instantly.

---

## 3. JVM Tuning & Resource Guidelines

For containerized Java 21 LTS deployments:

```bash
# Recommended baseline JVM flags for container environments
JAVA_OPTS="-XX:+UseG1GC \
           -XX:MaxRAMPercentage=75.0 \
           -XX:+ExitOnOutOfMemoryError \
           -XX:+HeapDumpOnOutOfMemoryError \
           -XX:HeapDumpPath=/var/log/tracelyt/dumps \
           -Djava.security.egd=file:/dev/urandom"
```

- **Garbage Collector:** Use G1GC with default low-latency targets to prevent stop-the-world pauses from disrupting scheduled evaluation timers.
- **Memory Boundaries:** `-XX:MaxRAMPercentage=75.0` dynamically calculates heap sizing based on container limits while reserving 25% for Metaspace and OS threads.
- **Fast Fail on OOM:** `-XX:+ExitOnOutOfMemoryError` ensures that an OOM event immediately terminates the degraded pod rather than leaving it in an unresponsive zombie state.
