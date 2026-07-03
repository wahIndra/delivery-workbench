import { useState, useEffect } from 'react';
import api from '../api';
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, Legend, ResponsiveContainer, 
  LineChart, Line, PieChart, Pie, Cell 
} from 'recharts';
import dayjs from 'dayjs';
import { Link } from 'react-router-dom';

const COLORS = ['#6366f1', '#8b5cf6', '#0ea5e9', '#10b981', '#f59e0b', '#ef4444'];

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

  if (loading) return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '60vh' }}>
      <div className="spinner" style={{ width: '40px', height: '40px', borderWidth: '4px', borderLeftColor: '#38bdf8' }}></div>
      <p style={{ marginTop: '1.5rem', color: '#64748b', fontWeight: 500 }}>Loading Advanced Dashboard...</p>
    </div>
  );
  
  if (error) return (
    <div style={{ backgroundColor: '#fef2f2', border: '1px solid #fecaca', padding: '2rem', borderRadius: '12px', color: '#991b1b' }}>
      <h3 style={{ marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
        Dashboard Error
      </h3>
      <p>{error}</p>
      <button onClick={fetchMetrics} style={{ marginTop: '1rem', padding: '0.5rem 1rem', backgroundColor: '#991b1b', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>Try Again</button>
    </div>
  );
  
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
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', paddingBottom: '3rem' }}>
      {/* Header Row */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 800, color: '#0f172a', margin: 0, letterSpacing: '-0.02em' }}>Advanced Dashboard</h1>
          <p style={{ color: '#64748b', marginTop: '0.25rem', fontSize: '0.9375rem' }}>Real-time overview of IT delivery flow and governance</p>
        </div>
        <button onClick={fetchMetrics} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.625rem 1.25rem', backgroundColor: '#ffffff', border: '1px solid #e2e8f0', borderRadius: '8px', color: '#475569', fontWeight: 600, fontSize: '0.875rem', cursor: 'pointer', boxShadow: '0 1px 2px rgba(0,0,0,0.05)', transition: 'all 0.2s' }}
          onMouseOver={(e) => { e.currentTarget.style.backgroundColor = '#f8fafc'; e.currentTarget.style.color = '#0f172a'; }}
          onMouseOut={(e) => { e.currentTarget.style.backgroundColor = '#ffffff'; e.currentTarget.style.color = '#475569'; }}
        >
          <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24"><path d="M21 2v6h-6"></path><path d="M3 12a9 9 0 0 1 15-6.7L21 8"></path><path d="M3 22v-6h6"></path><path d="M21 12a9 9 0 0 1-15 6.7L3 16"></path></svg>
          Refresh Data
        </button>
      </div>

      {/* KPI Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1.5rem' }}>
        <MetricCard title="Total Aging (>30d)" value={metrics.totalAgingRequests} color="#ef4444" gradient="linear-gradient(to right, #ef4444, #f87171)" />
        <MetricCard title="Total Reworks" value={metrics.totalReworkCount} color="#f59e0b" gradient="linear-gradient(to right, #f59e0b, #fbbf24)" />
        <MetricCard title="Avg Dev Cycle" value={`${metrics.avgDevCycleDays} days`} color="#0ea5e9" gradient="linear-gradient(to right, #0ea5e9, #38bdf8)" />
        <MetricCard title="Avg Request to Ready" value={`${metrics.avgRequestToReadyDays} days`} color="#8b5cf6" gradient="linear-gradient(to right, #8b5cf6, #a855f7)" />
      </div>

      {/* Charts Row 1: Funnel & Trends */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
        <ChartCard title="Delivery Funnel (By Status)" height="360px">
          {statusData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={statusData} layout="vertical" margin={{ top: 10, right: 30, left: 40, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#e2e8f0" />
                <XAxis type="number" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis dataKey="name" type="category" width={120} stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                <RechartsTooltip cursor={{fill: 'rgba(99, 102, 241, 0.05)'}} contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)' }} />
                <Bar dataKey="value" fill="#6366f1" radius={[0, 4, 4, 0]} barSize={32} />
              </BarChart>
            </ResponsiveContainer>
          ) : <EmptyState message="No active requests." />}
        </ChartCard>

        <ChartCard title="Lead Time Trend (Monthly)" height="360px">
          {leadTimeData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={leadTimeData} margin={{ top: 10, right: 30, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                <XAxis dataKey="name" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} dy={10} />
                <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} dx={-10} />
                <RechartsTooltip contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)' }} />
                <Line type="monotone" dataKey="days" stroke="#10b981" strokeWidth={4} dot={{r: 6, fill: '#fff', strokeWidth: 2}} activeDot={{r: 8, stroke: '#fff', strokeWidth: 2}} />
              </LineChart>
            </ResponsiveContainer>
          ) : <EmptyState message="No released requests yet." />}
        </ChartCard>
      </div>

      {/* Charts Row 2: Workloads & Priority */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1.5rem' }}>
        <ChartCard title="Business Owner Workload" height="300px">
          {bizOwnerData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={bizOwnerData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                <XAxis dataKey="name" stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} dy={10} />
                <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} />
                <RechartsTooltip cursor={{fill: 'rgba(139, 92, 246, 0.05)'}} contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }} />
                <Bar dataKey="count" fill="#8b5cf6" radius={[4, 4, 0, 0]} maxBarSize={50} />
              </BarChart>
            </ResponsiveContainer>
          ) : <EmptyState message="No assignments." />}
        </ChartCard>

        <ChartCard title="IT Owner Workload" height="300px">
          {itOwnerData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={itOwnerData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                <XAxis dataKey="name" stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} dy={10} />
                <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} />
                <RechartsTooltip cursor={{fill: 'rgba(14, 165, 233, 0.05)'}} contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }} />
                <Bar dataKey="count" fill="#0ea5e9" radius={[4, 4, 0, 0]} maxBarSize={50} />
              </BarChart>
            </ResponsiveContainer>
          ) : <EmptyState message="No assignments." />}
        </ChartCard>

        <ChartCard title="Priority Distribution" height="300px">
          {priorityData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={priorityData} cx="50%" cy="45%" innerRadius={70} outerRadius={100} paddingAngle={4} dataKey="value" stroke="none">
                  {priorityData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <RechartsTooltip contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }} />
                <Legend iconType="circle" wrapperStyle={{ fontSize: '12px', color: '#64748b' }} />
              </PieChart>
            </ResponsiveContainer>
          ) : <EmptyState message="No data." />}
        </ChartCard>
      </div>

      {/* Actionable Tables Row */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
        <ActionListCard 
          title={`SLA Breached (${slaMetrics?.breachedRequests?.length || 0})`} 
          color="#ef4444" 
          icon={<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {slaMetrics?.breachedRequests?.map(r => (
              <div key={r.requestId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', backgroundColor: '#fef2f2', borderRadius: '8px', border: '1px solid #fee2e2' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                  <Link to={`/requests/${r.requestId}`} style={{ fontWeight: 600, color: '#991b1b', textDecoration: 'none' }}>{r.requestCode}</Link>
                  <span style={{ fontSize: '0.875rem', color: '#b91c1c', maxWidth: '300px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{r.title}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
                    <span style={{ fontSize: '0.75rem', fontWeight: 600, color: '#ef4444', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Overdue</span>
                    <span style={{ fontSize: '1rem', fontWeight: 700, color: '#991b1b', fontFamily: 'monospace' }}>{r.agingHours}h <span style={{ color: '#f87171', fontWeight: 400 }}>/ {r.slaHours}h</span></span>
                  </div>
                  <Link to={`/requests/${r.requestId}`} style={{ padding: '0.375rem 0.75rem', backgroundColor: 'white', color: '#ef4444', border: '1px solid #fca5a5', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, textDecoration: 'none' }}>View</Link>
                </div>
              </div>
            ))}
            {(!slaMetrics?.breachedRequests || slaMetrics.breachedRequests.length === 0) && (
              <div style={{ padding: '2rem', textAlign: 'center', color: '#10b981', backgroundColor: '#f0fdf4', borderRadius: '8px', border: '1px dashed #6ee7b7' }}>
                No breached requests! ??
              </div>
            )}
          </div>
        </ActionListCard>

        <ActionListCard 
          title={`Top Interventions Needed (${activeBottlenecks?.length || 0})`} 
          color="#f59e0b"
          icon={<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {activeBottlenecks?.map(b => (
              <div key={b.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', backgroundColor: '#fffbeb', borderRadius: '8px', border: '1px solid #fef3c7' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.375rem', flex: 1, paddingRight: '1rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ fontSize: '0.65rem', fontWeight: 700, padding: '0.125rem 0.5rem', borderRadius: '999px', backgroundColor: b.severity === 'CRITICAL' ? '#fee2e2' : '#ffedd5', color: b.severity === 'CRITICAL' ? '#991b1b' : '#9a3412', letterSpacing: '0.05em' }}>
                      {b.severity}
                    </span>
                    <span style={{ fontWeight: 600, color: '#92400e', fontSize: '0.875rem' }}>{b.findingType}</span>
                  </div>
                  <span style={{ fontSize: '0.8125rem', color: '#b45309', lineHeight: 1.4 }}>{b.description}</span>
                </div>
                <Link to={`/requests/${b.requestId}`} style={{ padding: '0.375rem 0.75rem', backgroundColor: 'white', color: '#f59e0b', border: '1px solid #fcd34d', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, textDecoration: 'none', whiteSpace: 'nowrap' }}>Resolve</Link>
              </div>
            ))}
            {(!activeBottlenecks || activeBottlenecks.length === 0) && (
               <div style={{ padding: '2rem', textAlign: 'center', color: '#64748b', backgroundColor: '#f8fafc', borderRadius: '8px', border: '1px dashed #cbd5e1' }}>
                No active bottlenecks!
              </div>
            )}
          </div>
        </ActionListCard>
      </div>

      {/* Releases Row */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
        <ChartCard title="Recent Releases (Last 5)" height="auto">
          <div style={{ overflowX: 'auto' }}>
            {metrics.recentReleases?.length > 0 ? (
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
                <thead>
                  <tr>
                    <th style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #e2e8f0', color: '#64748b', fontWeight: 600 }}>Request</th>
                    <th style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #e2e8f0', color: '#64748b', fontWeight: 600 }}>Title</th>
                    <th style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #e2e8f0', color: '#64748b', fontWeight: 600, textAlign: 'right' }}>Released Date</th>
                  </tr>
                </thead>
                <tbody>
                  {metrics.recentReleases.map(r => (
                    <tr key={r.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                      <td style={{ padding: '1rem' }}><Link to={`/requests/${r.id}`} style={{ fontWeight: 600, color: '#6366f1', textDecoration: 'none' }}>{r.requestCode}</Link></td>
                      <td style={{ padding: '1rem', color: '#334155' }}>{r.title}</td>
                      <td style={{ padding: '1rem', color: '#64748b', textAlign: 'right' }}>{dayjs(r.releasedAt).format('MMM D, YYYY')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : <EmptyState message="No recent releases." />}
          </div>
        </ChartCard>

        <ChartCard title="Upcoming Releases (Candidates)" height="auto">
          <div style={{ overflowX: 'auto' }}>
            {metrics.upcomingReleases?.length > 0 ? (
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
                <thead>
                  <tr>
                    <th style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #e2e8f0', color: '#64748b', fontWeight: 600 }}>Request</th>
                    <th style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #e2e8f0', color: '#64748b', fontWeight: 600 }}>Title</th>
                    <th style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #e2e8f0', color: '#64748b', fontWeight: 600, textAlign: 'right' }}>Ready Since</th>
                  </tr>
                </thead>
                <tbody>
                  {metrics.upcomingReleases.map(r => (
                    <tr key={r.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                      <td style={{ padding: '1rem' }}><Link to={`/requests/${r.id}`} style={{ fontWeight: 600, color: '#6366f1', textDecoration: 'none' }}>{r.requestCode}</Link></td>
                      <td style={{ padding: '1rem', color: '#334155' }}>{r.title}</td>
                      <td style={{ padding: '1rem', color: '#64748b', textAlign: 'right' }}>{dayjs(r.updatedAt).format('MMM D, YYYY')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : <EmptyState message="No upcoming candidates." />}
          </div>
        </ChartCard>
      </div>
    </div>
  );
}

// Subcomponents for cleaner code

function MetricCard({ title, value, color, gradient }) {
  return (
    <div style={{ 
      backgroundColor: 'white', 
      borderRadius: '16px', 
      padding: '1.5rem', 
      boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03)',
      border: '1px solid #f1f5f9',
      position: 'relative',
      overflow: 'hidden',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center'
    }}>
      <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: '4px', background: gradient }}></div>
      <div style={{ color: '#64748b', fontWeight: 600, fontSize: '0.8125rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem' }}>{title}</div>
      <div style={{ fontSize: '2.25rem', fontWeight: 800, color: color, lineHeight: 1.2 }}>
        {value}
      </div>
    </div>
  );
}

function ChartCard({ title, height, children }) {
  return (
    <div style={{ 
      backgroundColor: 'white', 
      borderRadius: '16px', 
      boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03)',
      border: '1px solid #f1f5f9',
      display: 'flex',
      flexDirection: 'column'
    }}>
      <div style={{ padding: '1.5rem 1.5rem 0.5rem 1.5rem' }}>
        <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 700, color: '#0f172a' }}>{title}</h3>
      </div>
      <div style={{ padding: '1rem 1.5rem 1.5rem 1.5rem', height: height, flex: 1 }}>
        {children}
      </div>
    </div>
  );
}

function ActionListCard({ title, color, icon, children }) {
  return (
    <div style={{ 
      backgroundColor: 'white', 
      borderRadius: '16px', 
      boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03)',
      border: '1px solid #f1f5f9',
      display: 'flex',
      flexDirection: 'column',
      maxHeight: '400px'
    }}>
      <div style={{ padding: '1.25rem 1.5rem', borderBottom: '1px solid #f1f5f9', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <div style={{ color: color }}>{icon}</div>
        <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 700, color: '#0f172a' }}>{title}</h3>
      </div>
      <div style={{ padding: '1.5rem', overflowY: 'auto', flex: 1 }}>
        {children}
      </div>
    </div>
  );
}

function EmptyState({ message }) {
  return (
    <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#94a3b8', fontSize: '0.9375rem' }}>
      {message}
    </div>
  );
}
