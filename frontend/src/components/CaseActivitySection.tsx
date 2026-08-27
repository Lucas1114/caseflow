import { useState, type FormEvent } from 'react'
import {
  useMutation,
  useQuery,
  useQueryClient,
  type UseQueryResult,
} from '@tanstack/react-query'
import {
  createCaseActivity,
  getCaseActivities,
  type CaseActivity,
} from '../api/cases'

interface CaseActivitySectionProps {
  caseId: number
}

export function CaseActivitySection({ caseId }: CaseActivitySectionProps) {
  const queryClient = useQueryClient()
  const [note, setNote] = useState('')
  const [showSuccess, setShowSuccess] = useState(false)

  const activitiesQuery = useQuery({
    queryKey: ['cases', caseId, 'activities'],
    queryFn: () => getCaseActivities(caseId),
    retry: false,
  })

  const activityMutation = useMutation({
    mutationFn: (newNote: string) => createCaseActivity(caseId, newNote),
    onSuccess: (createdActivity) => {
      queryClient.setQueryData<CaseActivity[]>(
        ['cases', caseId, 'activities'],
        (activities = []) => [createdActivity, ...activities],
      )
      setNote('')
      setShowSuccess(true)
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setShowSuccess(false)
    activityMutation.mutate(note.trim())
  }

  const trimmedNote = note.trim()
  const canSubmit = trimmedNote.length > 0 && !activityMutation.isPending

  return (
    <section className="case-details__section" aria-labelledby="activity-heading">
      <h2 id="activity-heading">Activity</h2>

      <form className="activity-form" onSubmit={handleSubmit}>
        <label htmlFor="activity-note">Add a note</label>
        <textarea
          id="activity-note"
          value={note}
          maxLength={1000}
          rows={4}
          placeholder="Record an update or follow-up…"
          disabled={activityMutation.isPending}
          onChange={(event) => {
            setNote(event.target.value)
            setShowSuccess(false)
          }}
        />
        <div className="activity-form__footer">
          <span>{note.length}/1000</span>
          <button type="submit" disabled={!canSubmit}>
            {activityMutation.isPending ? 'Saving…' : 'Add note'}
          </button>
        </div>
        {activityMutation.isError && (
          <p className="activity-form__message activity-form__message--error" role="alert">
            {activityMutation.error.message}
          </p>
        )}
        {showSuccess && (
          <p className="activity-form__message activity-form__message--success" role="status">
            Activity note added.
          </p>
        )}
      </form>

      <ActivityTimeline query={activitiesQuery} />
    </section>
  )
}

interface ActivityTimelineProps {
  query: UseQueryResult<CaseActivity[], Error>
}

function ActivityTimeline({ query }: ActivityTimelineProps) {
  if (query.isPending) {
    return (
      <p className="activity-state" role="status">
        Loading activity…
      </p>
    )
  }

  if (query.isError) {
    return (
      <div className="activity-state activity-state--error" role="alert">
        <p>{query.error.message}</p>
        <button type="button" onClick={() => query.refetch()}>
          Try again
        </button>
      </div>
    )
  }

  if (query.data.length === 0) {
    return <p className="activity-state">No activity has been recorded yet.</p>
  }

  return (
    <ol className="activity-list" aria-label="Case activity history">
      {query.data.map((activity) => (
        <li key={activity.id}>
          <p>{activity.note}</p>
          <time dateTime={activity.createdAt}>{formatActivityTime(activity.createdAt)}</time>
        </li>
      ))}
    </ol>
  )
}

const activityTimeFormatter = new Intl.DateTimeFormat(undefined, {
  dateStyle: 'medium',
  timeStyle: 'short',
})

function formatActivityTime(createdAt: string) {
  return activityTimeFormatter.format(new Date(createdAt))
}
