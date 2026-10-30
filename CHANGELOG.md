# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project architecture and engineering foundation for `tracelyt-alerting`.
- Java 21 LTS + Spring Boot 3.4.3 application bootstrap with Actuator health endpoints on port `8084`.
- Checkstyle 10.21.4 configuration with 120-character line limit and strict import order rules.
- Pinned GitHub Actions workflows for CI, documentation validation, and dependency reviews.
- Architecture Decision Records (ADRs) establishing language selection and service boundaries.
- Comprehensive architectural documentation: domain model, alert evaluation, alert lifecycle, incident lifecycle, notification boundary, state management, distributed evaluation, missing data semantics, failure model, and scalability.
- Security threat modeling (STRIDE), tenant isolation boundaries, data privacy guidelines, and supply chain security.
- Verification tooling (`tools/validate_docs.py`) validating Markdown relative link integrity and secret scanning.
