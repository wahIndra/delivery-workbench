import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export default function CreateRequestPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    businessProblem: '',
    expectedOutcome: '',
    currentProcess: '',
    proposedChange: '',
    impactedUsers: '',
    impactedChannels: '',
    impactedSystems: '',
    priority: 'MEDIUM',
    deadline: '',
    deadlineReason: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const res = await api.post('/requests', formData);
      navigate(`/requests/${res.data.id}`);
    } catch (err) {
      console.error('Error creating request', err);
      alert('Failed to create request');
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div className="flex items-center justify-between mb-8">
        <h1>Create Delivery Request</h1>
        <button onClick={() => navigate(-1)} className="btn btn-secondary">Cancel</button>
      </div>

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Title *</label>
            <input type="text" className="form-control" name="title" required value={formData.title} onChange={handleChange} />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="form-group">
              <label className="form-label">Priority *</label>
              <select className="form-control" name="priority" value={formData.priority} onChange={handleChange}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Deadline</label>
              <input type="date" className="form-control" name="deadline" value={formData.deadline} onChange={handleChange} />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Deadline Reason</label>
            <input type="text" className="form-control" name="deadlineReason" value={formData.deadlineReason} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label className="form-label">Business Problem *</label>
            <textarea className="form-control" name="businessProblem" required value={formData.businessProblem} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label className="form-label">Expected Outcome *</label>
            <textarea className="form-control" name="expectedOutcome" required value={formData.expectedOutcome} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label className="form-label">Current Process</label>
            <textarea className="form-control" name="currentProcess" value={formData.currentProcess} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label className="form-label">Proposed Change</label>
            <textarea className="form-control" name="proposedChange" value={formData.proposedChange} onChange={handleChange} />
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="form-group">
              <label className="form-label">Impacted Users</label>
              <input type="text" className="form-control" name="impactedUsers" value={formData.impactedUsers} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label className="form-label">Impacted Channels</label>
              <input type="text" className="form-control" name="impactedChannels" value={formData.impactedChannels} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label className="form-label">Impacted Systems</label>
              <input type="text" className="form-control" name="impactedSystems" value={formData.impactedSystems} onChange={handleChange} />
            </div>
          </div>

          <div className="mt-8 flex justify-end">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Creating...' : 'Create Request'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
