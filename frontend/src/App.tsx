import { Navigate, Route, Routes } from 'react-router-dom'
import { CasesPage } from './pages/CasesPage'
import { CaseDetailsPage } from './pages/CaseDetailsPage'
import { NewCasePage } from './pages/NewCasePage'
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/cases" element={<CasesPage />} />
      <Route path="/cases/new" element={<NewCasePage />} />
      <Route path="/cases/:caseId" element={<CaseDetailsPage />} />
      <Route path="*" element={<Navigate to="/cases" replace />} />
    </Routes>
  )
}

export default App
