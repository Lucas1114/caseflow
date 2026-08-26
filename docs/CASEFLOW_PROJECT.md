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
│   ├── CASEFLOW_PROJECT.md
│   └── DAY_2_PROMPT.md
├── docker-compose.yml
├── README.md
├── AGENTS.md
└── .gitignore
```

The structure should remain simple: one Spring Boot backend, one React frontend, one PostgreSQL service, and project documentation.

## Sprint Task Workflow

- Use one Codex task per sprint day.
- Name tasks `Day 1`, `Day 2`, through `Day 7`.
- Every new task begins by reading `AGENTS.md` and this document, then inspecting Git status and recent commits.
- The repository is the durable handoff between daily tasks.
- A new day begins with a scope proposal and requires explicit approval before implementation.
- End each completed day with synchronized documentation, verification, a clean Git state, and a pushed commit.

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

## Day 2 Approved Scope

Day 2 adds a read-only case details flow. A user can select a case from the
existing list, navigate to `/cases/:caseId`, and see the case title, status,
identifier, and assigned user.

### API

- `GET /api/cases/{id}`: return one case and its assigned user.
- Return HTTP `404` when the requested case does not exist.
- Reuse the Day 1 response shape; do not change the database schema.

### Completion Criterion

Day 2 is complete only when this flow works end to end:

```text
User selects a case
  -> React navigates to /cases/:caseId
  -> GET /api/cases/{id}
  -> Spring Boot loads the case from PostgreSQL
  -> JSON returns the case and assigned user
  -> React renders the case details
```

The frontend must also show clear loading and error or not-found states and
provide navigation back to the case list.

### Day 2 Implementation Boundaries

- Add the single-case backend lookup, endpoint, `404` handling, and focused tests.
- Add the typed frontend request, details route and page, list-to-detail link,
  and styling consistent with the existing interface.
- Do not add forms, mutations, dependencies, database migrations, or later-day
  features.

### Day 2 Milestones

1. [x] Document the approved scope and completion criterion.
2. [x] Add and test `GET /api/cases/{id}` with HTTP `404` handling.
3. [x] Add the typed single-case frontend request.
4. [x] Add list-to-detail navigation and the case details page.
5. [x] Verify loading, success, and not-found behavior.
6. [x] Verify the full Day 2 flow and synchronize documentation.

## Day 3 Approved Scope

Day 3 adds one focused mutation to the case details page. A user can select a
new workflow status and save it to PostgreSQL.

### API

- `PATCH /api/cases/{id}/status`: validate and persist a case status change.
- Accept a JSON body containing one `status` value from the existing
  `CaseStatus` enum.
- Return the existing `CaseResponse` shape after the update.
- Return HTTP `400` for a missing or invalid status and HTTP `404` when the case
  does not exist.

### Completion Criterion

Day 3 is complete only when this flow works end to end:

```text
User selects a new status on /cases/:caseId
  -> React sends PATCH /api/cases/{id}/status
  -> Spring Boot validates and persists the status
  -> PostgreSQL stores the change
  -> React displays the updated status
  -> returning to /cases shows the same updated status
```

The frontend must show a clear saving state and a useful mutation error. The
case details and case list query data must remain synchronized after success.

### Day 3 Implementation Boundaries

- Add the status request, transactional update, endpoint, and focused tests.
- Add a typed frontend mutation and a native status select with an explicit
  save action on the existing details page.
- Reuse the current schema, response shape, status enum, and dependencies.
- Do not add editing for other fields, general-purpose forms, optimistic
  updates, activity history, bulk actions, or later-day features.

### Day 3 Milestones

1. [x] Document the approved scope and completion criterion.
2. [x] Add and test `PATCH /api/cases/{id}/status`.
3. [x] Add the typed frontend status mutation.
4. [x] Add the status control and mutation states to the details page.
5. [x] Verify persistence and list/detail cache synchronization.
6. [x] Verify regressions and synchronize documentation.

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
- Day 2 is complete.
- `GET /api/cases/{id}` returns one case with its assigned user and returns
  HTTP `404` for a missing case.
- React links each case card to `/cases/:caseId`, fetches that individual case
  with TanStack Query, and renders loading, success, and not-found states.
- Backend tests, frontend lint/build, direct API checks, list-to-detail browser
  navigation, missing-case behavior, and visual rendering have been verified.
- Day 3 is complete.
- `PATCH /api/cases/{id}/status` validates and persists an existing workflow
  status and returns HTTP `400` or `404` for invalid requests.
- React provides a typed status control on the details page, displays saving
  and error states, and synchronizes the details and list query data after a
  successful update.
- Backend tests, frontend lint/build, direct PostgreSQL-backed API checks, and
  list-to-detail browser behavior have been verified.
