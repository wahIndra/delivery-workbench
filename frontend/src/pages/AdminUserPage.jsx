import { useState, useEffect } from 'react';
import { userApi } from '../api';
import './AdminUserPage.css';

export default function AdminUserPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await userApi.getAll();
      setUsers(response.data);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch users:', err);
      setError('Failed to load users. Are you logged in as an ADMIN?');
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      await userApi.updateRole(userId, newRole);
      setUsers(users.map(u => u.id === userId ? { ...u, role: newRole } : u));
    } catch (err) {
      alert('Failed to update role');
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    
    try {
      await userApi.delete(userId);
      setUsers(users.filter(u => u.id !== userId));
    } catch (err) {
      alert('Failed to delete user');
    }
  };

  if (loading) return <div style={{ padding: '2rem', color: '#f8fafc' }}>Loading users...</div>;
  if (error) return <div style={{ padding: '2rem', color: '#fca5a5' }}>{error}</div>;

  return (
    <div className="admin-users-container">
      <div className="admin-users-header">
        <h1 style={{ color: '#f8fafc', margin: 0, fontSize: '1.5rem', fontWeight: 600 }}>User Management</h1>
        <p style={{ color: '#94a3b8', margin: '0.5rem 0 0 0' }}>Manage roles and access for platform users.</p>
      </div>

      <div className="admin-users-table-container">
        <table className="admin-users-table">
          <thead>
            <tr>
              <th>Username</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td style={{ fontWeight: 500, color: '#f1f5f9' }}>{user.username}</td>
                  <td>{user.fullName}</td>
                  <td>{user.email}</td>
                  <td>
                    <select 
                      value={user.role} 
                      onChange={(e) => handleRoleChange(user.id, e.target.value)}
                      className="admin-role-select"
                      disabled={user.username === 'admin'}
                    >
                      <option value="BUSINESS_USER">Business User</option>
                      <option value="BUSINESS_OWNER">Business Owner</option>
                      <option value="SYSTEM_ANALYST">System Analyst</option>
                      <option value="SOLUTION_ARCHITECT">Solution Architect</option>
                      <option value="QA">QA Engineer</option>
                      <option value="RELEASE_MANAGER">Release Manager</option>
                      <option value="ADMIN">Admin</option>
                    </select>
                  </td>
                  <td>
                    <span className="admin-status-badge">
                      {user.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <button 
                      onClick={() => handleDelete(user.id)}
                      className="admin-delete-btn"
                      disabled={user.username === 'admin'}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
        </table>
      </div>
    </div>
  );
}
