import { useState, useEffect } from 'react';
import api from '../api';

export default function DashboardPage() {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchMetrics();
  }, []);

  const fetchMetrics = async () => {
    try {
      setLoading(true);
      const response = await api.get('/dashboard/metrics');
      setMetrics(response.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching dashboard metrics', err);
      setError('Failed to load dashboard metrics. Are you logged in with correct permissions?');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div><p className="mt-4">Loading dashboard...</p></div>;
  if (error) return <div className="card" style={{ borderColor: 'var(--danger-600)' }}><h3 style={{ color: 'var(--danger-600)' }}>Error</h3><p>{error}</p></div>;
  if (!metrics) return null;

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <h1>Lead Time Dashboard</h1>
        <button onClick={fetchMetrics} className="btn btn-secondary">Refresh</button>
      </div>

      <div className="grid grid-cols-4 gap-4 mb-8">
        <MetricCard title="Total Aging (>30d)" value={metrics.totalAgingRequests} color="var(--danger-600)" />
        <MetricCard title="Total Reworks" value={metrics.totalReworkCount} color="var(--warning-600)" />
        <MetricCard title="Avg Dev Cycle" value={`${metrics.avgDevCycleDays} days`} color="var(--primary-600)" />
        <MetricCard title="Avg Request to Ready" value={`${metrics.avgRequestToReadyDays} days`} color="var(--secondary-600)" />
      </div>

      <div className="grid grid-cols-2 gap-4 mb-8">
        <div className="card">
          <h3>Requests by Status</h3>
          <div className="mt-4">
            {Object.keys(metrics.totalRequestsByStatus || {}).length === 0 ? (
              <p className="text-muted">No active requests.</p>
            ) : (
              Object.entries(metrics.totalRequestsByStatus).map(([status, count]) => (
                <div key={status} className="flex justify-between items-center mb-2 pb-2" style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <span className="badge badge-gray">{status}</span>
                  <span style={{ fontWeight: 600 }}>{count}</span>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="card">
          <h3>Stuck Requests (>14 Days)</h3>
          <div className="mt-4">
            {Object.keys(metrics.stuckRequestsByStage || {}).length === 0 ? (
              <p className="text-muted" style={{ color: 'var(--success-600)' }}>Great! No requests are stuck.</p>
            ) : (
              Object.entries(metrics.stuckRequestsByStage).map(([status, count]) => (
                <div key={status} className="flex justify-between items-center mb-2 pb-2" style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <span className="badge badge-yellow">{status}</span>
                  <span style={{ fontWeight: 600, color: 'var(--danger-600)' }}>{count}</span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      <div className="card">
        <h3>Average Lead Times</h3>
        <div className="grid grid-cols-4 gap-4 mt-4">
          <div className="p-4" style={{ backgroundColor: 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Ready to Dev Start</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 600 }}>{metrics.avgReadyToDevStartDays} days</div>
          </div>
          <div className="p-4" style={{ backgroundColor: 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>SIT Duration</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 600 }}>{metrics.avgSitDays} days</div>
          </div>
          <div className="p-4" style={{ backgroundColor: 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>UAT Duration</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 600 }}>{metrics.avgUatDays} days</div>
          </div>
          <div className="p-4" style={{ backgroundColor: 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>UAT Signoff to Release</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 600 }}>{metrics.avgUatToReleaseDays} days</div>
          </div>
        </div>
      </div>
    </div>
  );
}

function MetricCard({ title, value, color }) {
  return (
    <div className="card" style={{ borderTop: `4px solid ${color}`, marginBottom: 0 }}>
      <div style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', fontWeight: 500 }}>{title}</div>
      <div style={{ fontSize: '2rem', fontWeight: 700, marginTop: '0.5rem', color: 'var(--text-primary)' }}>{value}</div>
    </div>
  );
}
