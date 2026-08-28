export type CaseStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'

export interface AssignedUser {
  id: number
  name: string
  email: string
}

export interface CaseItem {
  id: number
  title: string
  status: CaseStatus
  assignedUser: AssignedUser
}

export interface CaseActivity {
  id: number
  note: string
  createdAt: string
}

export class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

export async function getCases(): Promise<CaseItem[]> {
  const response = await fetch('/api/cases')

  if (!response.ok) {
    throw new ApiError(`Unable to load cases (${response.status})`, response.status)
  }

  return response.json() as Promise<CaseItem[]>
}

export async function getCase(caseId: number): Promise<CaseItem> {
  const response = await fetch(`/api/cases/${caseId}`)

  if (!response.ok) {
    throw new ApiError(`Unable to load case (${response.status})`, response.status)
  }

  return response.json() as Promise<CaseItem>
}

export async function updateCaseStatus(
  caseId: number,
  status: CaseStatus,
): Promise<CaseItem> {
  const response = await fetch(`/api/cases/${caseId}/status`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status }),
  })

  if (!response.ok) {
    throw new ApiError(`Unable to update case (${response.status})`, response.status)
  }

  return response.json() as Promise<CaseItem>
}

export async function getCaseActivities(caseId: number): Promise<CaseActivity[]> {
  const response = await fetch(`/api/cases/${caseId}/activities`)

  if (!response.ok) {
    throw new ApiError(
      `Unable to load case activity (${response.status})`,
      response.status,
    )
  }

  return response.json() as Promise<CaseActivity[]>
}

export async function getUsers(): Promise<AssignedUser[]> {
  const response = await fetch('/api/users')

  if (!response.ok) {
    throw new ApiError(`Unable to load users (${response.status})`, response.status)
  }

  return response.json() as Promise<AssignedUser[]>
}

export async function updateCaseAssignee(
  caseId: number,
  assignedUserId: number,
): Promise<CaseItem> {
  const response = await fetch(`/api/cases/${caseId}/assignee`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ assignedUserId }),
  })

  if (!response.ok) {
    const message = response.status === 404
      ? 'The case or selected user no longer exists. Reload the page and try again.'
      : `Unable to save assignee (${response.status}). Please try again.`
    throw new ApiError(message, response.status)
  }

  return response.json() as Promise<CaseItem>
}

export async function createCaseActivity(
  caseId: number,
  note: string,
): Promise<CaseActivity> {
  const response = await fetch(`/api/cases/${caseId}/activities`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ note }),
  })

  if (!response.ok) {
    throw new ApiError(
      `Unable to save case activity (${response.status})`,
      response.status,
    )
  }

  return response.json() as Promise<CaseActivity>
}
