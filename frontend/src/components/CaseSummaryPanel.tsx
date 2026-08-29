import { useQuery } from '@tanstack/react-query'
import { getCaseSummary } from '../api/cases'

export function CaseSummaryPanel() {
  const summaryQuery = useQuery({
    queryKey: ['cases', 'summary'],
    queryFn: getCaseSummary,
    retry: false,
  })

  if (summaryQuery.isPending) {
    return (
      <section className="case-summary case-summary--state" aria-label="Workflow summary">
        <p role="status">Loading workflow summary…</p>
      </section>
    )
  }

  if (summaryQuery.isError) {
    return (
      <section
        className="case-summary case-summary--state case-summary--error"
        aria-label="Workflow summary"
      >
        <p role="alert">{summaryQuery.error.message}</p>
        <button type="button" onClick={() => summaryQuery.refetch()}>
          Try summary again
        </button>
      </section>
    )
  }

  const summaryItems = [
    { label: 'Total cases', value: summaryQuery.data.total },
    { label: 'Open', value: summaryQuery.data.open },
    { label: 'In progress', value: summaryQuery.data.inProgress },
    { label: 'Resolved', value: summaryQuery.data.resolved },
    { label: 'Closed', value: summaryQuery.data.closed },
  ]

  return (
    <section className="case-summary" aria-labelledby="workflow-summary-heading">
      <h2 id="workflow-summary-heading">Workflow summary</h2>
      <dl>
        {summaryItems.map((item) => (
          <div key={item.label}>
            <dt>{item.label}</dt>
            <dd>{item.value}</dd>
          </div>
        ))}
      </dl>
    </section>
  )
}
