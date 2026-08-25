import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { ApiError, getCase, type CaseStatus } from '../api/cases'

const statusLabels: Record<CaseStatus, string> = {
  OPEN: 'Open',
  IN_PROGRESS: 'In progress',
  RESOLVED: 'Resolved',
  CLOSED: 'Closed',
}

export function CaseDetailsPage() {
  const { caseId: caseIdParam } = useParams<{ caseId: string }>()
  const caseId = Number(caseIdParam)
  const hasValidCaseId = Number.isInteger(caseId) && caseId > 0

  const caseQuery = useQuery({
    queryKey: ['cases', caseId],
    queryFn: () => getCase(caseId),
    enabled: hasValidCaseId,
    retry: false,
  })

  if (!hasValidCaseId) {
    return <DetailsMessage message="That case address is not valid." />
  }

  if (caseQuery.isPending) {
    return (
      <main className="app-shell">
        <p className="state-message" role="status">
          Loading case…
        </p>
      </main>
    )
  }

  if (caseQuery.isError) {
    const message =
      caseQuery.error instanceof ApiError && caseQuery.error.status === 404
        ? `Case #${caseId} was not found.`
        : caseQuery.error.message

    return <DetailsMessage message={message} />
  }

  const caseItem = caseQuery.data

  return (
    <main className="app-shell">
      <Link className="back-link" to="/cases">
        ← Back to cases
      </Link>

      <article className="case-details">
        <div className="case-details__heading">
          <div>
            <p className="eyebrow">Case #{caseItem.id}</p>
            <h1>{caseItem.title}</h1>
          </div>
          <span className={`status status--${caseItem.status.toLowerCase()}`}>
            {statusLabels[caseItem.status]}
          </span>
        </div>

        <section className="case-details__section" aria-labelledby="assigned-user-heading">
          <h2 id="assigned-user-heading">Assigned user</h2>
          <div className="assignee case-details__assignee">
            <span className="assignee__avatar" aria-hidden="true">
              {caseItem.assignedUser.name.charAt(0)}
            </span>
            <div>
              <p className="assignee__name">{caseItem.assignedUser.name}</p>
              <p className="assignee__email">{caseItem.assignedUser.email}</p>
            </div>
          </div>
        </section>
      </article>
    </main>
  )
}

interface DetailsMessageProps {
  message: string
}

function DetailsMessage({ message }: DetailsMessageProps) {
  return (
    <main className="app-shell">
      <div className="state-message state-message--error" role="alert">
        <p>{message}</p>
        <Link className="state-message__link" to="/cases">
          Back to cases
        </Link>
      </div>
    </main>
  )
}
