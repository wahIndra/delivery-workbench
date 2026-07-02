import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function ReleaseReadinessPage() {
  const { id } = useParams();
  const [readiness, setReadiness] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const role = localStorage.getItem('mockRole');
  const isReleaseManager = role === 'RELEASE_MANAGER' || role === 'ADMIN';

  useEffect(() => {
    fetchReadiness();
  }, [id]);

  const fetchReadiness = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${id}/release-readiness`);
      setReadiness(res.data);
    } catch (err) {
      console.error('Error fetching release readiness', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      const res = await api.put(`/requests/${id}/release-readiness`, readiness);
      setReadiness(res.data);
      alert('Release Readiness saved successfully!');
    } catch (err) {
      console.error('Error saving readiness', err);
      alert(err.response?.data?.message || 'Failed to save Release Readiness');
    } finally {
      setSaving(false);
    }
  };

  const handleCheckboxChange = (e) => {
    setReadiness({ ...readiness, [e.target.name]: e.target.checked });
  };

  const handleMasterToggle = (e) => {
    setReadiness({ ...readiness, readyForRelease: e.target.checked });
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;

  const checklistItems = [
    { name: 'requirementSignedOff', label: 'Requirements Signed Off' },
    { name: 'solutionDesignApproved', label: 'Solution Design Approved' },
    { name: 'codeReviewed', label: 'Code Reviewed' },
    { name: 'sitPassed', label: 'SIT Passed' },
    { name: 'uatSignedOff', label: 'UAT Signed Off (BR-10)' },
    { name: 'securityReviewed', label: 'Security Reviewed' },
    { name: 'dbScriptReviewed', label: 'DB Script Reviewed' },
    { name: 'rollbackPlanAvailable', label: 'Rollback Plan Available' },
    { name: 'monitoringPrepared', label: 'Monitoring Prepared' },
    { name: 'releaseNotePrepared', label: 'Release Note Prepared' },
    { name: 'supportPicAssigned', label: 'Support PIC Assigned' },
  ];

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
          <h1 className="mt-2">Release Readiness</h1>
        </div>
        <div className="flex gap-2">
          {isReleaseManager && (
            <button onClick={handleSave} disabled={saving} className="btn btn-primary">
              {saving ? 'Saving...' : 'Save Checklist'}
            </button>
          )}
        </div>
      </div>

      <div className="card">
        <div className="flex justify-between items-center mb-6 pb-4" style={{ borderBottom: '1px solid var(--border-color)' }}>
          <div>
            <div className="text-muted" style={{ fontSize: '0.75rem' }}>Status</div>
            <div className={`badge mt-1 ${readiness.readyForRelease ? 'badge-green' : 'badge-gray'}`}>
              {readiness.readyForRelease ? 'READY FOR RELEASE' : 'PENDING'}
            </div>
          </div>
          {readiness.readyForRelease && readiness.reviewedBy && (
            <div className="text-right">
              <div className="text-muted" style={{ fontSize: '0.75rem' }}>Approved By (Release Manager)</div>
              <div style={{ fontWeight: 600, color: 'var(--success-600)' }}>{readiness.reviewedBy}</div>
              <div className="text-muted" style={{ fontSize: '0.75rem' }}>{dayjs(readiness.reviewedAt).format('MMM D, YYYY HH:mm')}</div>
            </div>
          )}
        </div>

        <form>
          <p className="mb-4 text-muted" style={{ fontSize: '0.875rem' }}>
            All 11 items must be checked before the master <strong>Ready for Release</strong> toggle can be enabled. (BR-02 & BR-10)
          </p>
          <div className="grid grid-cols-2 gap-y-4 gap-x-8 mb-8 pb-8" style={{ borderBottom: '1px dashed var(--border-color)' }}>
            {checklistItems.map(item => (
              <label key={item.name} className="checkbox-group p-3" style={{ backgroundColor: readiness[item.name] ? 'var(--success-100)' : 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)', transition: 'all 0.2s' }}>
                <input 
                  type="checkbox" 
                  name={item.name} 
                  checked={readiness[item.name] || false} 
                  onChange={handleCheckboxChange} 
                  disabled={!isReleaseManager}
                />
                <span style={{ fontSize: '0.875rem', fontWeight: readiness[item.name] ? 600 : 400, color: readiness[item.name] ? 'var(--success-600)' : 'var(--text-primary)' }}>
                  {item.label}
                </span>
              </label>
            ))}
          </div>

          <div className="p-6 text-center" style={{ backgroundColor: readiness.readyForRelease ? 'var(--success-100)' : 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)', border: `2px solid ${readiness.readyForRelease ? 'var(--success-600)' : 'var(--border-color)'}` }}>
            <h3 style={{ color: readiness.readyForRelease ? 'var(--success-600)' : 'var(--text-primary)' }}>Master Release Gate</h3>
            <label className="checkbox-group justify-center mt-4" style={{ transform: 'scale(1.2)' }}>
              <input 
                type="checkbox" 
                checked={readiness.readyForRelease || false} 
                onChange={handleMasterToggle} 
                disabled={!isReleaseManager}
              />
              <span style={{ fontWeight: 600 }}>Approve for Release</span>
            </label>
            <p className="mt-2 text-muted" style={{ fontSize: '0.75rem' }}>Only a Release Manager can approve this gate.</p>
          </div>
        </form>
      </div>
    </div>
  );
}
