import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api';
import './LoginPage.css'; // Premium UI styling

export default function LoginPage() {
  const [usernameInput, setUsernameInput] = useState('');
  const [password, setPassword] = useState('');
  
  // Registration specific state
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [role, setRole] = useState('BUSINESS_USER');
  
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleAuth = async (e) => {
    e.preventDefault();
    if (!usernameInput || !password) {
      setError('Please enter both username and password.');
      return;
    }
    
    if (isRegisterMode && (!fullName || !email)) {
      setError('Please fill in all registration fields.');
      return;
    }
    
    setIsLoading(true);
    setError('');

    try {
      if (isRegisterMode) {
        // Register flow
        const payload = {
          username: usernameInput,
          password: password,
          fullName: fullName,
          email: email,
          role: role
        };
        await authApi.register(payload);
        
        // Auto-login after register
        const loginRes = await authApi.login({ username: usernameInput, password });
        finishLogin(loginRes.data.user);
      } else {
        // Login flow
        const loginRes = await authApi.login({ username: usernameInput, password });
        finishLogin(loginRes.data.user);
      }
    } catch (err) {
      console.error('Auth error', err);
      if (err.response && err.response.status === 401) {
        setError('Invalid credentials. Please try again.');
      } else if (err.response && err.response.data) {
        setError(err.response.data.message || err.response.data || 'Authentication failed');
      } else {
        setError('Connection error. Is the backend running?');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const finishLogin = (user) => {
    // For dev MVP, we still rely on MockAuthFilter via these headers
    localStorage.setItem('mockUsername', user.username);
    localStorage.setItem('mockRole', user.role);
    navigate('/');
  };

  const autofillDemo = () => {
    setUsernameInput('admin');
    setPassword('password');
    setIsRegisterMode(false);
  };

  return (
    <div className="login-page-container">
      <div className="login-bg-glow-1"></div>
      <div className="login-bg-glow-2"></div>
      
      {/* Left side (same as before) */}
      <div className="login-content-wrapper">
        <div className="login-left-side">
          <div className="login-logo">
            <div className="login-logo-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 2 7 12 12 22 7 12 2"></polygon><polyline points="2 17 12 22 22 17"></polyline><polyline points="2 12 12 17 22 12"></polyline></svg>
            </div>
            <div className="login-logo-text">IT DELIVERY <span style={{ color: '#8b5cf6' }}>WORKBENCH</span></div>
          </div>
          
          <h1 className="login-title">
            The AI-Powered IT Delivery<br/>
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
                <h3 className="login-feature-title">Real-time authentication</h3>
                <p className="login-feature-desc">Full stack integration with role-based governance.</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right side */}
        <div className="login-right-side">
          <div className="login-panel">
            <h2>{isRegisterMode ? 'Create an account' : 'Welcome back'}</h2>
            <p>{isRegisterMode ? 'Register to access the platform' : 'Sign in to continue managing delivery flow'}</p>
            
            {error && (
              <div style={{ marginBottom: '1.5rem', padding: '1rem', backgroundColor: 'rgba(220, 38, 38, 0.1)', border: '1px solid rgba(220, 38, 38, 0.3)', borderRadius: '12px', color: '#fca5a5', fontSize: '0.9375rem' }}>
                {error}
              </div>
            )}

            <form onSubmit={handleAuth}>
              <div className="login-form-group">
                <label className="login-label">Username</label>
                <div className="login-input-wrapper">
                  <input 
                    type="text" 
                    className="login-input"
                    placeholder="Enter your username"
                    value={usernameInput}
                    onChange={(e) => setUsernameInput(e.target.value)}
                    style={{ paddingLeft: '1rem' }}
                  />
                </div>
              </div>

              {isRegisterMode && (
                <>
                  <div className="login-form-group">
                    <label className="login-label">Full Name</label>
                    <div className="login-input-wrapper">
                      <input 
                        type="text" 
                        className="login-input"
                        placeholder="John Doe"
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        style={{ paddingLeft: '1rem' }}
                      />
                    </div>
                  </div>
                  
                  <div className="login-form-group">
                    <label className="login-label">Email</label>
                    <div className="login-input-wrapper">
                      <input 
                        type="email" 
                        className="login-input"
                        placeholder="john@example.com"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        style={{ paddingLeft: '1rem' }}
                      />
                    </div>
                  </div>
                  
                  <div className="login-form-group">
                    <label className="login-label">Role</label>
                    <select 
                      className="login-input"
                      value={role}
                      onChange={(e) => setRole(e.target.value)}
                      style={{ paddingLeft: '1rem', appearance: 'auto', backgroundColor: '#1e293b', color: 'white' }}
                    >
                      <option value="BUSINESS_USER">Business User</option>
                      <option value="BUSINESS_OWNER">Business Owner</option>
                      <option value="SYSTEM_ANALYST">System Analyst</option>
                      <option value="SOLUTION_ARCHITECT">Solution Architect</option>
                      <option value="QA">QA Engineer</option>
                      <option value="RELEASE_MANAGER">Release Manager</option>
                    </select>
                  </div>
                </>
              )}

              <div className="login-form-group" style={{ marginBottom: '1.5rem' }}>
                <label className="login-label">Password</label>
                <div className="login-input-wrapper">
                  <input 
                    type="password" 
                    className="login-input"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{ paddingLeft: '1rem' }}
                  />
                </div>
              </div>

              <button type="submit" className="login-btn" disabled={isLoading}>
                {isLoading ? 'Processing...' : (isRegisterMode ? 'Sign up' : 'Sign in')}
              </button>
            </form>

            <div className="login-divider">
              <div className="login-divider-line"></div>
              <div className="login-divider-text">OR</div>
              <div className="login-divider-line"></div>
            </div>

            <div style={{ textAlign: 'center' }}>
              <button 
                type="button" 
                onClick={() => {
                  setIsRegisterMode(!isRegisterMode);
                  setError('');
                }}
                style={{ 
                  background: 'none', 
                  border: 'none', 
                  color: '#38bdf8', 
                  cursor: 'pointer',
                  fontSize: '0.9375rem',
                  fontWeight: '600'
                }}
              >
                {isRegisterMode ? 'Already have an account? Sign in' : 'Need an account? Sign up'}
              </button>
            </div>
            
            {!isRegisterMode && (
              <div style={{ marginTop: '2rem' }}>
                <div style={{ fontSize: '0.875rem', color: '#cbd5e1', marginBottom: '0.75rem' }}>Demo Admin Account</div>
                <div className="login-demo-account" onClick={autofillDemo}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span style={{ fontSize: '0.9375rem', color: '#e2e8f0' }}>admin</span>
                  </div>
                  <span style={{ fontSize: '0.9375rem', color: '#8b5cf6' }}>Password: password</span>
                </div>
              </div>
            )}
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
