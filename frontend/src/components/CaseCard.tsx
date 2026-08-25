import type { CaseItem, CaseStatus } from '../api/cases'
import { Link } from 'react-router-dom'

interface CaseCardProps {
  caseItem: CaseItem
}

const statusLabels: Record<CaseStatus, string> = {
  OPEN: 'Open',
  IN_PROGRESS: 'In progress',
  RESOLVED: 'Resolved',
  CLOSED: 'Closed',
}

export function CaseCard({ caseItem }: CaseCardProps) {
  return (
    <article className="case-card">
      <div className="case-card__heading">
        <span className={`status status--${caseItem.status.toLowerCase()}`}>
          {statusLabels[caseItem.status]}
        </span>
        <span className="case-id">Case #{caseItem.id}</span>
      </div>

      <h2>{caseItem.title}</h2>

      <Link className="case-card__link" to={`/cases/${caseItem.id}`}>
        View details
      </Link>

      <div className="assignee">
        <span className="assignee__avatar" aria-hidden="true">
          {caseItem.assignedUser.name.charAt(0)}
        </span>
        <div>
          <p className="assignee__name">{caseItem.assignedUser.name}</p>
          <p className="assignee__email">{caseItem.assignedUser.email}</p>
        </div>
      </div>
    </article>
  )
}
