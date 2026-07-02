# IT Delivery Workbench

An AI-assisted IT delivery governance platform that improves IT delivery predictability by reducing ambiguity, rework, and unclear ownership across the SDLC.

**Core Philosophy:** AI is an assistant, never an autonomous agent. Humans approve all significant decisions. Every AI action is immutably audited.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.3, Spring Data JPA, Spring Security |
| Database | PostgreSQL 15, Flyway migrations |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | React 18, Vite, React Router v6, Axios, Vanilla CSS |
| Containerisation | Docker Compose |

---

## Project Structure

```
delivery-workbench/
├── backend/                     # Spring Boot monolith
│   ├── src/main/java/           # Java source code
│   │   └── com/deliveryworkbench/
│   │       ├── ai/              # AIService interface + MockAIService
│   │       ├── config/          # Security, OpenAPI config
│   │       ├── controller/      # REST controllers
│   │       ├── dto/             # Request/response DTOs
│   │       ├── entity/          # JPA entities + enums
│   │       ├── exception/       # Global exception handler
│   │       ├── mapper/          # MapStruct mappers
│   │       ├── repository/      # Spring Data JPA repositories
│   │       ├── security/        # SecurityUtils, MockAuthFilter
│   │       └── service/         # Business logic + WorkflowService
│   └── src/main/resources/
│       ├── application.yml      # Main config (uses env vars)
│       ├── application-dev.yml  # Local dev profile
│       └── db/migration/        # Flyway SQL migrations (V1, V2, V3)
├── frontend/                    # React SPA
│   └── src/
│       ├── api.js               # Axios instance (injects mock auth headers)
│       ├── components/          # Layout, shared UI
│       └── pages/               # 13 page components
├── docker-compose.yml           # DB + backend + frontend containers
└── IMPROVEMENT_PLAN.md         # Expansion roadmap (Phases 1–15)
```

---

## Running Locally

### Option A — Docker Compose (Recommended, full stack)

> Prerequisites: Docker Desktop running.

```bash
# Copy environment file
cp .env.example .env

# Start all services (db, backend, frontend)
docker-compose up --build

# Access:
# Frontend: http://localhost:5173
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Option B — Manual (local dev)

**Step 1: Start the database**
```bash
# Only start the DB container
docker-compose up -d db
```
Flyway migrations run automatically on backend startup (V1 schema, V2/V3 seed data).

**Step 2: Run the backend** (requires Java 21 and Maven)
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
The `dev` profile activates `MockAuthFilter` so the frontend's mock credentials are accepted.
Backend runs on: `http://localhost:8080`

**Step 3: Run the frontend** (requires Node.js 18+)
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on: `http://localhost:5173` and proxies API calls to `localhost:8080`.

---

## Authentication (MVP — Dev Mode)

Authentication is mocked for local development (Assumption A-04).

1. The React frontend's [Login page](frontend/src/pages/LoginPage.jsx) shows a role-selector dropdown.
2. The selected username and role are stored in `localStorage`.
3. The Axios client ([api.js](frontend/src/api.js)) injects them as `X-Mock-Username` and `X-Mock-Role` HTTP headers on every request.
4. On the backend, `MockAuthFilter` (activated by `dev`/`test` Spring profiles) reads these headers and creates a valid Spring Security authentication context.
5. This means `SecurityUtils.getCurrentUsername()` returns the correct user for audit logs and stage history — not `SYSTEM`.

**Available demo users** (populated by V3 migration):

| Username | Role |
|----------|------|
| `business.owner` | `BUSINESS_OWNER` |
| `system.analyst` | `SYSTEM_ANALYST` |
| `business.user` | `BUSINESS_USER` |
| `principal.engineer` | `SOLUTION_ARCHITECT` |
| `qa.user` | `QA` |
| `release.manager` | `RELEASE_MANAGER` |
| `admin` | `ADMIN` |

> Production Note: Replace `MockAuthFilter` with a real JWT filter and connect to OAuth2/LDAP for production deployment.

---

## Key Business Rules Enforced

| Rule | Description |
|------|-------------|
| BR-01 | Cannot move to `READY_FOR_DEVELOPMENT` unless Definition of Ready = `READY` |
| BR-02 | Cannot move to `READY_FOR_RELEASE` unless all Release Readiness checklist items are `true` |
| BR-03 | Every AI output is saved to `AIAuditLog` before returning (immutable) |
| BR-05 | AI may suggest, but humans must approve all checklists and decisions |
| BR-06 | Every status transition is recorded in `DeliveryStageHistory` (immutable) |
| BR-09 | `businessOwner` and `itOwner` must be assigned before `READY_FOR_ANALYSIS` |
| BR-10 | UAT must be signed off before `READY_FOR_RELEASE` |

---

## Running Tests

```bash
cd backend
mvn test
```
Tests run on H2 in-memory database (PostgreSQL not required). `MockAuthFilter` is active on the `test` profile.

---

## API Documentation

Swagger UI is available when the backend is running:
- Local: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

---

## Roadmap

See [IMPROVEMENT_PLAN.md](IMPROVEMENT_PLAN.md) for the 15-phase improvement roadmap including: Request Scoring, SLA Management, Bottleneck Detection, AI Next Best Action, Approval Workflow, Risk Register, and more.

---

## License

MIT License. See [LICENSE](LICENSE) for details.
