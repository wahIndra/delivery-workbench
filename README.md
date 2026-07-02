# IT Delivery Workbench

An **AI-assisted IT delivery governance platform** that improves development delivery speed by standardising demand intake, reducing unclear requirements, tracking delivery stages, and measuring bottlenecks across the SDLC.

> **AI is an assistant only.** Humans retain full accountability for all approvals, design, development, test sign-offs, and release decisions.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start (Docker Compose)](#quick-start-docker-compose)
- [Local Development](#local-development)
- [Role Matrix](#role-matrix)
- [API Documentation](#api-documentation)
- [Business Rules](#business-rules)
- [AI Guardrails](#ai-guardrails)
- [Environment Variables](#environment-variables)
- [Implementation Progress](#implementation-progress)

---

## Overview

### Flow

```
Business Request
  → AI-assisted clarification
  → Requirement refinement
  → Definition of Ready validation
  → Impact analysis
  → Delivery tracking
  → QA preparation
  → Release readiness
  → Lead time dashboard
```

### Demo Users

| Username | Role | Access |
|----------|------|--------|
| business.user | BUSINESS_USER | Create and submit requests, answer clarifications |
| business.owner | BUSINESS_OWNER | Approve requirements, sign off UAT |
| system.analyst | SYSTEM_ANALYST | Manage requirements, DoR, AI clarification |
| principal.engineer | PRINCIPAL_ENGINEER | Impact analysis, solution design |
| developer | DEVELOPER | Development status updates |
| qa.user | QA | Test scenarios, SIT/UAT tracking |
| release.manager | RELEASE_MANAGER | Release readiness, deployment gating |
| management.viewer | MANAGEMENT_VIEWER | Read-only dashboard access |
| admin | ADMIN | User and master data management |

Default password for all demo users: `Password123!`

---

## Architecture

```
delivery-workbench/
├── backend/          Java 21 + Spring Boot 3.x + PostgreSQL + Flyway
├── frontend/         React 18 + Vite + Vanilla CSS
├── docker-compose.yml
├── .env.example
└── README.md
```

### Backend Packages

| Package | Responsibility |
|---------|---------------|
| controller | REST endpoints — validates DTOs, delegates to service |
| service | Business logic, status gates, AI orchestration |
| repository | Spring Data JPA interfaces |
| entity | JPA entities (never returned directly from REST) |
| dto | API request/response objects |
| mapper | MapStruct entity ↔ DTO converters |
| security | JWT filter, UserDetails, RBAC |
| config | Spring beans, CORS, OpenAPI |
| ai | AIService interface + MockAIService |
| dashboard | Lead time and bottleneck metrics |
| exception | Global exception handler |

---

## Prerequisites

- Java 21+
- Maven 3.9+ (or use included `mvnw`)
- Node.js 20+
- Docker Desktop (for Docker Compose)
- PostgreSQL 15 (if running locally without Docker)

---

## Quick Start (Docker Compose)

```bash
# 1. Clone the repository
git clone <repo-url>
cd delivery-workbench

# 2. Create your environment file
cp .env.example .env
# Edit .env — change DB_PASSWORD and JWT_SECRET before any shared deployment

# 3. Start all services
docker compose up -d

# 4. Check health
curl http://localhost:8080/api/health

# 5. Open the application
# Frontend: http://localhost:5173
# API Docs: http://localhost:8080/swagger-ui.html
```

### Stopping

```bash
docker compose down
# To also remove database volume:
docker compose down -v
```

---

## Local Development

### Backend

```bash
cd backend

# Start PostgreSQL via Docker (only the db service)
docker compose -f ../docker-compose.yml up db -d

# Run the backend
mvn spring-boot:run

# Run tests
mvn test
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# App: http://localhost:5173
```

---

## Role Matrix

| Action | BU | BO | SA | PE | DEV | QA | RM | MV | ADMIN |
|--------|----|----|----|----|-----|----|----|----|-------|
| Create request | ✓ | | | | | | | | |
| Submit request | ✓ | | | | | | | | |
| Generate AI clarification | | | ✓ | | | | | | |
| Answer clarification | ✓ | | | | | | | | |
| Create/update requirement | | | ✓ | | | | | | |
| Complete DoR checklist | | | ✓ | | | | | | |
| Create impact analysis | | | | ✓ | | | | | |
| Update delivery status | | | ✓ | ✓ | ✓ | ✓ | ✓ | | |
| Generate QA scenarios | | | | | | ✓ | | | |
| Complete release readiness | | | | | | | ✓ | | |
| View dashboard | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Manage users | | | | | | | | | ✓ |

BU=Business User, BO=Business Owner, SA=System Analyst, PE=Principal Engineer,
DEV=Developer, QA=QA Engineer, RM=Release Manager, MV=Management Viewer

---

## API Documentation

Once running, visit: **http://localhost:8080/swagger-ui.html**

OpenAPI JSON: **http://localhost:8080/api-docs**

### Key Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/auth/login | Authenticate and receive JWT |
| GET | /api/health | Health check (no auth) |
| GET | /api/requests | List delivery requests |
| POST | /api/requests | Create new delivery request |
| POST | /api/requests/{id}/submit | Submit a draft request |
| POST | /api/requests/{id}/status | Change status |
| GET | /api/requests/{id}/history | Stage history |
| POST | /api/requests/{id}/clarifications/ai-generate | AI clarification questions |
| POST | /api/requests/{id}/requirements/ai-generate | AI user story + AC |
| POST | /api/requests/{id}/impact/ai-generate | AI impact analysis draft |
| POST | /api/requests/{id}/qa-scenarios/ai-generate | AI test scenarios |
| POST | /api/requests/{id}/release-readiness/ai-generate | AI release checklist |
| GET | /api/dashboard | Lead time and metrics |
| GET | /api/ai-audit-logs | All AI audit logs |

---

## Business Rules

| Rule | Description |
|------|-------------|
| BR-01 | Request cannot move to READY_FOR_DEVELOPMENT unless DoR readyStatus == READY |
| BR-02 | Request cannot move to READY_FOR_RELEASE unless ReleaseReadiness.readyForRelease == true |
| BR-03 | Every AI output is saved to AIAuditLog before returning |
| BR-04 | AI clarification questions are editable drafts — humans send to business |
| BR-05 | AI never approves readiness, release, or requirement signoff |
| BR-06 | All status changes recorded in DeliveryStageHistory |
| BR-07 | MANAGEMENT_VIEWER has read-only access |
| BR-08 | ADMIN manages users and master data |
| BR-09 | businessOwner and itOwner required before READY_FOR_ANALYSIS |
| BR-10 | UAT signoff required before READY_FOR_RELEASE |

---

## AI Guardrails

The AI integration strictly follows these non-negotiable rules:

- ✅ AI **may** suggest clarification questions
- ✅ AI **may** draft user stories and acceptance criteria
- ✅ AI **may** draft impact analysis
- ✅ AI **may** draft test scenarios
- ✅ AI **may** draft release checklist
- ❌ AI **must not** approve requirements
- ❌ AI **must not** approve technical design
- ❌ AI **must not** approve release readiness
- ❌ AI **must not** change workflow status
- ❌ AI **must not** merge code
- ❌ AI **must not** deploy applications
- ❌ AI **must not** access secrets or production credentials

---

## Environment Variables

See [.env.example](.env.example) for all available variables.

| Variable | Description | Default |
|----------|-------------|---------|
| DB_NAME | PostgreSQL database name | deliveryworkbench |
| DB_USERNAME | PostgreSQL username | dwuser |
| DB_PASSWORD | PostgreSQL password | *required* |
| JWT_SECRET | JWT signing secret (min 32 chars) | *required in prod* |
| JWT_EXPIRATION_MS | Token expiry in milliseconds | 86400000 (24h) |
| CORS_ALLOWED_ORIGINS | Comma-separated allowed origins | http://localhost:5173 |
| VITE_API_BASE_URL | Frontend API base URL | http://localhost:8080 |

> **Security**: Never commit `.env` to version control. Rotate `JWT_SECRET` and `DB_PASSWORD` before any shared deployment.

---

## Implementation Progress

| Step | Description | Status |
|------|-------------|--------|
| 1 | Project structure, Spring Boot skeleton, Docker Compose, README | ✅ Complete |
| 2 | Core entities, enums, repositories, DTOs, mappers, Flyway migrations | ⬜ Pending |
| 3 | DeliveryRequest APIs and workflow status transition service | ⬜ Pending |
| 4 | Definition of Ready checklist and BR-01 gate | ⬜ Pending |
| 5 | ClarificationQuestion APIs and MockAIService | ⬜ Pending |
| 6 | Requirement APIs and AI-generated user story + AC | ⬜ Pending |
| 7 | ImpactAnalysis APIs and AI-generated draft | ⬜ Pending |
| 8 | QATestScenario APIs and AI-generated scenarios | ⬜ Pending |
| 9 | ReleaseReadiness APIs and BR-02 / BR-10 gates | ⬜ Pending |
| 10 | AIAuditLog and DeliveryStageHistory read-only APIs | ⬜ Pending |
| 11 | Dashboard metrics API | ⬜ Pending |
| 12 | React frontend — all 13 pages | ⬜ Pending |
| 13 | Seed data, unit tests, final README | ⬜ Pending |

---

*Built with Java 21 · Spring Boot 3 · React 18 · PostgreSQL 15 · Docker Compose*


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
