import { useState, type FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useNavigate } from 'react-router-dom'
import {
  createCase,
  getUsers,
  type CreateCaseInput,
} from '../api/cases'

export function NewCasePage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [title, setTitle] = useState('')
  const [assignedUserId, setAssignedUserId] = useState('')

  const usersQuery = useQuery({
    queryKey: ['users'],
    queryFn: getUsers,
    retry: false,
  })

  const createMutation = useMutation({
    mutationFn: (input: CreateCaseInput) => createCase(input),
    onSuccess: (createdCase) => {
      queryClient.setQueryData(['cases', createdCase.id], createdCase)
      void queryClient.invalidateQueries({ queryKey: ['cases'], exact: true })
      void queryClient.invalidateQueries({ queryKey: ['cases', 'summary'] })
      navigate(`/cases/${createdCase.id}`)
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    createMutation.mutate({
      title: title.trim(),
      assignedUserId: Number(assignedUserId),
    })
  }

  const trimmedTitle = title.trim()
  const canSubmit =
    usersQuery.isSuccess &&
    usersQuery.data.length > 0 &&
    trimmedTitle.length > 0 &&
    trimmedTitle.length <= 200 &&
    assignedUserId !== '' &&
    !createMutation.isPending

  return (
    <main className="app-shell app-shell--form">
      <Link className="back-link" to="/cases">
        ← Back to cases
      </Link>

      <article className="case-form-card">
        <header>
          <p className="eyebrow">Case intake</p>
          <h1>New case</h1>
          <p>Create a case and assign an owner. New cases begin with an open status.</p>
        </header>

        <form className="case-form" onSubmit={handleSubmit}>
          <div className="case-form__field">
            <label htmlFor="case-title">Title</label>
            <input
              id="case-title"
              type="text"
              value={title}
              maxLength={200}
              autoFocus
              disabled={createMutation.isPending}
              placeholder="Describe the work to be completed"
              onChange={(event) => setTitle(event.target.value)}
            />
            <span className="case-form__hint">{title.length}/200</span>
          </div>

          <div className="case-form__field">
            <label htmlFor="case-assignee">Assigned user</label>
            {usersQuery.isPending && (
              <p className="case-form__state" role="status">Loading assignees…</p>
            )}
            {usersQuery.isError && (
              <div className="case-form__state case-form__state--error" role="alert">
                <p>{usersQuery.error.message}</p>
                <button type="button" onClick={() => usersQuery.refetch()}>Try again</button>
              </div>
            )}
            {usersQuery.isSuccess && usersQuery.data.length === 0 && (
              <p className="case-form__state">No users are available to assign.</p>
            )}
            {usersQuery.isSuccess && usersQuery.data.length > 0 && (
              <select
                id="case-assignee"
                value={assignedUserId}
                disabled={createMutation.isPending}
                onChange={(event) => setAssignedUserId(event.target.value)}
              >
                <option value="">Select an assignee</option>
                {usersQuery.data.map((user) => (
                  <option key={user.id} value={user.id}>{user.name} — {user.email}</option>
                ))}
              </select>
            )}
          </div>

          {createMutation.isError && (
            <p className="case-form__error" role="alert">{createMutation.error.message}</p>
          )}

          <div className="case-form__actions">
            <Link to="/cases">Cancel</Link>
            <button type="submit" disabled={!canSubmit}>
              {createMutation.isPending ? 'Creating…' : 'Create case'}
            </button>
          </div>
        </form>
      </article>
    </main>
  )
}
