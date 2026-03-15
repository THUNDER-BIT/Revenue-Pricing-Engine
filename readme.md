# Revenue Pricing Engine

A precision-focused billing tool that calculates **Total Contract Value (TCV)** for multi-year SaaS ramp deals — the kind commonly used in enterprise software sales.

Built to demonstrate production-grade Spring Boot development: 3-tier architecture, financial precision using BigDecimal, full test coverage, and CI/CD via GitHub Actions.


---

## What It Does

Enterprise SaaS deals often use "ramp pricing" — a customer pays less in Year 1, more in Year 2, and full price in Year 3. Calculating the total value of these contracts manually is error-prone.

This engine lets you define any number of pricing segments (monthly price + duration), hit Calculate, and instantly get the exact TCV — with no floating-point rounding errors.

**Example:**
| Segment | Monthly Price | Duration | Value |
|---------|--------------|----------|-------|
| Year 1  | $1,000       | 12 months | $12,000 |
| Year 2  | $1,500       | 12 months | $18,000 |
| Year 3  | $2,000       | 12 months | $24,000 |
| **TCV** |              |           | **$54,000** |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.5 |
| Frontend | Thymeleaf, Bootstrap 5, JavaScript (Fetch API) |
| Database | H2 (in-memory) |
| ORM | Spring Data JPA / Hibernate |
| Testing | JUnit 5, Mockito, Spring WebMvcTest |
| Build | Maven |
| CI/CD | GitHub Actions |

---

## Quick Start

**Prerequisites:** Java 17+, Maven (or use the included wrapper)
```bash
# Clone the repo
git clone https://github.com/THUNDER-BIT/revenue-pricing-engine.git
cd revenue-pricing-engine

# Run the app
./mvnw spring-boot:run
```

Open your browser at **http://localhost:8080**

**Run tests:**
```bash
./mvnw test
```

Expected output: `Tests run: 21, Failures: 0, Errors: 0`

---

## Sample API Request

The engine exposes a REST endpoint directly:
```bash
POST /api/v1/billing/calculate
Content-Type: application/json

[
  { "monthlyPrice": 1000.00, "durationMonths": 12 },
  { "monthlyPrice": 1500.00, "durationMonths": 12 },
  { "monthlyPrice": 2000.00, "durationMonths": 12 }
]
```

**Response:**
```
54000.00
```

---

## Architecture

The project follows a 3-tier architecture with strict separation of concerns:
```
UI (Thymeleaf + Bootstrap)
        │  JSON via Fetch API
        ▼
BillingRestController     ← API Gateway, handles HTTP
        │
        ▼
BillingService            ← Business logic, BigDecimal math
        │
        ▼
RampRepository            ← Spring Data JPA, persists to H2
```

**Key engineering decisions:**

- **BigDecimal over double/float** — financial calculations require exact arithmetic. Standard floating-point gives `0.1 + 0.2 = 0.30000000000000004`. BigDecimal gives `0.30`. In billing systems, rounding errors are unacceptable.
- **Null filtering before persistence** — the service filters null elements from the input list before calling `saveAll()`, preventing `IllegalArgumentException` from Spring Data when the list contains null entries.
- **Decoupled layers** — the controller never contains business logic. The service never knows about HTTP. Each layer is independently testable.

---

## Test Coverage

21 tests across 3 test classes:

| Test Class | Tests | What It Covers |
|-----------|-------|---------------|
| `BillingServiceTest` | 13 | TCV calculation, BigDecimal precision, null handling, DB interaction |
| `BillingRestControllerTest` | 7 | HTTP status codes, JSON serialization, input validation |
| `BillingUIControllerTest` | 1 | View resolution for the Thymeleaf template |

---

## CI/CD

Every push to `main` triggers a GitHub Actions workflow that compiles the project and runs all 21 tests on Ubuntu. 

[![Java CI](https://github.com/THUNDER-BIT/revenue-pricing-engine/actions/workflows/ci.yml/badge.svg)](https://github.com/THUNDER-BIT/revenue-pricing-engine/actions/workflows/ci.yml)