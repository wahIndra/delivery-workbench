import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function DefinitionOfReadyPage() {
  const { id } = useParams();
  const [dor, setDor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const role = localStorage.getItem('mockRole');

  useEffect(() => {
    fetchDor();
  }, [id]);

  const fetchDor = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${id}/dor`);
      setDor(res.data);
    } catch (err) {
      console.error('Error fetching DoR', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      const res = await api.put(`/requests/${id}/dor`, dor);
      setDor(res.data);
      alert('Definition of Ready saved successfully!');
    } catch (err) {
      console.error('Error saving DoR', err);
      alert('Failed to save DoR');
    } finally {
      setSaving(false);
    }
  };

  const handleCheckboxChange = (e) => {
    setDor({ ...dor, [e.target.name]: e.target.checked });
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;

  const getStatusBadge = (status) => {
    const colors = {
      'NOT_READY': 'badge-gray',
      'PARTIALLY_READY': 'badge-yellow',
      'READY': 'badge-green',
    };
    return colors[status] || 'badge-gray';
  };

  const checklistItems = [
    { name: 'businessProblemClear', label: 'Business Problem is Clear' },
    { name: 'expectedOutcomeDefined', label: 'Expected Outcome is Defined' },
    { name: 'scopeAgreed', label: 'Scope is Agreed' },
    { name: 'outOfScopeAgreed', label: 'Out of Scope is Agreed' },
    { name: 'impactedUsersIdentified', label: 'Impacted Users are Identified' },
    { name: 'impactedSystemsIdentified', label: 'Impacted Systems are Identified' },
    { name: 'processFlowDocumented', label: 'Process Flow is Documented' },
    { name: 'dataRequirementListed', label: 'Data Requirements are Listed' },
    { name: 'integrationRequirementListed', label: 'Integration Requirements are Listed' },
    { name: 'acceptanceCriteriaAgreed', label: 'Acceptance Criteria is Agreed' },
    { name: 'priorityClear', label: 'Priority is Clear' },
    { name: 'deadlineReasonClear', label: 'Deadline Reason is Clear' },
    { name: 'risksIdentified', label: 'Risks are Identified' },
    { name: 'businessOwnerAssigned', label: 'Business Owner is Assigned' },
    { name: 'itOwnerAssigned', label: 'IT Owner is Assigned' },
    { name: 'testerAssigned', label: 'Tester is Assigned' },
  ];

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
          <h1 className="mt-2">Definition of Ready</h1>
        </div>
        <div className="flex gap-2">
          {(role === 'SYSTEM_ANALYST' || role === 'SOLUTION_ARCHITECT') && (
            <button onClick={handleSave} disabled={saving} className="btn btn-primary">
              {saving ? 'Saving...' : 'Save Checklist'}
            </button>
          )}
        </div>
      </div>

      <div className="card">
        <div className="flex justify-between items-center mb-6 pb-4" style={{ borderBottom: '1px solid var(--border-color)' }}>
          <div>
            <div className="text-muted" style={{ fontSize: '0.75rem' }}>Ready Status</div>
            <div className={`badge mt-1 ${getStatusBadge(dor.readyStatus)}`}>{dor.readyStatus}</div>
          </div>
          {dor.readyStatus === 'READY' && dor.reviewedBy && (
            <div className="text-right">
              <div className="text-muted" style={{ fontSize: '0.75rem' }}>Approved By</div>
              <div style={{ fontWeight: 600 }}>{dor.reviewedBy}</div>
              <div className="text-muted" style={{ fontSize: '0.75rem' }}>{dayjs(dor.reviewedAt).format('MMM D, YYYY HH:mm')}</div>
            </div>
          )}
        </div>

        <form>
          <p className="mb-4 text-muted" style={{ fontSize: '0.875rem' }}>
            All items must be checked to move the request to <strong>READY_FOR_DEVELOPMENT</strong>.
          </p>
          <div className="grid grid-cols-2 gap-y-4 gap-x-8">
            {checklistItems.map(item => (
              <label key={item.name} className="checkbox-group p-3" style={{ backgroundColor: dor[item.name] ? 'var(--success-100)' : 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)', transition: 'all 0.2s' }}>
                <input 
                  type="checkbox" 
                  name={item.name} 
                  checked={dor[item.name] || false} 
                  onChange={handleCheckboxChange} 
                  disabled={role !== 'SYSTEM_ANALYST' && role !== 'SOLUTION_ARCHITECT'}
                />
                <span style={{ fontSize: '0.875rem', fontWeight: dor[item.name] ? 600 : 400, color: dor[item.name] ? 'var(--success-600)' : 'var(--text-primary)' }}>
                  {item.label}
                </span>
              </label>
            ))}
          </div>
        </form>
      </div>
    </div>
  );
}
