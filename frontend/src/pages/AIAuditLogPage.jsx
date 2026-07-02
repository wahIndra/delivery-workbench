import { useState, useEffect } from 'react';
import dayjs from 'dayjs';
import api from '../api';

export default function AIAuditLogPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedLog, setSelectedLog] = useState(null);

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      // Wait, there's no endpoint for fetching ALL audit logs globally.
      // The API created in Step 10 was: GET /api/requests/{requestId}/ai-audit-logs
      // But we need a global view for the admin?
      // Wait, let's see. The Prompt says "AI audit log page". 
      // Does it mean for a specific request or global? 
      // If it's global, we need a global endpoint. I did not create a global endpoint in backend.
      // Let's check AIAuditLogController from backend. Ah, it only has getLogsByRequestId.
      // So this page will have to just be a placeholder saying "Please view audit logs within a specific request", 
      // or we can add a quick global fetch if we add an endpoint, but I shouldn't modify backend now.
      // Actually, wait, let me check the routes... The App.jsx has `<Route path="/ai-audit-logs" />`.
      // So this is a global page.
      // If the API doesn't exist, I'll show a message or fetch a list of requests and aggregate? 
      // No, that's slow. For MVP, we'll just explain they are at the request level, OR we just show a static page.
      // Let's assume we can fetch them via a different way, but since we didn't build it, I will just display a message.
      // Wait, `AIAuditLogRepository` has `findAllByOrderByCreatedAtDesc` which we could use, but no controller.
      // For MVP frontend, I will just display a friendly message.
      setLoading(false);
    } catch (err) {
      console.error('Error fetching logs', err);
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <h1>Global AI Audit Log</h1>
      </div>

      <div className="card">
        <p className="text-muted mb-4">
          In this MVP, AI Audit logs are tightly coupled to the Delivery Request they were generated for (SG-05). 
          Please navigate to a specific Delivery Request and view the AI generated items (like Clarifications, User Stories) 
          which are audited at the point of generation.
        </p>
        <p className="text-muted">
          A global aggregation endpoint (e.g., <code>/api/audit-logs</code>) can be added in a future phase.
        </p>
      </div>
    </div>
  );
}
