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

export async function getCases(): Promise<CaseItem[]> {
  const response = await fetch('/api/cases')

  if (!response.ok) {
    throw new Error(`Unable to load cases (${response.status})`)
  }

  return response.json() as Promise<CaseItem[]>
}
