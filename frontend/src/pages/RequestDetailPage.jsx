import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import api, { slaApi, bottleneckApi } from '../api';
import SlaBadge from '../components/SlaBadge';

export default function RequestDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [request, setRequest] = useState(null);
  const [history, setHistory] = useState([]);
  const [priorityScore, setPriorityScore] = useState(null);
  const [aging, setAging] = useState(null);
  const [bottlenecks, setBottlenecks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [reqRes, histRes, scoreRes, agingRes, bottleneckRes] = await Promise.all([
        api.get(`/requests/${id}`),
        api.get(`/requests/${id}/stage-history`),
        api.get(`/requests/${id}/priority-score`).catch(() => ({ data: null })),
        slaApi.getAgingForRequest(id).catch(() => ({ data: null })),
        bottleneckApi.getFindingsForRequest(id).catch(() => ({ data: [] }))
      ]);
      setRequest(reqRes.data);
      setHistory(histRes.data);
      setPriorityScore(scoreRes.data);
      if (agingRes.data) setAging(agingRes.data);
      if (bottleneckRes.data) setBottlenecks(bottleneckRes.data);
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

  const handleAnalyzeBottlenecks = async () => {
    try {
      const res = await bottleneckApi.analyzeRequest(id);
      setBottlenecks(res.data);
      alert('Bottleneck analysis completed.');
    } catch (err) {
      alert('Failed to run analysis');
    }
  };

  const handleUpdateBottleneckStatus = async (findingId, status) => {
    try {
      await bottleneckApi.updateStatus(id, findingId, status);
      const res = await bottleneckApi.getFindingsForRequest(id);
      setBottlenecks(res.data);
    } catch (err) {
      alert('Failed to update status');
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div><p className="mt-4">Loading request details...</p></div>;
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
                <div className="form-label">Aging (Current Stage)</div>
                <div>
                  <span className="font-semibold">{aging ? `${aging.agingHours}h` : 'N/A'}</span>
                  {aging && aging.slaHours && <span className="text-gray-500 text-sm ml-1">/ {aging.slaHours}h</span>}
                  <div className="mt-1">
                    <SlaBadge status={aging?.slaStatus} />
                  </div>
                </div>
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

        <div className="card mt-8 border-red-500">
          <div className="flex justify-between items-center">
            <h3>Bottleneck Findings</h3>
            <button className="btn btn-secondary text-sm" onClick={handleAnalyzeBottlenecks}>
              Run Analysis
            </button>
          </div>
          <div className="mt-4 flex flex-col gap-4">
            {bottlenecks.length === 0 ? (
              <p className="text-gray-500">No bottlenecks detected.</p>
            ) : (
              bottlenecks.map(b => (
                <div key={b.id} className="p-4 bg-gray-50 rounded-md border flex flex-col gap-2">
                  <div className="flex justify-between items-start">
                    <div className="flex items-center gap-2">
                      <span className={`badge ${b.severity === 'CRITICAL' ? 'bg-red-200 text-red-900' : b.severity === 'HIGH' ? 'bg-orange-200 text-orange-900' : 'bg-yellow-100 text-yellow-800'}`}>
                        {b.severity}
                      </span>
                      <span className="font-semibold">{b.findingType}</span>
                    </div>
                    <div>
                      {b.status === 'OPEN' ? (
                        <div className="flex gap-2">
                          <button onClick={() => handleUpdateBottleneckStatus(b.id, 'ACKNOWLEDGED')} className="btn btn-secondary text-xs">Ack</button>
                          <button onClick={() => handleUpdateBottleneckStatus(b.id, 'RESOLVED')} className="btn btn-primary text-xs bg-green-600 border-green-600 text-white">Resolve</button>
                          <button onClick={() => handleUpdateBottleneckStatus(b.id, 'IGNORED')} className="text-gray-400 hover:text-gray-600 text-xs underline">Ignore</button>
                        </div>
                      ) : (
                        <span className="text-sm font-semibold text-gray-500">{b.status}</span>
                      )}
                    </div>
                  </div>
                  <p className="text-sm">{b.description}</p>
                  {b.recommendedAction && (
                    <p className="text-sm text-blue-800 bg-blue-50 p-2 rounded mt-1">
                      <strong>AI Suggestion:</strong> {b.recommendedAction}
                    </p>
                  )}
                </div>
              ))
            )}
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
