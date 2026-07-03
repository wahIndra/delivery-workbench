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
    { name: 'Dashboard', path: '/dashboard' },
    { name: 'Requests', path: '/requests' },
    { name: 'Release Calendar', path: '/release-calendar' },
    { name: 'AI Audit Logs', path: '/ai-audit-logs' },
    { name: 'Admin', path: '/admin/users' },
  ];

  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-header">
          Delivery Workbench
        </div>
        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`nav-item ${location.pathname.startsWith(item.path) ? 'active' : ''}`}
            >
              {item.name}
            </Link>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div className="mb-4">
            <div style={{ color: 'var(--text-inverse)', fontWeight: 600 }}>{username}</div>
            <div>{role}</div>
          </div>
          <button onClick={handleLogout} className="btn btn-secondary" style={{ width: '100%', fontSize: '0.75rem' }}>
            Logout / Switch Role
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        <header className="top-header">
          <div style={{ fontWeight: 500, color: 'var(--text-secondary)' }}>
            IT Delivery Governance
          </div>
          <div className="flex items-center gap-4">
            <NotificationBell />
            <span className="badge badge-purple">Beta</span>
          </div>
        </header>
        
        <div className="page-container animate-fade-in">
          {children}
        </div>
      </main>
    </div>
  );
}
