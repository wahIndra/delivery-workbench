import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function RequestDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [request, setRequest] = useState(null);
  const [history, setHistory] = useState([]);
  const [priorityScore, setPriorityScore] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [reqRes, histRes, scoreRes] = await Promise.all([
        api.get(`/requests/${id}`),
        api.get(`/requests/${id}/stage-history`),
        api.get(`/requests/${id}/priority-score`).catch(() => ({ data: null }))
      ]);
      setRequest(reqRes.data);
      setHistory(histRes.data);
      setPriorityScore(scoreRes.data);
    } catch (err) {
      console.error('Failed to fetch request data', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (newStatus) => {
    try {
      const notes = prompt(`Enter notes for transitioning to ${newStatus}:`, `Transitioned to ${newStatus}`);
      if (notes === null) return; // cancelled
      
      const res = await api.put(`/requests/${id}/status`, { newStatus, notes });
      setRequest(res.data);
      
      // refresh history
      const histRes = await api.get(`/requests/${id}/stage-history`);
      setHistory(histRes.data);
    } catch (err) {
      console.error('Failed to change status', err);
      alert(err.response?.data?.message || 'Failed to change status. Check business rules.');
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;
  if (!request) return <div>Request not found</div>;

  const role = localStorage.getItem('mockRole');

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <div style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>{request.requestCode}</div>
          <h1 style={{ margin: 0 }}>{request.title}</h1>
        </div>
        <div className="flex gap-2">
          {request.status === 'DRAFT' && role === 'BUSINESS_USER' && (
            <button onClick={() => handleStatusChange('SUBMITTED')} className="btn btn-primary">Submit Request</button>
          )}
          {request.status === 'SUBMITTED' && role === 'SYSTEM_ANALYST' && (
            <button onClick={() => handleStatusChange('REQUIREMENT_REFINEMENT')} className="btn btn-primary">Start Refinement</button>
          )}
          <button onClick={() => navigate('/requests')} className="btn btn-secondary">Back to List</button>
        </div>
      </div>

      <div className="grid" style={{ gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
        {/* Left Column: Details & Modules */}
        <div>
          <div className="card">
            <h3>Request Summary</h3>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <div className="form-label">Status</div>
                <div className="badge badge-blue">{request.status}</div>
              </div>
              <div>
                <div className="form-label">Priority</div>
                <div>{priorityScore?.priorityRecommendation || 'PENDING'}</div>
              </div>
              <div>
                <div className="form-label">Business Owner</div>
                <div>{request.businessOwner || 'Unassigned'}</div>
              </div>
              <div>
                <div className="form-label">IT Owner</div>
                <div>{request.itOwner || 'Unassigned'}</div>
              </div>
            </div>
            
            <div className="mt-4">
              <div className="form-label">Business Problem</div>
              <p>{request.businessProblem}</p>
            </div>
            <div className="mt-4">
              <div className="form-label">Expected Outcome</div>
              <p>{request.expectedOutcome}</p>
            </div>
          </div>

          <h3 className="mb-4 mt-8">Delivery Modules</h3>
          <div className="grid grid-cols-2 gap-4">
            <ModuleCard title="Priority Scoring" path={`/requests/${id}/priority-score`} desc="Evaluate business value and technical risk." />
            <ModuleCard title="Clarification Questions" path={`/requests/${id}/clarifications`} desc="Ask and answer questions to clarify the request." />
            <ModuleCard title="Requirement Refinement" path={`/requests/${id}/requirements`} desc="Define scope, user story, and acceptance criteria." />
            <ModuleCard title="Definition of Ready" path={`/requests/${id}/dor`} desc="Complete the 12-point readiness checklist." />
            <ModuleCard title="Impact Analysis" path={`/requests/${id}/impact`} desc="Assess risks and impacts on other systems." />
            <ModuleCard title="QA Test Scenarios" path={`/requests/${id}/qa-scenarios`} desc="Manage test scenarios for validation." />
            <ModuleCard title="Release Readiness" path={`/requests/${id}/release-readiness`} desc="Final checklist before production release." />
          </div>
        </div>

        {/* Right Column: Timeline & Actions */}
        <div>
          <div className="card">
            <h3>Workflow Actions</h3>
            <div className="flex flex-col gap-2 mt-4">
              <button onClick={() => handleStatusChange('NEED_CLARIFICATION')} className="btn btn-secondary">Set Need Clarification</button>
              <button onClick={() => handleStatusChange('READY_FOR_ANALYSIS')} className="btn btn-secondary">Set Ready for Analysis</button>
              <button onClick={() => handleStatusChange('IMPACT_ANALYSIS')} className="btn btn-secondary">Start Impact Analysis</button>
              <button onClick={() => handleStatusChange('READY_FOR_DEVELOPMENT')} className="btn btn-secondary">Set Ready for Dev</button>
              <button onClick={() => handleStatusChange('IN_DEVELOPMENT')} className="btn btn-secondary">Start Development</button>
              <button onClick={() => handleStatusChange('SIT')} className="btn btn-secondary">Move to SIT</button>
              <button onClick={() => handleStatusChange('UAT')} className="btn btn-secondary">Move to UAT</button>
              <button onClick={() => handleStatusChange('READY_FOR_RELEASE')} className="btn btn-secondary">Set Ready for Release</button>
              <button onClick={() => handleStatusChange('RELEASED')} className="btn btn-primary">Mark Released</button>
            </div>
          </div>

          <div className="card">
            <h3>Stage History</h3>
            <div className="timeline mt-4">
              {history.map((h, i) => (
                <div key={i} className="timeline-item">
                  <div className="timeline-content">
                    <div className="flex justify-between items-center mb-1">
                      <strong style={{ fontSize: '0.875rem' }}>{h.toStatus}</strong>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{dayjs(h.changedAt).format('MMM D, HH:mm')}</span>
                    </div>
                    <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                      By {h.changedBy}
                    </div>
                    {h.notes && (
                      <div style={{ fontSize: '0.875rem', fontStyle: 'italic', marginTop: '0.25rem' }}>
                        "{h.notes}"
                      </div>
                    )}
                  </div>
                </div>
              ))}
              {history.length === 0 && <p className="text-muted">No history yet.</p>}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function ModuleCard({ title, path, desc }) {
  return (
    <Link to={path} className="card" style={{ display: 'block', textDecoration: 'none', color: 'inherit' }}>
      <h4 style={{ color: 'var(--primary-600)' }}>{title}</h4>
      <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>{desc}</p>
    </Link>
  );
}
