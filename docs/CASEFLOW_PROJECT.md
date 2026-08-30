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

## Day 4 Approved Scope

Day 4 adds case activity notes to the case details page. A user can record a
short operational note and see the case's activity history newest first.

### API

- `GET /api/cases/{id}/activities`: return the case's activities newest first.
- `POST /api/cases/{id}/activities`: validate and persist one activity note.
- Accept a JSON body containing a nonblank `note` of at most 1,000 characters.
- Return HTTP `400` for a missing, blank, or oversized note and HTTP `404` when
  the case does not exist.

### Completion Criterion

Day 4 is complete only when this flow works end to end:

```text
User enters an activity note on /cases/:caseId
  -> React sends POST /api/cases/{id}/activities
  -> Spring Boot validates and persists the activity
  -> PostgreSQL stores it against the case
  -> React clears the form and displays the new activity
  -> reloading the details page shows the same activity
```

The frontend must show activity loading, empty, saving, success, and useful
error states. Activities are displayed newest first.

### Day 4 Implementation Boundaries

- Add the `case_activities` table through a new Flyway migration.
- Add a small activity entity, repository, service, explicit API contracts, and
  focused tests for listing, creation, validation, ordering, and missing cases.
- Add typed frontend activity requests, a timeline, and a controlled native
  textarea form on the existing details page.
- Reuse the current dependencies and preserve all Day 1 through Day 3 behavior.
- Do not add activity editing or deletion, attachments, rich text, automatic
  status events, authors, threaded comments, pagination, general case editing,
  authentication, or later-day features.

### Day 4 Milestones

1. [x] Document the approved scope and completion criterion.
2. [x] Add the activity migration and persistence model.
3. [x] Add and test the activity list and create endpoints.
4. [x] Add typed frontend activity requests and timeline.
5. [x] Add the note form and mutation states.
6. [x] Verify persistence, ordering, regressions, and synchronize documentation.

## Day 5 Approved Scope

Day 5 adds case reassignment to another existing user from the details page.

### API

- `GET /api/users`: return existing users with id, name, and email.
- `PATCH /api/cases/{id}/assignee`: accept a non-null, positive
  `assignedUserId` and return the existing `CaseResponse` after persistence.
- Return HTTP `400` for an invalid request and `404` for a missing case or user.

### Completion Criterion

```text
User selects another assignee on /cases/:caseId
  -> React sends PATCH /api/cases/{id}/assignee
  -> Spring Boot validates the case and user
  -> PostgreSQL updates cases.assigned_user_id
  -> React displays the new assignee
  -> the case list and a reloaded details page show the same assignee
```

The frontend must show user loading, saving, success, and useful error states.
Saving is disabled when unchanged or pending. Update the detail query cache
and invalidate the case list after success.

### Day 5 Implementation Boundaries

- Add minimal user listing contracts, controller, service, and repository.
- Add the validated assignee request, transactional update, and focused tests.
- Add typed frontend requests and a controlled native select with explicit save.
- Reuse the existing foreign key, seed users, dependencies, and architecture;
  no database migration is needed. PostgreSQL remains Docker Compose-only.
- Preserve Day 1 through Day 4 behavior. Do not add user management,
  unassignment, multiple assignees, teams, assignment history, automatic activity
  entries, notifications, queues, search/filtering/sorting, authentication,
  general case editing, optimistic updates, or any Day 6 work.

### Day 5 Milestones

1. [x] Document the approved scope and completion criterion.
2. [x] Implement and test user listing and reassignment API.
3. [x] Add typed requests, assignee control, and cache synchronization.
4. [x] Verify API, PostgreSQL persistence, browser states, and regressions.
5. [x] Synchronize documentation for the Day 5 commit and push handoff.

### Day 5 Verification (2026-08-28)

- 19 backend tests pass. New tests exercise real user/case services with
  mocked repositories, including reassignment, unchanged assignments, field
  preservation, invalid IDs, missing users/cases, and user listing/empty data.
- Frontend lint and production build pass; no frontend test dependency added.
- Docker Compose PostgreSQL and Flyway confirm schema version 2; no migration.
- The running API returns existing users and expected `400`/`404` responses.
- Browser reassignment from Maya to Noah updates the details and list; reload
  preserves Noah. A direct SQL read confirms `assigned_user_id = 2` while the
  case status remains `OPEN`.
- A temporary localhost QA proxy outside the repository verifies user loading,
  disabled controls during save, success, failed-save selection retention,
  user-list errors, and successful retries. No production fault hooks added.
- Browser regressions verify status updates/list synchronization and activity
  creation/reload persistence. Original case #1 assignee and status were
  restored; a clearly labeled Day 5 regression note remains in local demo data.
- Visual inspection confirms the assignee control fits the existing interface.
- Day 6 has not been started.

## Day 6 Approved Scope

Day 6 adds a compact workflow summary above the case list. It shows the total
number of cases and the count in each existing workflow status so an operator
can understand the current queue at a glance.

### API

- `GET /api/cases/summary`: return `total`, `open`, `inProgress`, `resolved`,
  and `closed` counts calculated from PostgreSQL.
- Return zero for statuses that have no cases.

### Completion Criterion

```text
User opens /cases
  -> React requests GET /api/cases/summary
  -> Spring Boot runs a grouped status-count query
  -> PostgreSQL calculates the current totals
  -> React displays total and per-status counts
  -> changing a case status and returning to /cases shows refreshed counts
```

The summary must show clear loading and retryable error states without hiding
a successfully loaded case list.

### Day 6 Implementation Boundaries

- Add one grouped repository query, a small response contract, a read-only
  service method, controller endpoint, and focused tests.
- Add a typed frontend request and a small summary panel above the existing
  case grid.
- Invalidate the summary after a successful status mutation.
- Reuse the current schema, dependencies, enum, routing, and query architecture;
  no database migration is needed. PostgreSQL remains Docker Compose-only.
- Preserve Day 1 through Day 5 behavior. Do not add charts, dashboards,
  assignee metrics, search/filtering/sorting, pagination, due dates, priorities,
  case creation/editing, authentication, dependencies, or any Day 7 work.

### Day 6 Milestones

1. [x] Document the approved scope and completion criterion.
2. [x] Implement and test the workflow summary API.
3. [x] Add the typed summary query, panel, and cache synchronization.
4. [x] Verify PostgreSQL-backed counts, UI states, and regressions.
5. [x] Synchronize documentation for the Day 6 commit and push handoff.

### Day 6 Verification (2026-08-29)

- 21 backend tests pass. New tests cover the summary JSON contract, total
  calculation, per-status mapping, and zero values for missing status groups.
- Frontend lint and production build pass; no frontend test dependency added.
- Docker Compose PostgreSQL and Flyway confirm schema version 2 with no new
  migration. The summary endpoint returns total 3 with one open, one in
  progress, one resolved, and zero closed, matching a direct grouped SQL read.
- Browser checks confirm the summary's success, error, and successful retry
  states, plus readable desktop and mobile layouts.
- Changing case #1 from open to closed updates the case list summary to zero
  open and one closed. Restoring it to open restores the original counts.
- Existing case list/detail, user listing, assignee, and activity endpoints
  remain operational. Case #1 is restored to its original `OPEN` status and
  Maya Chen assignment; existing activity data was not changed.
- Day 7 has not been started.

## Day 7 Approved Scope

Day 7 completes the sprint with case intake. An operator can create a case,
assign it to an existing user, and continue work from the new case's details
page. New cases always begin in the `OPEN` workflow status.

### API

- `POST /api/cases`: validate and persist a new case.
- Accept a nonblank `title` of at most 200 characters and a positive,
  non-null `assignedUserId`.
- Trim the title, set the status to `OPEN` on the server, and return the
  existing `CaseResponse` with HTTP `201`.
- Return HTTP `400` for invalid input and `404` when the selected user does
  not exist.

### Completion Criterion

```text
User selects New case on /cases
  -> React loads existing assignees
  -> user enters a title and selects an assignee
  -> React sends POST /api/cases
  -> Spring Boot validates and persists an OPEN case
  -> PostgreSQL stores the case
  -> React navigates to /cases/{newId}
  -> the details page displays the new case
  -> returning to /cases shows the case and refreshed summary
```

The form must show clear assignee loading, error, empty, retry, validation,
submitting, and creation-error states. Failed submissions preserve entered
values, and reloading the successful result must show the persisted case.

### Day 7 Implementation Boundaries

- Add a small validated creation request, transactional service method,
  controller endpoint, and focused tests.
- Add a typed frontend request, `/cases/new` route, list-page action, and a
  controlled native form for title and existing assignee.
- Seed the new detail cache and invalidate the case list and summary after
  success before navigating to the new details page.
- Reuse the current table, foreign key, response contracts, dependencies, and
  architecture; no database migration is needed. PostgreSQL remains Docker
  Compose-only.
- Preserve Day 1 through Day 6 behavior. Do not add title editing, selectable
  initial status, user creation, unassigned cases, descriptions, priorities,
  due dates, search/filtering/sorting, pagination, deletion, bulk actions,
  attachments, notifications, automatic activities, authentication, new
  dependencies, deployment, or CI/CD work.

### Day 7 Milestones

1. [x] Document the approved scope and completion criterion.
2. [x] Implement and test case creation API.
3. [x] Add the typed creation request, route, form, and cache synchronization.
4. [x] Verify persistence, UI states, responsive behavior, and regressions.
5. [x] Synchronize final documentation for the Day 7 commit and push handoff.

### Day 7 Verification (2026-08-30)

- 25 backend tests pass. New tests cover HTTP `201`, trimmed title persistence,
  server-owned `OPEN` status, assignee mapping, invalid titles and assignee
  IDs, and a missing user without persistence.
- Frontend lint and production build pass; no frontend test dependency added.
- Docker Compose PostgreSQL and Flyway confirm schema version 2 with no new
  migration. Direct SQL confirms created titles, statuses, and assignee foreign
  keys match the API and browser submissions.
- API checks confirm successful creation plus expected `400` and `404`
  responses. Status and assignee mutations, user listing, activity listing,
  case details, and summary counts remain operational.
- Browser checks confirm disabled/valid form states, successful creation and
  details navigation, reload persistence, list/summary refresh, readable
  desktop/mobile layouts, and successful recovery from an assignee-list error.
- Two clearly labeled verification cases were removed after testing. The
  database is restored to the original three cases and six existing activity
  rows. Case #1 is `OPEN` and assigned to Maya Chen.
- The seven-day CaseFlow portfolio sprint is complete.

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
- Day 4 is complete.
- Flyway creates and seeds `case_activities`, linked to cases with a foreign
  key and indexed for newest-first retrieval.
- `GET /api/cases/{id}/activities` returns activity history and
  `POST /api/cases/{id}/activities` validates and persists a new note.
- React displays activity loading, empty, error, saving, and success states on
  the case details page and immediately adds a saved note to the timeline.
- Backend tests, frontend lint/build, direct PostgreSQL-backed API checks,
  browser creation, reload persistence, empty state, and list navigation have
  been verified.
- Day 5 is complete.
- `GET /api/users` lists existing users; `PATCH /api/cases/{id}/assignee`
  validates and persists reassignment using the existing foreign key.
- React provides a controlled assignee select with explicit save, useful
  loading/error/retry states, and synchronized details/list query data.
- Backend tests, frontend lint/build, Docker PostgreSQL API checks, browser
  persistence/state checks, and status/activity regressions have been verified.
- Day 6 is complete.
- `GET /api/cases/summary` uses a grouped PostgreSQL query and returns the total
  and a zero-safe count for every existing workflow status.
- React displays the independent summary above the case grid with loading,
  error/retry, responsive, and status-mutation cache refresh behavior.
- Backend tests, frontend lint/build, grouped SQL comparison, API checks,
  browser state/refresh checks, and Day 1 through Day 5 regressions have been
  verified.
- Day 7 is complete.
- `POST /api/cases` validates and persists a trimmed title, an existing
  assignee, and a server-owned initial `OPEN` status with HTTP `201`.
- React provides a responsive `/cases/new` intake form with assignee
  loading/error/empty/retry states, controlled validation, preserved input on
  creation errors, success navigation, and list/summary cache refresh.
- Backend tests, frontend lint/build, PostgreSQL/API checks, browser creation,
  error recovery, responsive rendering, and Day 1 through Day 6 regressions
  have been verified. The seven-day sprint is complete.
