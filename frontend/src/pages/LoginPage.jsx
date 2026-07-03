import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginPage.css';

export default function LoginPage() {
  const navigate = useNavigate();
  const [usernameInput, setUsernameInput] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

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
    setError('');
    
    let loginId = usernameInput.trim().toLowerCase();
    
    // Map demo emails to usernames
    if (loginId === 'admin@demo.com') loginId = 'admin';
    if (loginId === 'business@demo.com') loginId = 'business.user';
    if (loginId === 'developer@demo.com') loginId = 'developer';
    if (loginId === 'qa@demo.com') loginId = 'qa.user';
    if (loginId === 'manager@demo.com') loginId = 'release.manager';

    const user = users.find(u => u.username === loginId);
    if (user) {
      localStorage.setItem('mockUsername', user.username);
      localStorage.setItem('mockRole', user.role);
      navigate('/dashboard');
    } else {
      setError('Invalid credentials. Please use a valid demo account (e.g. admin@demo.com).');
    }
  };

  const autofillDemo = () => {
    setUsernameInput('admin@demo.com');
    setPassword('admin123');
  };

  return (
    <div className="login-page-container">
      {/* Background glow effects */}
      <div className="login-bg-glow-1"></div>
      <div className="login-bg-glow-2"></div>

      <div className="login-content-wrapper">
        {/* LEFT SIDE - Branding & Features */}
        <div className="login-left-side">
          <div className="login-logo-container">
            <div className="login-logo-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
                <line x1="12" y1="22.08" x2="12" y2="12"></line>
              </svg>
            </div>
            <div className="login-logo-text">
              <span style={{ color: '#f8fafc' }}>IT DELIVERY</span> <span style={{ color: '#38bdf8' }}>WORKBENCH</span>
            </div>
          </div>

          <h1 className="login-title">
            AI-Assisted <br/>SDLC Governance <br/>
            <span className="login-title-gradient">Platform</span>
          </h1>

          <p className="login-subtitle">
            Reduce ambiguity, rework, and delivery bottlenecks across the IT development lifecycle.
          </p>

          <div className="login-features-list">
            <div className="login-feature">
              <div className="login-feature-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#38bdf8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
              </div>
              <div>
                <h3 className="login-feature-title">One request. One workflow. One source of truth.</h3>
                <p className="login-feature-desc">Standardize intake, requirements, and approvals from start to finish.</p>
              </div>
            </div>

            <div className="login-feature">
              <div className="login-feature-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#8b5cf6" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path><polyline points="9 12 11 14 15 10"></polyline></svg>
              </div>
              <div>
                <h3 className="login-feature-title">Human approval gates.</h3>
                <p className="login-feature-desc">AI-assisted acceleration with enterprise governance and accountability.</p>
              </div>
            </div>

            <div className="login-feature">
              <div className="login-feature-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#38bdf8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>
              </div>
              <div>
                <h3 className="login-feature-title">Track readiness, risk, bottlenecks, and release confidence.</h3>
                <p className="login-feature-desc">Real-time visibility across the delivery lifecycle.</p>
              </div>
            </div>
          </div>
        </div>

        {/* RIGHT SIDE - Login Panel */}
        <div className="login-right-side">
          <div className="login-panel">
            <h2>Welcome back</h2>
            <p>Sign in to continue managing delivery flow</p>
            
            {error && (
              <div style={{ marginBottom: '1.5rem', padding: '1rem', backgroundColor: 'rgba(220, 38, 38, 0.1)', border: '1px solid rgba(220, 38, 38, 0.3)', borderRadius: '12px', color: '#fca5a5', fontSize: '0.9375rem' }}>
                {error}
              </div>
            )}

            <form onSubmit={handleLogin}>
              <div className="login-form-group">
                <label className="login-label">Email or Username</label>
                <div className="login-input-wrapper">
                  <div className="login-input-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                  </div>
                  <input 
                    type="text" 
                    className="login-input"
                    placeholder="Enter your email or username"
                    value={usernameInput}
                    onChange={(e) => setUsernameInput(e.target.value)}
                  />
                </div>
              </div>

              <div className="login-form-group" style={{ marginBottom: '1.5rem' }}>
                <label className="login-label">Password</label>
                <div className="login-input-wrapper">
                  <div className="login-input-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                  </div>
                  <input 
                    type="password" 
                    className="login-input"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <div className="login-input-action">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                  </div>
                </div>
              </div>

              <div className="login-options">
                <label className="login-checkbox-label">
                  <input type="checkbox" className="login-checkbox" />
                  <span style={{ fontSize: '0.9375rem', color: '#cbd5e1' }}>Remember me</span>
                </label>
                <a href="#" className="login-forgot-link">Forgot password?</a>
              </div>

              <button type="submit" className="login-btn">
                Sign in
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
              </button>
            </form>

            <div className="login-divider">
              <div className="login-divider-line"></div>
              <div className="login-divider-text">OR</div>
              <div className="login-divider-line"></div>
            </div>

            <div style={{ marginBottom: '2rem' }}>
              <div style={{ fontSize: '0.875rem', color: '#cbd5e1', marginBottom: '0.75rem' }}>Demo accounts (if applicable)</div>
              <div className="login-demo-account" onClick={autofillDemo}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                  <span style={{ fontSize: '0.9375rem', color: '#e2e8f0' }}>admin@demo.com</span>
                </div>
                <span style={{ fontSize: '0.9375rem', color: '#8b5cf6' }}>Password: admin123</span>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'flex-start', gap: '0.75rem' }}>
              <div style={{ color: '#94a3b8', marginTop: '0.125rem' }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path><polyline points="9 12 11 14 15 10"></polyline></svg>
              </div>
              <div style={{ fontSize: '0.8125rem', color: '#64748b', lineHeight: '1.6' }}>
                <span style={{ color: '#94a3b8' }}>Secure. Controlled. Intelligent Delivery.</span><br/>
                Your data is protected and your workflow is our priority.
              </div>
            </div>

          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="login-footer">
        <span>© 2024 IT Delivery Workbench. All rights reserved.</span>
        <a href="#">Privacy Policy</a>
        <a href="#">Terms of Use</a>
      </div>
    </div>
  );
}
