import { useState, useEffect } from 'react';
import api from '../api';
import dayjs from 'dayjs';

export default function ReleaseScheduleSection({ requestId }) {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);

  // Form State
  const [showForm, setShowForm] = useState(false);
  const [releaseTitle, setReleaseTitle] = useState('');
  const [plannedReleaseDate, setPlannedReleaseDate] = useState('');
  const [releaseWindow, setReleaseWindow] = useState('');
  const [releaseManager, setReleaseManager] = useState('');

  useEffect(() => {
    fetchSchedules();
  }, [requestId]);

  const fetchSchedules = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${requestId}/release-schedules`);
      setSchedules(res.data);
    } catch (err) {
      console.error('Failed to fetch schedules', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/requests/${requestId}/release-schedules`, {
        releaseTitle,
        plannedReleaseDate: plannedReleaseDate ? new Date(plannedReleaseDate).toISOString() : null,
        releaseWindow,
        releaseManager
      });
      setShowForm(false);
      setReleaseTitle('');
      setPlannedReleaseDate('');
      setReleaseWindow('');
      setReleaseManager('');
      fetchSchedules();
    } catch (err) {
      console.error('Failed to create schedule', err);
      alert('Error creating schedule.');
    }
  };

  const handleStatusChange = async (id, status) => {
    try {
      await api.put(`/release-schedules/${id}`, { releaseStatus: status });
      fetchSchedules();
    } catch (err) {
      alert(err.response?.data?.message || 'Error updating status');
    }
  };

  if (loading) return <div className="text-center p-4"><div className="spinner"></div></div>;

  return (
    <div className="card mt-6">
      <div className="flex justify-between items-center mb-4">
        <h3>Release Schedules</h3>
        <button onClick={() => setShowForm(!showForm)} className="btn btn-secondary">
          {showForm ? 'Cancel' : 'Plan Release'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="bg-gray-50 p-4 rounded-md border mb-4 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="form-label">Release Title</label>
              <input type="text" className="form-control" required value={releaseTitle} onChange={e => setReleaseTitle(e.target.value)} />
            </div>
            <div>
              <label className="form-label">Planned Date</label>
              <input type="datetime-local" className="form-control" value={plannedReleaseDate} onChange={e => setPlannedReleaseDate(e.target.value)} />
            </div>
            <div>
              <label className="form-label">Release Window</label>
              <input type="text" className="form-control" placeholder="e.g. Sat 02:00-04:00 AM" value={releaseWindow} onChange={e => setReleaseWindow(e.target.value)} />
            </div>
            <div>
              <label className="form-label">Release Manager</label>
              <input type="text" className="form-control" value={releaseManager} onChange={e => setReleaseManager(e.target.value)} />
            </div>
          </div>
          <button type="submit" className="btn btn-primary">Save Schedule</button>
        </form>
      )}

      <div className="space-y-3">
        {schedules.map(s => (
          <div key={s.id} className="border rounded-md p-4 flex justify-between items-center">
            <div>
              <div className="font-semibold text-lg">{s.releaseTitle}</div>
              <div className="text-sm text-gray-600 mt-1">
                <strong>Planned:</strong> {s.plannedReleaseDate ? dayjs(s.plannedReleaseDate).format('MMM D, YYYY HH:mm') : 'TBD'} | 
                <strong> Window:</strong> {s.releaseWindow || 'N/A'} | 
                <strong> Manager:</strong> {s.releaseManager || 'Unassigned'}
              </div>
              <div className="mt-2">
                <span className="badge bg-indigo-100 text-indigo-800">{s.releaseStatus}</span>
              </div>
            </div>
            <div className="flex flex-col gap-2">
              {s.releaseStatus === 'PLANNED' && (
                <button onClick={() => handleStatusChange(s.id, 'READY')} className="btn btn-secondary text-xs">Mark Ready</button>
              )}
              {s.releaseStatus === 'READY' && (
                <button onClick={() => handleStatusChange(s.id, 'RELEASED')} className="btn btn-primary text-xs">Execute Release</button>
              )}
              {['PLANNED', 'READY'].includes(s.releaseStatus) && (
                <button onClick={() => handleStatusChange(s.id, 'CANCELLED')} className="btn text-red-600 border border-red-200 hover:bg-red-50 text-xs">Cancel</button>
              )}
            </div>
          </div>
        ))}
        {schedules.length === 0 && <div className="text-gray-500 text-center p-4">No releases planned.</div>}
      </div>
    </div>
  );
}
