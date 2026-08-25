import { Navigate, Route, Routes } from 'react-router-dom'
import { CasesPage } from './pages/CasesPage'
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/cases" element={<CasesPage />} />
      <Route path="*" element={<Navigate to="/cases" replace />} />
    </Routes>
  )
}

export default App
