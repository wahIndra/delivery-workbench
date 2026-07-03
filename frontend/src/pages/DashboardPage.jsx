import { useState, useEffect } from 'react';
import api from '../api';
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, Legend, ResponsiveContainer, 
  LineChart, Line, PieChart, Pie, Cell 
} from 'recharts';
import dayjs from 'dayjs';
import { Link } from 'react-router-dom';

const COLORS = ['#4f46e5', '#10b981', '#f59e0b', '#ef4444', '#06b6d4', '#8b5cf6'];

export default function DashboardPage() {
  const [metrics, setMetrics] = useState(null);
  const [slaMetrics, setSlaMetrics] = useState(null);
  const [activeBottlenecks, setActiveBottlenecks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchMetrics();
  }, []);

  const fetchMetrics = async () => {
    try {
      setLoading(true);
      const [dashRes, slaRes, bottleneckRes] = await Promise.all([
        api.get('/dashboard/metrics'),
        api.get('/dashboard/sla-metrics').catch(() => ({ data: null })),
        api.get('/dashboard/bottlenecks/active').catch(() => ({ data: [] }))
      ]);
      setMetrics(dashRes.data);
      if (slaRes.data) {
        setSlaMetrics(slaRes.data);
      }
      if (bottleneckRes.data) {
        setActiveBottlenecks(bottleneckRes.data);
      }
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

  // Format Data for Charts
  const statusData = Object.keys(metrics.totalRequestsByStatus || {}).map(status => ({
    name: status,
    value: metrics.totalRequestsByStatus[status]
  }));

  const leadTimeData = Object.keys(metrics.avgLeadTimeByMonth || {}).sort().map(month => ({
    name: month,
    days: metrics.avgLeadTimeByMonth[month]
  }));

  const bizOwnerData = Object.keys(metrics.requestsByBusinessOwner || {}).map(owner => ({
    name: owner,
    count: metrics.requestsByBusinessOwner[owner]
  }));

  const itOwnerData = Object.keys(metrics.requestsByItOwner || {}).map(owner => ({
    name: owner,
    count: metrics.requestsByItOwner[owner]
  }));

  const priorityData = Object.keys(metrics.requestsByPriority || {}).map((pri, index) => ({
    name: pri,
    value: metrics.requestsByPriority[pri],
    color: COLORS[index % COLORS.length]
  }));

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-12">
      <div className="flex items-center justify-between">
        <h1 className="m-0 text-gray-800">Advanced Dashboard</h1>
        <div className="flex gap-4">
          <button onClick={fetchMetrics} className="btn btn-secondary">Refresh</button>
        </div>
      </div>

      {/* KPI Row */}
      <div className="grid grid-cols-4 gap-4">
        <MetricCard title="Total Aging (>30d)" value={metrics.totalAgingRequests} color="var(--danger-600)" />
        <MetricCard title="Total Reworks" value={metrics.totalReworkCount} color="var(--warning-600)" />
        <MetricCard title="Avg Dev Cycle" value={`${metrics.avgDevCycleDays} days`} color="var(--primary-600)" />
        <MetricCard title="Avg Request to Ready" value={`${metrics.avgRequestToReadyDays} days`} color="var(--secondary-600)" />
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-2 gap-4">
        <div className="card shadow-sm h-80">
          <h3 className="mb-4 text-gray-700">Delivery Funnel (By Status)</h3>
          {statusData.length > 0 ? (
            <ResponsiveContainer width="100%" height="85%">
              <BarChart data={statusData} layout="vertical" margin={{ top: 5, right: 30, left: 40, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} />
                <XAxis type="number" />
                <YAxis dataKey="name" type="category" width={120} tick={{fontSize: 11}} />
                <RechartsTooltip cursor={{fill: 'transparent'}} />
                <Bar dataKey="value" fill="#6366f1" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : <p className="text-gray-400 mt-8 text-center">No active requests.</p>}
        </div>

        <div className="card shadow-sm h-80">
          <h3 className="mb-4 text-gray-700">Lead Time Trend (Monthly)</h3>
          {leadTimeData.length > 0 ? (
            <ResponsiveContainer width="100%" height="85%">
              <LineChart data={leadTimeData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" tick={{fontSize: 12}} />
                <YAxis tick={{fontSize: 12}} />
                <RechartsTooltip />
                <Line type="monotone" dataKey="days" stroke="#10b981" strokeWidth={3} dot={{r: 4}} />
              </LineChart>
            </ResponsiveContainer>
          ) : <p className="text-gray-400 mt-8 text-center">No released requests yet.</p>}
        </div>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-3 gap-4">
        <div className="card shadow-sm h-72">
          <h3 className="mb-4 text-gray-700">Business Owner Workload</h3>
          {bizOwnerData.length > 0 ? (
            <ResponsiveContainer width="100%" height="85%">
              <BarChart data={bizOwnerData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" tick={{fontSize: 10}} />
                <YAxis />
                <RechartsTooltip />
                <Bar dataKey="count" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : <p className="text-gray-400 mt-8 text-center">No assignments.</p>}
        </div>

        <div className="card shadow-sm h-72">
          <h3 className="mb-4 text-gray-700">IT Owner Workload</h3>
          {itOwnerData.length > 0 ? (
            <ResponsiveContainer width="100%" height="85%">
              <BarChart data={itOwnerData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" tick={{fontSize: 10}} />
                <YAxis />
                <RechartsTooltip />
                <Bar dataKey="count" fill="#0ea5e9" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : <p className="text-gray-400 mt-8 text-center">No assignments.</p>}
        </div>

        <div className="card shadow-sm h-72">
          <h3 className="mb-4 text-gray-700">Priority Distribution</h3>
          {priorityData.length > 0 ? (
            <ResponsiveContainer width="100%" height="85%">
              <PieChart>
                <Pie data={priorityData} cx="50%" cy="50%" innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                  {priorityData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <RechartsTooltip />
                <Legend iconType="circle" />
              </PieChart>
            </ResponsiveContainer>
          ) : <p className="text-gray-400 mt-8 text-center">No data.</p>}
        </div>
      </div>

      {/* Actionable Tables Row */}
      <div className="grid grid-cols-2 gap-4">
        <div className="card border-red-500 shadow-sm flex flex-col h-80">
          <h3 className="text-red-700 mb-4 sticky top-0 bg-white">SLA Breached ({slaMetrics?.breachedRequests?.length || 0})</h3>
          <div className="flex-1 overflow-y-auto pr-2">
            <div className="flex flex-col gap-2">
              {slaMetrics?.breachedRequests?.map(r => (
                <div key={r.requestId} className="flex justify-between items-center pb-2 border-b">
                  <div className="flex flex-col">
                    <span className="font-semibold text-gray-800">{r.requestCode}</span>
                    <span className="text-xs text-gray-500 truncate max-w-[200px]">{r.title}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="badge bg-red-100 text-red-800 font-mono text-xs">{r.agingHours}h / {r.slaHours}h</span>
                    <Link to={`/requests/${r.requestId}`} className="text-xs text-indigo-600 hover:underline">View</Link>
                  </div>
                </div>
              ))}
              {(!slaMetrics?.breachedRequests || slaMetrics.breachedRequests.length === 0) && <span className="text-gray-500 mt-4">No breached requests! 🎉</span>}
            </div>
          </div>
        </div>

        <div className="card border-orange-500 shadow-sm flex flex-col h-80">
          <h3 className="text-orange-700 mb-4 sticky top-0 bg-white">Top Interventions Needed ({activeBottlenecks?.length || 0})</h3>
          <div className="flex-1 overflow-y-auto pr-2">
            <div className="flex flex-col gap-2">
              {activeBottlenecks?.map(b => (
                <div key={b.id} className="flex justify-between items-center pb-2 border-b">
                  <div className="flex flex-col gap-1 w-full mr-2">
                    <div className="flex gap-2 items-center">
                      <span className={`badge text-[10px] ${b.severity === 'CRITICAL' ? 'bg-red-200 text-red-900' : 'bg-orange-100 text-orange-800'}`}>{b.severity}</span>
                      <span className="font-semibold text-gray-800 text-sm">{b.findingType}</span>
                    </div>
                    <span className="text-xs text-gray-600 truncate max-w-[280px]">{b.description}</span>
                  </div>
                  <Link to={`/requests/${b.requestId}`} className="text-xs text-indigo-600 hover:underline whitespace-nowrap">Resolve</Link>
                </div>
              ))}
              {(!activeBottlenecks || activeBottlenecks.length === 0) && <span className="text-gray-500 mt-4">No active bottlenecks!</span>}
            </div>
          </div>
        </div>
      </div>

      {/* Releases Row */}
      <div className="grid grid-cols-2 gap-4">
        <div className="card shadow-sm h-64 flex flex-col">
          <h3 className="text-gray-700 mb-4 sticky top-0 bg-white">Recent Releases (Last 5)</h3>
          <div className="flex-1 overflow-y-auto">
            {metrics.recentReleases?.length > 0 ? (
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="text-gray-500 border-b">
                    <th className="pb-2 font-medium">Request</th>
                    <th className="pb-2 font-medium">Title</th>
                    <th className="pb-2 font-medium text-right">Released Date</th>
                  </tr>
                </thead>
                <tbody>
                  {metrics.recentReleases.map(r => (
                    <tr key={r.id} className="border-b last:border-0 hover:bg-gray-50">
                      <td className="py-2"><Link to={`/requests/${r.id}`} className="text-indigo-600 hover:underline">{r.requestCode}</Link></td>
                      <td className="py-2 text-gray-600 truncate max-w-[150px]">{r.title}</td>
                      <td className="py-2 text-right text-gray-500">{dayjs(r.releasedAt).format('MMM D, YYYY')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : <p className="text-gray-400 mt-4">No recent releases.</p>}
          </div>
        </div>

        <div className="card shadow-sm h-64 flex flex-col">
          <h3 className="text-gray-700 mb-4 sticky top-0 bg-white">Upcoming Releases (Candidates)</h3>
          <div className="flex-1 overflow-y-auto">
            {metrics.upcomingReleases?.length > 0 ? (
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="text-gray-500 border-b">
                    <th className="pb-2 font-medium">Request</th>
                    <th className="pb-2 font-medium">Title</th>
                    <th className="pb-2 font-medium text-right">Ready Since</th>
                  </tr>
                </thead>
                <tbody>
                  {metrics.upcomingReleases.map(r => (
                    <tr key={r.id} className="border-b last:border-0 hover:bg-gray-50">
                      <td className="py-2"><Link to={`/requests/${r.id}`} className="text-indigo-600 hover:underline">{r.requestCode}</Link></td>
                      <td className="py-2 text-gray-600 truncate max-w-[150px]">{r.title}</td>
                      <td className="py-2 text-right text-gray-500">{dayjs(r.updatedAt).format('MMM D, YYYY')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : <p className="text-gray-400 mt-4">No upcoming candidates.</p>}
          </div>
        </div>
      </div>

    </div>
  );
}

function MetricCard({ title, value, color }) {
  return (
    <div className="card animate-slide-up group" style={{ borderTop: `4px solid ${color}`, background: 'var(--gradient-card)' }}>
      <div className="text-gray-500 font-medium text-sm tracking-wide uppercase">{title}</div>
      <div className="text-3xl font-bold mt-2 bg-clip-text text-transparent group-hover:scale-105 transition-transform origin-left" style={{ background: 'var(--gradient-primary)', WebkitBackgroundClip: 'text', color: color }}>
        {value}
      </div>
    </div>
  );
}
