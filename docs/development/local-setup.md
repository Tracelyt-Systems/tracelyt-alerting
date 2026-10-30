# Local Development Setup

This guide details the prerequisites, build instructions, and verification steps required to set up and develop on the `tracelyt-alerting` repository.

---

## 1. Prerequisites

Before working with the repository, ensure your development environment meets the following requirements:

| Component | Minimum Version | Recommended Version | Verification Command | Notes |
|---|---|---|---|---|
| **Java JDK** | 21 LTS | OpenJDK 21.0.x (Eclipse Temurin / MS OpenJDK) | `java -version` | LTS version required. Java 21 features used. |
| **Git** | 2.30+ | Latest Stable | `git --version` | Line endings handled via `.gitattributes`. |
| **Make** | 3.81+ | 4.x (Optional) | `make --version` | Windows users can use `mvnw.cmd` directly. |
| **Python** | 3.9+ | 3.11+ | `python --version` | Used for local documentation and linting validation. |

> **Notice:** An active internet connection is required during the first build to resolve Maven Wrapper and dependencies. Subsequent builds can operate completely offline using cached local repository artifacts (`~/.m2/repository`).

---

## 2. Repository Cloning & Layout

Clone the repository and inspect the workspace:

```bash
git clone https://github.com/Tracelyt-Systems/tracelyt-alerting.git
cd tracelyt-alerting
```

---

## 3. Building the Project

The repository uses the Apache Maven Wrapper (`mvnw` for Linux/macOS, `mvnw.cmd` for Windows) to ensure reproducible builds without requiring a pre-installed global Maven installation.

### Linux / macOS

```bash
# Validate project structure and dependencies
./mvnw -B -ntp validate

# Run Checkstyle formatting checks
./mvnw -B -ntp checkstyle:check

# Run unit tests
./mvnw -B -ntp test

# Build packaged application JAR
./mvnw -B -ntp package -DskipTests
```

### Windows (PowerShell or CMD)

```cmd
:: Set JAVA_HOME if not configured globally
set JAVA_HOME=C:\Path\To\Your\jdk-21

:: Run validation, Checkstyle, and tests
mvnw.cmd -B -ntp clean test checkstyle:check

:: Package the application JAR
mvnw.cmd -B -ntp package -DskipTests
```

---

## 4. Makefile Shortcuts (Optional)

If `make` is installed on your workstation, you can run verified targets:

```bash
make help        # Display list of available targets
make validate    # Run validation and Checkstyle checks
make test        # Run unit tests
make build       # Compile and package application JAR
make docs-check  # Validate Markdown documentation links & secrets
make clean       # Clean target directory
```

---

## 5. Running the Application Scaffold

To run the bootstrap application locally:

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Once started on port `8084`, verify the actuator health endpoint:

```bash
curl http://localhost:8084/actuator/health
# Response: {"status":"UP"}
```

> **Important Boundary:** The bootstrap application verifies configuration and health checks only. It intentionally exposes **no alert evaluation endpoints, no mock incidents, and connects to no databases or brokers**.

---

## 6. Documentation and Validation Tools

The repository includes a dedicated documentation validator in `tools/validate_docs.py` that verifies local Markdown link integrity and scans for accidentally committed secrets or tokens.

Run the tool locally:

```bash
python tools/validate_docs.py
```

---

## 7. Troubleshooting Common Issues

### Issue: `JAVA_HOME is not defined correctly`
- **Solution:** Verify `java -version` returns Java 21+. Set `JAVA_HOME` pointing to the JDK root folder (not the `bin` directory).

### Issue: Checkstyle violation: `Line is longer than 120 characters`
- **Solution:** Reformat lines in Java files to stay within 120 characters, or run `./mvnw checkstyle:check` to identify line numbers.

### Issue: Native memory allocation or Metaspace error during build
- **Solution:** Set `MAVEN_OPTS="-Xmx512m -XX:MaxMetaspaceSize=256m"` before invoking `mvnw`.
