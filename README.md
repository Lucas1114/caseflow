# CaseFlow

CaseFlow is a full-stack case-management portfolio project built as a focused 7-day sprint. It uses React and TypeScript on the frontend, Spring Boot and Java on the backend, and PostgreSQL through Docker.

## Current Status

Day 1 is complete. The first vertical slice is verified end to end:

```text
React -> GET /api/cases -> Spring Boot -> PostgreSQL -> JSON -> React rendering
```

The current application displays seeded cases and each case's assigned user.
Day 2 is complete. Each case links to a read-only details page at
`/cases/:caseId`, backed by `GET /api/cases/{id}`. Missing cases return HTTP
`404` and display a clear not-found state in React.
Day 3 is complete. A user can update a case's workflow status from the details
page. The change is persisted through `PATCH /api/cases/{id}/status`, and the
details and case list views remain synchronized.
Day 4 is complete. A user can add a persistent activity note from the case
details page and see the case's activity history newest first. The form and
timeline provide clear loading, empty, saving, success, and error states.

## Technology

- React 19, TypeScript, Vite
- React Router and TanStack Query
- Native `fetch()` for API requests
- Java 25 and Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA, Bean Validation, and Flyway
- PostgreSQL 18.6 through Docker Compose
- Maven and npm

## Repository Structure

```text
caseflow/
├── backend/             # Spring Boot API and Flyway migrations
├── frontend/            # React and TypeScript client
├── docs/
│   └── CASEFLOW_PROJECT.md
├── docker-compose.yml   # PostgreSQL only
├── README.md
├── AGENTS.md
└── .gitignore
```

See [`docs/CASEFLOW_PROJECT.md`](docs/CASEFLOW_PROJECT.md) for the approved scope and project decisions.

## Prerequisites

- Java 25
- Maven 3.9+
- Node.js 24 LTS
- npm 11+
- Docker and Docker Compose

PostgreSQL runs only through Docker Compose. A local PostgreSQL installation is not required.

## Run Locally

Start PostgreSQL from the repository root:

```bash
docker compose up -d --wait postgres
```

Start the backend in a second terminal:

```bash
cd backend
mvn spring-boot:run
```

Start the frontend in a third terminal:

```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:5173/cases](http://localhost:5173/cases). Vite proxies `/api` requests to the backend at `http://localhost:8080`.

The API can also be checked directly:

```bash
curl http://localhost:8080/api/cases
curl http://localhost:8080/api/cases/1
curl http://localhost:8080/api/cases/1/activities
curl -X PATCH \
  -H 'Content-Type: application/json' \
  -d '{"status":"IN_PROGRESS"}' \
  http://localhost:8080/api/cases/1/status
curl -X POST \
  -H 'Content-Type: application/json' \
  -d '{"note":"Customer supplied the missing document."}' \
  http://localhost:8080/api/cases/1/activities
```

## Development Checks

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

## Local Database

The development connection is:

- host: `localhost`
- port: `5432`
- database: `caseflow`
- username: `caseflow`
- password: `caseflow`

These credentials are intentionally simple and are for local development only. Database files persist in the `caseflow-postgres-data` named volume.

Stop the application database without deleting its data:

```bash
docker compose down
```
