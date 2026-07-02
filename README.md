# IT Delivery Workbench

An AI-assisted IT delivery governance platform designed to streamline and govern the end-to-end software delivery lifecycle.

## Overview
The IT Delivery Workbench bridges the gap between Business, Analysis, QA, and Release Management by providing a single source of truth for Delivery Requests. It ensures strict governance (e.g., Definition of Ready, Release Readiness) while leveraging AI to accelerate drafting tasks like User Stories, Impact Analysis, and QA Scenarios.

**Core Philosophy:** AI is an assistant, not an autonomous agent. A human must always review and approve AI outputs (BR-05). All AI generation is strictly audited.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.2, PostgreSQL, Flyway, Spring Data JPA
- **Frontend:** React, Vite, React Router, Axios, Vanilla CSS
- **AI Integration:** Mocked AI Service (Spring AI structure prepared for future phases)

## Project Structure
```
delivery-workbench/
├── backend/               # Spring Boot Application
│   ├── src/main/java      # Java source code
│   └── src/main/resources # Application config & Flyway migrations
├── frontend/              # React SPA
│   ├── src/               # React components, pages, API utils
│   └── vite.config.js     # Vite configuration (proxy to backend)
└── docker-compose.yml     # PostgreSQL Database container
```

## Setup & Running Locally

### 1. Database
Ensure Docker is running, then start the PostgreSQL database:
```bash
docker-compose up -d
```
The database runs on `localhost:5432`. Flyway migrations run automatically when the backend starts.

### 2. Backend
Requires Java 21 and Maven.
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
The backend API runs on `http://localhost:8080`.
- **API Documentation (Swagger UI):** `http://localhost:8080/swagger-ui.html`

### 3. Frontend
Requires Node.js (v18+).
```bash
cd frontend
npm install
npm run dev
```
The frontend runs on `http://localhost:5173`.

## Authentication (MVP Security Note)
For this MVP, authentication is mocked (Assumption A-04). 
When you visit the frontend login page, you can select from a dropdown of predefined users (e.g., Business Owner, System Analyst, Release Manager). This sets a simulated session role, allowing you to bypass a complex OAuth2 setup for demo purposes. Real JWT and Spring Security will be implemented in future production phases.

## Features
- **Dashboard:** Lead time metrics and bottleneck tracking.
- **Request Tracking:** Stateful workflow engine (`DRAFT` to `RELEASED`).
- **Clarifications:** Q&A thread with AI generation capability.
- **Requirement Refinement:** AI-assisted User Stories and Acceptance Criteria.
- **Definition of Ready (DoR):** 12-point readiness gate (BR-01).
- **Impact Analysis:** Cross-system impact drafts.
- **QA Scenarios:** Test case management.
- **Release Readiness:** Master release gate and checklist (BR-02, BR-10).
- **AI Audit Log:** Immutable ledger of AI interactions (SG-05).

## License
MIT License. See [LICENSE](LICENSE) for details.
