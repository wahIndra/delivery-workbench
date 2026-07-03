import { Link, useLocation, useNavigate } from 'react-router-dom';
import NotificationBell from './NotificationBell';

export default function Layout({ children }) {
  const location = useLocation();
  const navigate = useNavigate();
  
  const username = localStorage.getItem('mockUsername') || 'Not Logged In';
  const role = localStorage.getItem('mockRole') || 'GUEST';

  const handleLogout = () => {
    localStorage.removeItem('mockUsername');
    localStorage.removeItem('mockRole');
    navigate('/login');
  };

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { name: 'Requests', path: '/requests', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01' },
    { name: 'Release Calendar', path: '/release-calendar', icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z' },
    { name: 'AI Audit Logs', path: '/ai-audit-logs', icon: 'M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z' },
    { name: 'Admin', path: '/admin/users', icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' },
  ];

  return (
    <div className="app-container">
      <aside className="sidebar" style={{ backgroundColor: '#020617', borderRight: '1px solid rgba(255,255,255,0.05)' }}>
        <div className="sidebar-header" style={{ padding: '1.5rem', borderBottom: '1px solid rgba(255,255,255,0.05)', background: 'transparent' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', background: 'linear-gradient(135deg, #38bdf8, #8b5cf6)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 12px rgba(56, 189, 248, 0.2)' }}>
               <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path></svg>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <span style={{ fontSize: '0.875rem', fontWeight: 800, letterSpacing: '0.05em', color: '#f8fafc', lineHeight: 1 }}>DELIVERY</span>
              <span style={{ fontSize: '0.75rem', fontWeight: 600, letterSpacing: '0.1em', color: '#38bdf8', marginTop: '2px' }}>WORKBENCH</span>
            </div>
          </div>
        </div>

        <nav className="sidebar-nav" style={{ padding: '1.5rem 0' }}>
          <div style={{ padding: '0 1.5rem', fontSize: '0.7rem', fontWeight: 600, color: '#64748b', letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: '0.75rem' }}>Menu</div>
          {navItems.map((item) => {
            const isActive = location.pathname.startsWith(item.path);
            return (
              <Link
                key={item.path}
                to={item.path}
                className="nav-item"
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.75rem',
                  padding: '0.75rem 1.5rem',
                  color: isActive ? '#f8fafc' : '#94a3b8',
                  backgroundColor: isActive ? 'rgba(56, 189, 248, 0.1)' : 'transparent',
                  borderLeft: `3px solid ${isActive ? '#38bdf8' : 'transparent'}`,
                  textDecoration: 'none',
                  fontSize: '0.9375rem',
                  fontWeight: isActive ? 600 : 500,
                  transition: 'all 0.2s'
                }}
                onMouseOver={(e) => { if(!isActive) { e.currentTarget.style.color = '#f8fafc'; e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.03)'; } }}
                onMouseOut={(e) => { if(!isActive) { e.currentTarget.style.color = '#94a3b8'; e.currentTarget.style.backgroundColor = 'transparent'; } }}
              >
                <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                  <path d={item.icon}></path>
                </svg>
                {item.name}
              </Link>
            );
          })}
        </nav>

        <div className="sidebar-footer" style={{ padding: '1.5rem', borderTop: '1px solid rgba(255,255,255,0.05)', backgroundColor: 'transparent' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.25rem', padding: '0.75rem', backgroundColor: 'rgba(255,255,255,0.03)', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.05)' }}>
             <div style={{ width: '32px', height: '32px', borderRadius: '50%', backgroundColor: '#1e293b', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#94a3b8' }}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
             </div>
             <div style={{ overflow: 'hidden' }}>
               <div style={{ color: '#f8fafc', fontWeight: 600, fontSize: '0.875rem', whiteSpace: 'nowrap', textOverflow: 'ellipsis', overflow: 'hidden' }}>{username}</div>
               <div style={{ color: '#38bdf8', fontSize: '0.7rem', fontWeight: 600, letterSpacing: '0.05em' }}>{role}</div>
             </div>
          </div>
          <button onClick={handleLogout} style={{ width: '100%', padding: '0.625rem', backgroundColor: 'transparent', border: '1px solid rgba(255,255,255,0.1)', color: '#94a3b8', borderRadius: '8px', fontSize: '0.8125rem', fontWeight: 500, cursor: 'pointer', transition: 'all 0.2s', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
            onMouseOver={(e) => { e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.05)'; e.currentTarget.style.color = '#f8fafc'; }}
            onMouseOut={(e) => { e.currentTarget.style.backgroundColor = 'transparent'; e.currentTarget.style.color = '#94a3b8'; }}
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
            Sign Out
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main-content" style={{ backgroundColor: '#f1f5f9' }}>
        <header className="top-header" style={{ height: '72px', padding: '0 2rem', backgroundColor: 'rgba(255, 255, 255, 0.9)', backdropFilter: 'blur(12px)', borderBottom: '1px solid #e2e8f0', display: 'flex', alignItems: 'center', justifyContent: 'space-between', position: 'sticky', top: 0, zIndex: 40, boxShadow: '0 1px 3px rgba(0,0,0,0.02)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
             <div style={{ fontWeight: 600, color: '#0f172a', fontSize: '1.125rem' }}>
               IT Delivery Governance
             </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
            <span style={{ backgroundColor: 'rgba(139, 92, 246, 0.1)', color: '#7c3aed', padding: '0.25rem 0.75rem', borderRadius: '9999px', fontSize: '0.75rem', fontWeight: 700, letterSpacing: '0.05em', border: '1px solid rgba(139, 92, 246, 0.2)' }}>BETA</span>
            <div style={{ height: '24px', width: '1px', backgroundColor: '#e2e8f0' }}></div>
            <NotificationBell />
          </div>
        </header>
        
        <div className="page-container animate-fade-in" style={{ padding: '2.5rem', flex: 1, maxWidth: '1600px', margin: '0 auto', width: '100%', overflowY: 'auto' }}>
          {children}
        </div>
      </main>
    </div>
  );
}
