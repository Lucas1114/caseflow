import { useState, type FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { getUsers, updateCaseAssignee, type CaseItem } from '../api/cases'

interface CaseAssigneeFormProps {
  caseItem: CaseItem
}

export function CaseAssigneeForm({ caseItem }: CaseAssigneeFormProps) {
  const queryClient = useQueryClient()
  const [draftUserId, setDraftUserId] = useState<number | null>(null)
  const selectedUserId = draftUserId ?? caseItem.assignedUser.id
  const usersQuery = useQuery({
    queryKey: ['users'],
    queryFn: getUsers,
    retry: false,
  })
  const assigneeMutation = useMutation({
    mutationFn: (userId: number) => updateCaseAssignee(caseItem.id, userId),
    onSuccess: (updatedCase) => {
      queryClient.setQueryData(['cases', caseItem.id], updatedCase)
      void queryClient.invalidateQueries({ queryKey: ['cases'], exact: true })
      setDraftUserId(null)
    },
  })

  const canSave = usersQuery.isSuccess
    && usersQuery.data.some((user) => user.id === selectedUserId)
    && selectedUserId !== caseItem.assignedUser.id
    && !assigneeMutation.isPending

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (canSave) assigneeMutation.mutate(selectedUserId)
  }

  return (
    <div className="assignee-form">
      {usersQuery.isPending && <p role="status">Loading users…</p>}
      {usersQuery.isError && (
        <div className="activity-state activity-state--error" role="alert">
          <p>{usersQuery.error.message}</p>
          <button type="button" onClick={() => usersQuery.refetch()}>Retry users</button>
        </div>
      )}
      {usersQuery.isSuccess && usersQuery.data.length === 0 && (
        <p role="status">No users are available for assignment.</p>
      )}
      {usersQuery.isSuccess && usersQuery.data.length > 0 && (
        <form className="status-form" onSubmit={handleSubmit}>
          <label htmlFor="case-assignee">Assign to</label>
          <div className="status-form__controls">
            <select
              id="case-assignee"
              value={selectedUserId}
              disabled={assigneeMutation.isPending}
              onChange={(event) => {
                setDraftUserId(Number(event.target.value))
                assigneeMutation.reset()
              }}
            >
              {usersQuery.data.map((user) => (
                <option key={user.id} value={user.id}>{user.name} ({user.email})</option>
              ))}
            </select>
            <button type="submit" disabled={!canSave}>
              {assigneeMutation.isPending ? 'Saving assignee…' : 'Save assignee'}
            </button>
          </div>
        </form>
      )}
      {assigneeMutation.isError && (
        <p className="status-form__error" role="alert">{assigneeMutation.error.message}</p>
      )}
      {assigneeMutation.isSuccess && (
        <p className="assignee-form__success" role="status">Assignee updated.</p>
      )}
    </div>
  )
}
