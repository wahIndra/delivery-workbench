import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'

/**
 * App — root router skeleton.
 * Full page components are added in Step 12.
 * Placeholder pages ensure the app compiles and routes work.
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<PlaceholderPage title="Login" />} />
        <Route path="/dashboard" element={<PlaceholderPage title="Dashboard" />} />
        <Route path="/requests" element={<PlaceholderPage title="Request List" />} />
        <Route path="/requests/new" element={<PlaceholderPage title="Create Request" />} />
        <Route path="/requests/:id" element={<PlaceholderPage title="Request Detail" />} />
        <Route path="/requests/:id/clarifications" element={<PlaceholderPage title="Clarifications" />} />
        <Route path="/requests/:id/requirements" element={<PlaceholderPage title="Requirements" />} />
        <Route path="/requests/:id/dor" element={<PlaceholderPage title="Definition of Ready" />} />
        <Route path="/requests/:id/impact" element={<PlaceholderPage title="Impact Analysis" />} />
        <Route path="/requests/:id/qa-scenarios" element={<PlaceholderPage title="QA Scenarios" />} />
        <Route path="/requests/:id/release-readiness" element={<PlaceholderPage title="Release Readiness" />} />
        <Route path="/ai-audit-logs" element={<PlaceholderPage title="AI Audit Log" />} />
        <Route path="/admin/users" element={<PlaceholderPage title="User Admin" />} />
        <Route path="*" element={<PlaceholderPage title="404 — Page Not Found" />} />
      </Routes>
    </BrowserRouter>
  )
}

function PlaceholderPage({ title }) {
  return (
    <div style={{ padding: '40px', fontFamily: 'system-ui, sans-serif' }}>
      <h1 style={{ color: '#1a56db' }}>IT Delivery Workbench</h1>
      <h2>{title}</h2>
      <p style={{ color: '#6b7280' }}>
        This page will be implemented in Step 12 (React frontend).
        Backend APIs are available at{' '}
        <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a>.
      </p>
    </div>
  )
}

export default App
