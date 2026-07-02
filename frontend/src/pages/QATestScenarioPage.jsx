import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function QATestScenarioPage() {
  const { id } = useParams();
  const [scenarios, setScenarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [showForm, setShowForm] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    scenarioName: '',
    scenarioType: 'POSITIVE',
    precondition: '',
    testSteps: '',
    expectedResult: ''
  });

  const role = localStorage.getItem('mockRole');
  const isQA = role === 'QA' || role === 'ADMIN';

  useEffect(() => {
    fetchScenarios();
  }, [id]);

  const fetchScenarios = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${id}/qa-scenarios`);
      setScenarios(res.data);
    } catch (err) {
      console.error('Error fetching QA scenarios', err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateAI = async () => {
    try {
      setGenerating(true);
      const res = await api.post(`/requests/${id}/qa-scenarios/ai-generate`);
      setScenarios([...scenarios, ...res.data]);
    } catch (err) {
      console.error('Error generating AI QA scenarios', err);
      alert('Failed to generate AI scenarios');
    } finally {
      setGenerating(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post(`/requests/${id}/qa-scenarios`, formData);
      setScenarios([...scenarios, res.data]);
      setShowForm(false);
      setFormData({ scenarioName: '', scenarioType: 'POSITIVE', precondition: '', testSteps: '', expectedResult: '' });
    } catch (err) {
      console.error('Error creating scenario', err);
      alert('Failed to create scenario');
    }
  };

  const handleDelete = async (scenarioId) => {
    if (!confirm('Are you sure you want to delete this scenario?')) return;
    try {
      await api.delete(`/requests/${id}/qa-scenarios/${scenarioId}`);
      setScenarios(scenarios.filter(s => s.id !== scenarioId));
    } catch (err) {
      console.error('Error deleting scenario', err);
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
          <h1 className="mt-2">QA Test Scenarios</h1>
        </div>
        <div className="flex gap-2">
          {isQA && (
            <>
              <button onClick={handleGenerateAI} disabled={generating} className="btn btn-secondary" style={{ borderColor: 'var(--secondary-500)', color: 'var(--secondary-600)' }}>
                {generating ? '✨ Generating...' : '✨ AI Generate Scenarios'}
              </button>
              <button onClick={() => setShowForm(!showForm)} className="btn btn-primary">
                {showForm ? 'Cancel' : '+ Add Manual Scenario'}
              </button>
            </>
          )}
        </div>
      </div>

      {showForm && (
        <div className="card animate-fade-in" style={{ borderLeft: '4px solid var(--primary-600)' }}>
          <h3>Add Test Scenario</h3>
          <form onSubmit={handleCreate} className="mt-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="form-group">
                <label className="form-label">Scenario Name *</label>
                <input type="text" className="form-control" required value={formData.scenarioName} onChange={e => setFormData({...formData, scenarioName: e.target.value})} />
              </div>
              <div className="form-group">
                <label className="form-label">Scenario Type *</label>
                <select className="form-control" value={formData.scenarioType} onChange={e => setFormData({...formData, scenarioType: e.target.value})}>
                  <option value="POSITIVE">Positive</option>
                  <option value="NEGATIVE">Negative</option>
                  <option value="REGRESSION">Regression</option>
                  <option value="INTEGRATION">Integration</option>
                  <option value="PERFORMANCE">Performance</option>
                  <option value="SECURITY">Security</option>
                </select>
              </div>
            </div>
            
            <div className="form-group mt-2">
              <label className="form-label">Precondition</label>
              <input type="text" className="form-control" value={formData.precondition} onChange={e => setFormData({...formData, precondition: e.target.value})} />
            </div>

            <div className="grid grid-cols-2 gap-4 mt-2">
              <div className="form-group">
                <label className="form-label">Test Steps</label>
                <textarea className="form-control" rows="4" value={formData.testSteps} onChange={e => setFormData({...formData, testSteps: e.target.value})} />
              </div>
              <div className="form-group">
                <label className="form-label">Expected Result</label>
                <textarea className="form-control" rows="4" value={formData.expectedResult} onChange={e => setFormData({...formData, expectedResult: e.target.value})} />
              </div>
            </div>
            <div className="flex justify-end mt-4">
              <button type="submit" className="btn btn-primary">Save Scenario</button>
            </div>
          </form>
        </div>
      )}

      {scenarios.length === 0 && !showForm ? (
        <div className="card text-center p-8">
          <h3 className="text-muted">No test scenarios yet.</h3>
          <p>QA Engineers can generate AI scenarios based on Requirements and Impact Analysis.</p>
        </div>
      ) : (
        <div className="flex flex-col gap-4 mt-4">
          {scenarios.map((s) => (
            <div key={s.id} className="card" style={{ padding: '0', overflow: 'hidden' }}>
              <div className="flex justify-between items-center p-4" style={{ backgroundColor: 'var(--bg-surface-hover)', borderBottom: '1px solid var(--border-color)' }}>
                <div className="flex items-center gap-4">
                  <span className={`badge ${s.scenarioType === 'POSITIVE' ? 'badge-green' : (s.scenarioType === 'NEGATIVE' ? 'badge-red' : 'badge-purple')}`}>
                    {s.scenarioType}
                  </span>
                  <strong style={{ fontSize: '1.125rem' }}>{s.scenarioName}</strong>
                </div>
                <div className="flex gap-2 items-center">
                  {s.source === 'AI' && <span className="badge badge-purple" style={{ backgroundColor: 'transparent', border: '1px solid var(--secondary-500)' }}>AI Generated</span>}
                  {isQA && <button onClick={() => handleDelete(s.id)} className="btn btn-danger" style={{ padding: '0.25rem 0.5rem' }}>Delete</button>}
                </div>
              </div>
              <div className="p-4">
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <div className="form-label">Precondition</div>
                    <div style={{ fontSize: '0.875rem' }}>{s.precondition || '-'}</div>
                  </div>
                  <div>
                    <div className="form-label">Test Steps</div>
                    <div style={{ fontSize: '0.875rem', whiteSpace: 'pre-wrap' }}>{s.testSteps || '-'}</div>
                  </div>
                  <div>
                    <div className="form-label">Expected Result</div>
                    <div style={{ fontSize: '0.875rem', whiteSpace: 'pre-wrap' }}>{s.expectedResult || '-'}</div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
