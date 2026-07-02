import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { decisionLogApi } from '../api';

export default function DecisionLogPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  const [form, setForm] = useState({
    decisionTitle: '',
    decisionDescription: '',
    decisionType: 'TECHNICAL',
    decidedBy: '',
    decisionDate: dayjs().format('YYYY-MM-DD'),
    impact: ''
  });

  useEffect(() => {
    fetchLogs();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchLogs = async () => {
    try {
      const res = await decisionLogApi.getLogs(id);
      setLogs(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await decisionLogApi.create(id, form);
      setForm({
        ...form,
        decisionTitle: '',
        decisionDescription: '',
        impact: ''
      });
      fetchLogs();
    } catch (err) {
      alert('Failed to save decision log');
    }
  };

  if (loading) return <div className="p-8 text-center">Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h2>Decision Log</h2>
        <button onClick={() => navigate(`/requests/${id}`)} className="btn btn-secondary">Back to Request</button>
      </div>

      <div className="card mb-8">
        <h3>Log a New Decision</h3>
        <form onSubmit={handleCreate} className="mt-4 flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="form-label">Decision Title *</label>
              <input type="text" className="form-control" required
                value={form.decisionTitle} onChange={e => setForm({...form, decisionTitle: e.target.value})} />
            </div>
            <div>
              <label className="form-label">Decision Type *</label>
              <select className="form-control" value={form.decisionType} onChange={e => setForm({...form, decisionType: e.target.value})}>
                <option value="SCOPE">SCOPE</option>
                <option value="TECHNICAL">TECHNICAL</option>
                <option value="BUSINESS">BUSINESS</option>
                <option value="RISK">RISK</option>
                <option value="RELEASE">RELEASE</option>
                <option value="PRIORITY">PRIORITY</option>
              </select>
            </div>
            <div>
              <label className="form-label">Decided By *</label>
              <input type="text" className="form-control" required
                value={form.decidedBy} onChange={e => setForm({...form, decidedBy: e.target.value})} />
            </div>
            <div>
              <label className="form-label">Decision Date *</label>
              <input type="date" className="form-control" required
                value={form.decisionDate} onChange={e => setForm({...form, decisionDate: e.target.value})} />
            </div>
          </div>
          <div>
            <label className="form-label">Decision Description *</label>
            <textarea className="form-control" rows="3" required
              value={form.decisionDescription} onChange={e => setForm({...form, decisionDescription: e.target.value})}></textarea>
          </div>
          <div>
            <label className="form-label">Impact</label>
            <textarea className="form-control" rows="2"
              value={form.impact} onChange={e => setForm({...form, impact: e.target.value})}></textarea>
          </div>
          <div>
            <button type="submit" className="btn btn-primary">Save Decision</button>
          </div>
        </form>
      </div>

      <h3>Decision History</h3>
      <div className="mt-4 flex flex-col gap-4">
        {logs.length === 0 ? (
          <p className="text-gray-500">No decisions logged yet.</p>
        ) : (
          logs.map(log => (
            <div key={log.id} className="card border-l-4 border-primary-500">
              <div className="flex justify-between items-start mb-2">
                <h4 className="m-0 text-primary-700">{log.decisionTitle}</h4>
                <div className="text-sm text-gray-500">{dayjs(log.decisionDate).format('MMM D, YYYY')}</div>
              </div>
              <div className="flex gap-2 mb-4">
                <span className="badge badge-blue">{log.decisionType}</span>
                <span className="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">By {log.decidedBy}</span>
              </div>
              <p className="text-sm mb-2">{log.decisionDescription}</p>
              {log.impact && (
                <div className="text-xs bg-orange-50 text-orange-800 p-2 rounded">
                  <strong>Impact:</strong> {log.impact}
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}
