import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import RequestListPage from './pages/RequestListPage';
import CreateRequestPage from './pages/CreateRequestPage';
import RequestDetailPage from './pages/RequestDetailPage';
import ClarificationPage from './pages/ClarificationPage';
import RequirementRefinementPage from './pages/RequirementRefinementPage';
import DefinitionOfReadyPage from './pages/DefinitionOfReadyPage';
import ImpactAnalysisPage from './pages/ImpactAnalysisPage';
import QATestScenarioPage from './pages/QATestScenarioPage';
import ReleaseReadinessPage from './pages/ReleaseReadinessPage';
import PriorityScoringPage from './pages/PriorityScoringPage';
import AIAuditLogPage from './pages/AIAuditLogPage';
import DecisionLogPage from './pages/DecisionLogPage';
import MeetingNotesPage from './pages/MeetingNotesPage';
import AttachmentsPage from './pages/AttachmentsPage';
import AdminUserPage from './pages/AdminUserPage';
import ApprovalPage from './pages/ApprovalPage';
import NotificationsPage from './pages/NotificationsPage';
import ReleaseCalendarPage from './pages/ReleaseCalendarPage';
import RiskRegisterPage from './pages/RiskRegisterPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<LoginPage />} />
        
        {/* Routes wrapped in Layout */}
        <Route path="/dashboard" element={<Layout><DashboardPage /></Layout>} />
        <Route path="/requests" element={<Layout><RequestListPage /></Layout>} />
        <Route path="/requests/new" element={<Layout><CreateRequestPage /></Layout>} />
        <Route path="/requests/:id" element={<Layout><RequestDetailPage /></Layout>} />
        
        {/* Phase 2 Routes */}
        <Route path="/requests/:id/priority-score" element={<Layout><PriorityScoringPage /></Layout>} />
        <Route path="/requests/:id/clarifications" element={<Layout><ClarificationPage /></Layout>} />
        <Route path="/requests/:id/requirements" element={<Layout><RequirementRefinementPage /></Layout>} />
        <Route path="/requests/:id/dor" element={<Layout><DefinitionOfReadyPage /></Layout>} />
        
        {/* Phase 3 Routes */}
        <Route path="/requests/:id/impact" element={<Layout><ImpactAnalysisPage /></Layout>} />
        <Route path="/requests/:id/qa-scenarios" element={<Layout><QATestScenarioPage /></Layout>} />
        <Route path="/requests/:id/release-readiness" element={<Layout><ReleaseReadinessPage /></Layout>} />
        <Route path="/requests/:id/decision-logs" element={<Layout><DecisionLogPage /></Layout>} />
        <Route path="/requests/:id/meeting-notes" element={<Layout><MeetingNotesPage /></Layout>} />
        <Route path="/requests/:id/attachments" element={<Layout><AttachmentsPage /></Layout>} />
        <Route path="/requests/:id/approvals" element={<Layout><ApprovalPage /></Layout>} />
        
        {/* Phase 4 Routes */}
        <Route path="/ai-audit-logs" element={<Layout><AIAuditLogPage /></Layout>} />
        <Route path="/admin/users" element={<Layout><AdminUserPage /></Layout>} />
        
        {/* Phase 10 Routes */}
        <Route path="/notifications" element={<Layout><NotificationsPage /></Layout>} />
        
        {/* Phase 12 & 13 Routes */}
        <Route path="/release-calendar" element={<Layout><ReleaseCalendarPage /></Layout>} />
        <Route path="/requests/:id/risks" element={<Layout><RiskRegisterPage /></Layout>} />
        
        <Route path="*" element={<Layout><div className="card text-center p-8"><h2>404 — Page Not Found</h2></div></Layout>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
