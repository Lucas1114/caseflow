import { useQuery } from '@tanstack/react-query'
import { getCases } from '../api/cases'
import { CaseCard } from '../components/CaseCard'
import { CaseSummaryPanel } from '../components/CaseSummaryPanel'

export function CasesPage() {
  const casesQuery = useQuery({
    queryKey: ['cases'],
    queryFn: getCases,
  })

  return (
    <main className="app-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">CaseFlow workspace</p>
          <h1>Cases</h1>
          <p className="page-description">
            Review active work and the people responsible for each case.
          </p>
        </div>
      </header>

      <CaseSummaryPanel />

      {casesQuery.isPending && (
        <p className="state-message" role="status">
          Loading cases…
        </p>
      )}

      {casesQuery.isError && (
        <div className="state-message state-message--error" role="alert">
          <p>{casesQuery.error.message}</p>
          <button type="button" onClick={() => casesQuery.refetch()}>
            Try again
          </button>
        </div>
      )}

      {casesQuery.isSuccess && (
        <section className="case-grid" aria-label="Case list">
          {casesQuery.data.map((caseItem) => (
            <CaseCard key={caseItem.id} caseItem={caseItem} />
          ))}
        </section>
      )}
    </main>
  )
}
