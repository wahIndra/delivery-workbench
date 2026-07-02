import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function RequirementRefinementPage() {
  const { id } = useParams();
  const [requirement, setRequirement] = useState(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [saving, setSaving] = useState(false);

  const role = localStorage.getItem('mockRole');

  useEffect(() => {
    fetchRequirement();
  }, [id]);

  const fetchRequirement = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${id}/requirements`);
      setRequirement(res.data);
    } catch (err) {
      console.error('Error fetching requirement', err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateAI = async () => {
    try {
      setGenerating(true);
      const res = await api.post(`/requests/${id}/requirements/ai-generate`);
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
        dependencies: requirement.dependencies
      };
      const res = await api.put(`/requests/${id}/requirements`, payload);
      setRequirement(res.data);
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
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
          <h1 className="mt-2">Requirement Refinement</h1>
        </div>
        <div className="flex gap-2">
          {role === 'SYSTEM_ANALYST' && (
            <>
              <button onClick={handleGenerateAI} disabled={generating} className="btn btn-secondary" style={{ borderColor: 'var(--secondary-500)', color: 'var(--secondary-600)' }}>
                {generating ? '✨ Generating...' : '✨ AI Generate User Story & AC'}
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
              <div className="badge badge-blue">{requirement.status}</div>
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
        </form>
      </div>
    </div>
  );
}
