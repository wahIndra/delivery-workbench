import { useState, useEffect } from 'react';
import api from '../api';
import dayjs from 'dayjs';
import { Link } from 'react-router-dom';

export default function ReleaseCalendarPage() {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchSchedules();
  }, []);

  const fetchSchedules = async () => {
    try {
      setLoading(true);
      const res = await api.get('/release-schedules');
      setSchedules(res.data);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch schedules', err);
      setError('Failed to load release calendar. You might not have permission.');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PLANNED': return 'bg-gray-100 text-gray-800';
      case 'READY': return 'bg-blue-100 text-blue-800';
      case 'RELEASED': return 'bg-green-100 text-green-800';
      case 'ROLLED_BACK': return 'bg-red-100 text-red-800';
      case 'CANCELLED': return 'bg-orange-100 text-orange-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div><p className="mt-4">Loading calendar...</p></div>;
  if (error) return <div className="card border-red-500"><h3 className="text-red-700">Error</h3><p>{error}</p></div>;

  return (
    <div className="max-w-7xl mx-auto pb-12 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="m-0 text-gray-800">Release Calendar</h1>
        <button onClick={fetchSchedules} className="btn btn-secondary">Refresh</button>
      </div>

      <div className="card shadow-sm">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b bg-gray-50 text-gray-500">
              <th className="p-3 font-medium">Release Title</th>
              <th className="p-3 font-medium">Request</th>
              <th className="p-3 font-medium">Planned Date</th>
              <th className="p-3 font-medium">Actual Date</th>
              <th className="p-3 font-medium">Release Manager</th>
              <th className="p-3 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {schedules.map(schedule => (
              <tr key={schedule.id} className="border-b last:border-0 hover:bg-gray-50">
                <td className="p-3 font-medium text-gray-800">{schedule.releaseTitle}</td>
                <td className="p-3">
                  <Link to={`/requests/${schedule.requestId}`} className="text-indigo-600 hover:underline">View Request</Link>
                </td>
                <td className="p-3">{schedule.plannedReleaseDate ? dayjs(schedule.plannedReleaseDate).format('MMM D, YYYY HH:mm') : '-'}</td>
                <td className="p-3">{schedule.actualReleaseDate ? dayjs(schedule.actualReleaseDate).format('MMM D, YYYY HH:mm') : '-'}</td>
                <td className="p-3 text-gray-600">{schedule.releaseManager || '-'}</td>
                <td className="p-3">
                  <span className={`badge ${getStatusColor(schedule.releaseStatus)}`}>{schedule.releaseStatus}</span>
                </td>
              </tr>
            ))}
            {schedules.length === 0 && (
              <tr>
                <td colSpan="6" className="p-8 text-center text-gray-500">No release schedules found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
