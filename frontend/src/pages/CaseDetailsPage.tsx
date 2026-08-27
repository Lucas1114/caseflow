import { useState, type FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { CaseActivitySection } from '../components/CaseActivitySection'
import {
  ApiError,
  getCase,
  updateCaseStatus,
  type CaseItem,
  type CaseStatus,
} from '../api/cases'

const statusLabels: Record<CaseStatus, string> = {
  OPEN: 'Open',
  IN_PROGRESS: 'In progress',
  RESOLVED: 'Resolved',
  CLOSED: 'Closed',
}

const caseStatuses = Object.keys(statusLabels) as CaseStatus[]

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

  return (
    <main className="app-shell">
      <Link className="back-link" to="/cases">
        ← Back to cases
      </Link>

      <CaseDetails caseItem={caseQuery.data} />
    </main>
  )
}

interface CaseDetailsProps {
  caseItem: CaseItem
}

function CaseDetails({ caseItem }: CaseDetailsProps) {
  const queryClient = useQueryClient()
  const [selectedStatus, setSelectedStatus] = useState<CaseStatus>(caseItem.status)

  const statusMutation = useMutation({
    mutationFn: (status: CaseStatus) => updateCaseStatus(caseItem.id, status),
    onSuccess: (updatedCase) => {
      queryClient.setQueryData(['cases', caseItem.id], updatedCase)
      void queryClient.invalidateQueries({ queryKey: ['cases'], exact: true })
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    statusMutation.mutate(selectedStatus)
  }

  const hasStatusChange = selectedStatus !== caseItem.status

  return (
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

      <section className="case-details__section" aria-labelledby="status-heading">
        <h2 id="status-heading">Workflow status</h2>
        <form className="status-form" onSubmit={handleSubmit}>
          <label htmlFor="case-status">Status</label>
          <div className="status-form__controls">
            <select
              id="case-status"
              value={selectedStatus}
              onChange={(event) => setSelectedStatus(event.target.value as CaseStatus)}
              disabled={statusMutation.isPending}
            >
              {caseStatuses.map((status) => (
                <option key={status} value={status}>
                  {statusLabels[status]}
                </option>
              ))}
            </select>
            <button type="submit" disabled={!hasStatusChange || statusMutation.isPending}>
              {statusMutation.isPending ? 'Saving…' : 'Save status'}
            </button>
          </div>
          {statusMutation.isError && (
            <p className="status-form__error" role="alert">
              {statusMutation.error.message}
            </p>
          )}
        </form>
      </section>

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

      <CaseActivitySection caseId={caseItem.id} />
    </article>
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
