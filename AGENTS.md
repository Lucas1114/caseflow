# CaseFlow Working Rules

Read `docs/CASEFLOW_PROJECT.md` before making project-level decisions. Treat it as the source of truth for approved scope and technical choices.

## Scope

- Treat this project as a 7-day portfolio sprint.
- Work only on the currently approved day.
- Do not begin a later day without explicit user approval.
- Prefer the simplest commercial-style implementation that demonstrates a real full-stack feature.
- Prioritize React and TypeScript learning and evidence. Keep Java and Spring Boot code straightforward.
- Do not add dependencies or architectural layers unless they are needed by the approved scope.

## Working Style

- Inspect relevant files and Git status before changing the project.
- Work incrementally and keep each change small enough to inspect and verify.
- Explain the purpose and approach before each meaningful implementation step.
- Explain React and TypeScript concepts when they first appear.
- Do not generate the entire application in one pass.
- Do not redo completed work or modify unrelated files.
- Verify changes in proportion to their risk.
- Keep `README.md` and `docs/CASEFLOW_PROJECT.md` synchronized at meaningful milestones.

## Daily Task Handoff

- Use a separate Codex task for each sprint day.
- Name each task `Day 1`, `Day 2`, and so on, matching the active sprint day.
- At the start of a new daily task, read this file and `docs/CASEFLOW_PROJECT.md`, inspect Git status and recent commits, and confirm the previous day's milestone before proposing changes.
- Treat repository documentation and committed code as the durable handoff between tasks; do not rely on prior conversation history being available.
- At the end of each day, synchronize project documentation, verify the approved completion criterion, commit and push the work, and leave the repository clean.
- Do not begin implementation for a new day until that day's scope is explicitly approved.

## Environment

- Use Java 25 and Maven for the backend.
- Use Node.js and npm for the frontend.
- Run PostgreSQL only through Docker Compose. Never require a local PostgreSQL installation.
- Keep the repository as a single backend, a single frontend, and shared project documentation.

## Strict Non-Goals Before MVP

Do not introduce authentication, authorization, pagination, search, filtering, sorting, forms, Redis, Kafka, Kubernetes, CQRS, event sourcing, microservices, AI features, complex observability, performance work, load testing, or other unapproved features.

## Day 1

The only Day 1 completion criterion is:

`React -> GET /api/cases -> Spring Boot -> PostgreSQL -> JSON -> React renders cases`

Implement only the minimum `User` and `Case` model described in `docs/CASEFLOW_PROJECT.md`. Do not implement `CaseActivity` or begin Day 2 during Day 1.
