import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function ImpactAnalysisPage() {
  const { id } = useParams();
  const [impact, setImpact] = useState(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [saving, setSaving] = useState(false);

  const role = localStorage.getItem('mockRole');

  useEffect(() => {
    fetchImpact();
  }, [id]);

  const fetchImpact = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${id}/impact-analysis`);
      setImpact(res.data);
    } catch (err) {
      console.error('Error fetching Impact Analysis', err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateAI = async () => {
    try {
      setGenerating(true);
      const res = await api.post(`/requests/${id}/impact-analysis/ai-generate`);
      setImpact(res.data);
    } catch (err) {
      console.error('Error generating AI impact analysis', err);
      alert('Failed to generate AI impact analysis');
    } finally {
      setGenerating(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      const payload = {
        impactedApplications: impact.impactedApplications,
        impactedDatabases: impact.impactedDatabases,
        impactedApis: impact.impactedApis,
        impactedJobs: impact.impactedJobs,
        impactedQueues: impact.impactedQueues,
        integrationImpact: impact.integrationImpact,
        securityImpact: impact.securityImpact,
        performanceImpact: impact.performanceImpact,
        operationalImpact: impact.operationalImpact,
        dataImpact: impact.dataImpact,
        riskLevel: impact.riskLevel,
        mitigationPlan: impact.mitigationPlan
      };
      const res = await api.put(`/requests/${id}/impact-analysis`, payload);
      setImpact(res.data);
      alert('Impact Analysis saved successfully!');
    } catch (err) {
      console.error('Error saving Impact Analysis', err);
      alert('Failed to save Impact Analysis');
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e) => {
    setImpact({ ...impact, [e.target.name]: e.target.value });
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
          <h1 className="mt-2">Impact Analysis</h1>
        </div>
        <div className="flex gap-2">
          {role === 'SOLUTION_ARCHITECT' && (
            <>
              <button onClick={handleGenerateAI} disabled={generating} className="btn btn-secondary" style={{ borderColor: 'var(--secondary-500)', color: 'var(--secondary-600)' }}>
                {generating ? '✨ Generating...' : '✨ AI Generate Draft'}
              </button>
              <button onClick={handleSave} disabled={saving} className="btn btn-primary">
                {saving ? 'Saving...' : 'Save Analysis'}
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
              <div className="badge badge-blue">{impact.status}</div>
            </div>
            <div>
              <div className="text-muted" style={{ fontSize: '0.75rem' }}>Risk Level</div>
              <select className="form-control" style={{ padding: '0.2rem 1rem', display: 'inline-block', width: 'auto', fontWeight: 600, color: impact.riskLevel === 'HIGH' ? 'var(--danger-600)' : (impact.riskLevel === 'MEDIUM' ? 'var(--warning-600)' : 'var(--success-600)') }} name="riskLevel" value={impact.riskLevel} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>
          </div>
          <div className="text-muted" style={{ fontSize: '0.75rem' }}>
            Last updated: {dayjs(impact.updatedAt).format('MMM D, YYYY HH:mm')}
          </div>
        </div>

        <form>
          <div className="grid grid-cols-2 gap-4">
            <div className="form-group">
              <label className="form-label">Impacted Applications</label>
              <textarea className="form-control" name="impactedApplications" rows="2" value={impact.impactedApplications || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} />
            </div>
            <div className="form-group">
              <label className="form-label">Impacted Databases</label>
              <textarea className="form-control" name="impactedDatabases" rows="2" value={impact.impactedDatabases || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} />
            </div>
            <div className="form-group">
              <label className="form-label">Impacted APIs</label>
              <textarea className="form-control" name="impactedApis" rows="2" value={impact.impactedApis || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} />
            </div>
            <div className="form-group">
              <label className="form-label">Impacted Jobs/Queues</label>
              <textarea className="form-control" name="impactedJobs" rows="2" value={impact.impactedJobs || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} />
            </div>
          </div>

          <h4 className="mt-4 mb-2 border-b pb-2">Detailed Impacts (AI Assisted)</h4>
          <div className="grid grid-cols-2 gap-4">
            <div className="form-group">
              <label className="form-label">Integration Impact</label>
              <textarea className="form-control" name="integrationImpact" rows="3" value={impact.integrationImpact || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} style={{ backgroundColor: 'rgba(139, 92, 246, 0.03)' }} />
            </div>
            <div className="form-group">
              <label className="form-label">Security Impact</label>
              <textarea className="form-control" name="securityImpact" rows="3" value={impact.securityImpact || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} style={{ backgroundColor: 'rgba(139, 92, 246, 0.03)' }} />
            </div>
            <div className="form-group">
              <label className="form-label">Performance Impact</label>
              <textarea className="form-control" name="performanceImpact" rows="3" value={impact.performanceImpact || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} style={{ backgroundColor: 'rgba(139, 92, 246, 0.03)' }} />
            </div>
            <div className="form-group">
              <label className="form-label">Data Impact</label>
              <textarea className="form-control" name="dataImpact" rows="3" value={impact.dataImpact || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} style={{ backgroundColor: 'rgba(139, 92, 246, 0.03)' }} />
            </div>
          </div>

          <div className="form-group mt-4">
            <label className="form-label">Mitigation Plan</label>
            <textarea className="form-control" name="mitigationPlan" rows="4" value={impact.mitigationPlan || ''} onChange={handleChange} disabled={role !== 'SOLUTION_ARCHITECT'} />
          </div>
        </form>
      </div>
    </div>
  );
}
