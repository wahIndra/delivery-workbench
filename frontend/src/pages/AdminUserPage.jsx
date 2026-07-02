export default function AdminUserPage() {
  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <h1>User Administration</h1>
      </div>

      <div className="card text-center p-8">
        <h3 className="mb-4">Mock Authentication Enabled</h3>
        <p className="text-muted" style={{ maxWidth: '600px', margin: '0 auto' }}>
          For the MVP, a full user management and authentication system is out of scope (A-04). 
          Authentication is simulated by selecting a predefined user role on the Login page. 
          Real User Management will be implemented in a future phase using Spring Security and OAuth2/JWT.
        </p>
      </div>
    </div>
  );
}
