import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { requirementApi } from '../api';

export default function RequirementRefinementPage() {
  const { id } = useParams();
  const [requirement, setRequirement] = useState(null);
  const [versions, setVersions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [changeReason, setChangeReason] = useState('');
  const [showHistory, setShowHistory] = useState(false);

  const role = localStorage.getItem('mockRole');

  useEffect(() => {
    fetchRequirement();
    fetchVersions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchRequirement = async () => {
    try {
      setLoading(true);
      const res = await requirementApi.get(id);
      setRequirement(res.data);
    } catch (err) {
      console.error('Error fetching requirement', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchVersions = async () => {
    try {
      const res = await requirementApi.getVersions(id);
      setVersions(res.data);
    } catch (err) {
      console.error('Error fetching version history', err);
    }
  };

  const handleGenerateAI = async () => {
    try {
      setGenerating(true);
      const res = await requirementApi.generateAi(id);
      setRequirement(res.data);
    } catch (err) {
      console.error('Error generating AI requirement', err);
      alert('Failed to generate AI requirement');
    } finally {
      setGenerating(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      const payload = {
        scope: requirement.scope,
        outOfScope: requirement.outOfScope,
        userStory: requirement.userStory,
        acceptanceCriteria: requirement.acceptanceCriteria,
        assumptions: requirement.assumptions,
        dependencies: requirement.dependencies,
        status: requirement.status,
        changeReason: changeReason
      };
      const res = await requirementApi.save(id, payload);
      setRequirement(res.data);
      setChangeReason('');
      fetchVersions();
      alert('Requirement saved successfully!');
    } catch (err) {
      console.error('Error saving requirement', err);
      alert('Failed to save requirement');
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e) => {
    setRequirement({ ...requirement, [e.target.name]: e.target.value });
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;

  return (
    <div className="flex gap-6">
      <div className={showHistory ? 'w-2/3' : 'w-full'}>
        <div className="flex items-center justify-between mb-8">
          <div>
            <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
            <h1 className="mt-2">Requirement Refinement</h1>
          </div>
          <div className="flex gap-2">
            <button onClick={() => setShowHistory(!showHistory)} className="btn btn-secondary">
              {showHistory ? 'Hide History' : 'View History'}
            </button>
            {role === 'SYSTEM_ANALYST' && (
              <>
                <button onClick={handleGenerateAI} disabled={generating} className="btn btn-secondary" style={{ borderColor: 'var(--secondary-500)', color: 'var(--secondary-600)' }}>
                  {generating ? '✨ Generating...' : '✨ AI Generate'}
                </button>
                <button onClick={handleSave} disabled={saving} className="btn btn-primary">
                  {saving ? 'Saving...' : 'Save Requirements'}
                </button>
              </>
            )}
          </div>
        </div>

        <div className="card">
          <div className="flex justify-between items-center mb-6 pb-4" style={{ borderBottom: '1px solid var(--border-color)' }}>
            <div className="flex items-center gap-4">
              <div>
                <div className="text-muted" style={{ fontSize: '0.75rem' }}>Status</div>
                <select name="status" value={requirement.status} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} className="form-control mt-1 py-1 px-2 text-sm">
                  <option value="DRAFT">DRAFT</option>
                  <option value="REVIEW">REVIEW</option>
                  <option value="APPROVED">APPROVED</option>
                </select>
              </div>
              <div>
                <div className="text-muted" style={{ fontSize: '0.75rem' }}>Version</div>
                <div style={{ fontWeight: 600 }}>v{requirement.version}</div>
              </div>
            </div>
            <div className="text-muted" style={{ fontSize: '0.75rem' }}>
              Last updated: {dayjs(requirement.updatedAt).format('MMM D, YYYY HH:mm')}
            </div>
          </div>

          <form>
            <div className="grid grid-cols-2 gap-4">
              <div className="form-group">
                <label className="form-label">Scope</label>
                <textarea className="form-control" name="scope" rows="4" value={requirement.scope || ''} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} />
              </div>
              <div className="form-group">
                <label className="form-label">Out of Scope</label>
                <textarea className="form-control" name="outOfScope" rows="4" value={requirement.outOfScope || ''} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} />
              </div>
            </div>

            <div className="form-group mt-4">
              <label className="form-label">User Story (AI Assisted)</label>
              <textarea className="form-control" name="userStory" rows="6" value={requirement.userStory || ''} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} style={{ backgroundColor: 'rgba(139, 92, 246, 0.03)' }} />
            </div>

            <div className="form-group mt-4">
              <label className="form-label">Acceptance Criteria (AI Assisted)</label>
              <textarea className="form-control" name="acceptanceCriteria" rows="8" value={requirement.acceptanceCriteria || ''} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} style={{ backgroundColor: 'rgba(139, 92, 246, 0.03)' }} />
            </div>

            <div className="grid grid-cols-2 gap-4 mt-4">
              <div className="form-group">
                <label className="form-label">Assumptions</label>
                <textarea className="form-control" name="assumptions" rows="3" value={requirement.assumptions || ''} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} />
              </div>
              <div className="form-group">
                <label className="form-label">Dependencies</label>
                <textarea className="form-control" name="dependencies" rows="3" value={requirement.dependencies || ''} onChange={handleChange} disabled={role !== 'SYSTEM_ANALYST'} />
              </div>
            </div>
            
            {role === 'SYSTEM_ANALYST' && requirement.version >= 1 && requirement.status === 'APPROVED' && (
              <div className="form-group mt-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
                <label className="form-label text-red-600">Change Reason (Required for major updates)</label>
                <input type="text" className="form-control mt-2" placeholder="e.g. Added out of scope items for phase 2" value={changeReason} onChange={e => setChangeReason(e.target.value)} />
                <p className="text-xs text-gray-500 mt-2">Providing a reason will bump the requirement version to v{requirement.version + 1}.</p>
              </div>
            )}
          </form>
        </div>
      </div>
      
      {showHistory && (
        <div className="w-1/3">
          <h2 className="mb-4 text-xl">Version History</h2>
          <div className="flex flex-col gap-4">
            {versions.map(v => (
              <div key={v.id} className="card p-4 text-sm relative">
                <div className="flex justify-between items-center mb-2">
                  <span className="font-bold">Version {v.version}</span>
                  <span className="text-gray-500 text-xs">{dayjs(v.createdAt).format('MMM D, YYYY HH:mm')}</span>
                </div>
                {v.changeReason && (
                  <div className="mb-2">
                    <strong>Reason:</strong> {v.changeReason}
                  </div>
                )}
                <div className="text-gray-600 text-xs mb-1">By: {v.changedBy}</div>
                <details className="mt-2 text-gray-700 bg-gray-50 p-2 rounded cursor-pointer">
                  <summary className="font-medium text-xs outline-none">View Snapshot Details</summary>
                  <div className="mt-2 space-y-2 text-xs">
                    <div><strong>Scope:</strong> {v.scope}</div>
                    <div><strong>User Story:</strong> {v.userStory}</div>
                    <div><strong>Acceptance Criteria:</strong> {v.acceptanceCriteria}</div>
                  </div>
                </details>
              </div>
            ))}
            {versions.length === 0 && (
              <div className="text-gray-500">No version history available.</div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
