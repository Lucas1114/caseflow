# CaseFlow Project

## Purpose

CaseFlow is a resume-ready and GitHub-ready full-stack portfolio application built during a focused 7-day sprint. The project should demonstrate a complete React-to-database workflow without over-engineering.

This document is the source of truth for approved project scope. Update it when a meaningful project decision or milestone is approved.

## Repository Structure

```text
caseflow/
├── backend/
├── frontend/
├── docs/
│   └── CASEFLOW_PROJECT.md
├── docker-compose.yml
├── README.md
├── AGENTS.md
└── .gitignore
```

The structure should remain simple: one Spring Boot backend, one React frontend, one PostgreSQL service, and project documentation.

## Confirmed Technology Choices

### Frontend

- React 19.2.8
- TypeScript 6.0.2
- Vite 8.2.2
- React Router 7.18.2
- TanStack Query 5.102.3
- Native `fetch()`
- No Axios
- No React Hook Form yet
- No UI framework yet

### Backend

- Java 25
- Spring Boot 4.1.1
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL driver
- Bean Validation
- Flyway
- No Lombok
- No Spring Security yet
- No OpenAPI yet

### Infrastructure

- PostgreSQL runs only through Docker Compose.
- A local PostgreSQL installation must not be required.
- The Docker service is named `postgres` and uses the official `postgres:18.6-alpine3.24` image.
- Local development uses the `caseflow` database and `caseflow` user.
- Database files persist in the `caseflow-postgres-data` named volume.

## Day 1 Approved Scope

Day 1 builds the first vertical slice and nothing beyond it.

### Minimum User

- `id`
- `name`
- `email`

### Minimum Case

- `id`
- `title`
- `status`
- assigned user

### Case Workflow

The case status enum contains exactly:

- `OPEN`
- `IN_PROGRESS`
- `RESOLVED`
- `CLOSED`

### API

Day 1 exposes one required endpoint:

- `GET /api/cases`: return cases and the user assigned to each case.

The endpoint returns this small, explicit response shape:

```json
[
  {
    "id": 1,
    "title": "Review supplier contract renewal",
    "status": "OPEN",
    "assignedUser": {
      "id": 1,
      "name": "Maya Chen",
      "email": "maya.chen@example.com"
    }
  }
]
```

### Completion Criterion

Day 1 is complete only when this flow works end to end:

```text
React
  -> GET /api/cases
  -> Spring Boot
  -> PostgreSQL
  -> JSON
  -> React correctly renders cases
```

## Explicitly Out of Scope for Day 1

- `CaseActivity`
- authentication and authorization
- pagination
- search, filtering, and sorting
- forms
- Redis and Kafka
- Kubernetes
- CQRS and event sourcing
- microservices
- AI features
- complex observability
- performance work

Do not begin Day 2 until the user explicitly approves it.

## Working Milestones

1. [x] Establish repository rules and project documentation.
2. [x] Add Docker Compose PostgreSQL infrastructure.
3. [x] Scaffold and verify the Spring Boot backend.
4. [x] Add schema migrations and deterministic seed data.
5. [x] Implement and verify `GET /api/cases`.
6. [x] Scaffold and verify the React TypeScript frontend.
7. [x] Fetch and render cases with TanStack Query.
8. [x] Verify the full vertical slice and synchronize documentation.

## Current Status

- Day 1 is complete.
- Flyway creates and seeds the minimum `app_users` and `cases` tables in PostgreSQL.
- `GET /api/cases` returns the three seeded cases with their assigned users.
- React fetches the endpoint through the Vite development proxy and renders three case cards.
- Backend compilation, frontend lint/build, direct API checks, and browser rendering have been verified.
- Day 2 has not started and requires explicit approval.
