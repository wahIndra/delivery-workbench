import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function LoginPage() {
  const navigate = useNavigate();
  const [selectedUser, setSelectedUser] = useState('business.user');

  // Hardcoded mock users based on master_prompt requirements
  const users = [
    { username: 'business.user', role: 'BUSINESS_USER', name: 'Business User' },
    { username: 'business.owner', role: 'BUSINESS_OWNER', name: 'Business Owner' },
    { username: 'system.analyst', role: 'SYSTEM_ANALYST', name: 'System Analyst' },
    { username: 'principal.engineer', role: 'SOLUTION_ARCHITECT', name: 'Solution Architect' },
    { username: 'developer', role: 'DEVELOPER', name: 'Developer' },
    { username: 'qa.user', role: 'QA', name: 'QA Engineer' },
    { username: 'release.manager', role: 'RELEASE_MANAGER', name: 'Release Manager' },
    { username: 'management.viewer', role: 'MANAGEMENT_VIEWER', name: 'Management Viewer' },
    { username: 'admin', role: 'ADMIN', name: 'Administrator' },
  ];

  const handleLogin = (e) => {
    e.preventDefault();
    const user = users.find(u => u.username === selectedUser);
    if (user) {
      localStorage.setItem('mockUsername', user.username);
      localStorage.setItem('mockRole', user.role);
      navigate('/dashboard');
    }
  };

  return (
    <div className="flex items-center justify-center" style={{ minHeight: '100vh', backgroundColor: 'var(--bg-main)' }}>
      <div className="card glass-panel" style={{ width: '100%', maxWidth: '400px', padding: '2.5rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 style={{ color: 'var(--primary-600)', margin: 0 }}>IT Delivery</h1>
          <h2 style={{ fontSize: '1.25rem', color: 'var(--text-secondary)' }}>Workbench</h2>
        </div>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label className="form-label">Select Demo User (Role)</label>
            <select 
              className="form-control" 
              value={selectedUser} 
              onChange={(e) => setSelectedUser(e.target.value)}
            >
              {users.map(u => (
                <option key={u.username} value={u.username}>
                  {u.name} ({u.role})
                </option>
              ))}
            </select>
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
              Authentication is mocked for the MVP. Selecting a user simulates a logged-in session with that role.
            </p>
          </div>

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
            Login / Enter
          </button>
        </form>
      </div>
    </div>
  );
}
