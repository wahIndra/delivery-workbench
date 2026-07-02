import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function RequestListPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      const response = await api.get('/requests');
      setRequests(response.data.content || response.data);
    } catch (err) {
      console.error('Failed to fetch requests', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const colors = {
      'DRAFT': 'badge-gray',
      'SUBMITTED': 'badge-blue',
      'NEED_CLARIFICATION': 'badge-yellow',
      'READY_FOR_RELEASE': 'badge-green',
      'RELEASED': 'badge-green',
      'CANCELLED': 'badge-red'
    };
    return colors[status] || 'badge-purple';
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <h1>Delivery Requests</h1>
        <button onClick={() => navigate('/requests/new')} className="btn btn-primary">
          + New Request
        </button>
      </div>

      <div className="card" style={{ padding: 0 }}>
        {loading ? (
          <div className="p-8 text-center"><div className="spinner"></div></div>
        ) : (
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Title</th>
                  <th>Status</th>
                  <th>Business Owner</th>
                  <th>IT Owner</th>
                  <th>Created At</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {requests.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '2rem' }}>No requests found.</td>
                  </tr>
                ) : (
                  requests.map((req) => (
                    <tr key={req.id}>
                      <td style={{ fontWeight: 600 }}>{req.requestCode}</td>
                      <td>{req.title}</td>
                      <td>
                        <span className={`badge ${getStatusBadge(req.status)}`}>{req.status}</span>
                      </td>
                      <td>{req.businessOwner || '-'}</td>
                      <td>{req.itOwner || '-'}</td>
                      <td>{dayjs(req.createdAt).format('YYYY-MM-DD')}</td>
                      <td>
                        <Link to={`/requests/${req.id}`} className="btn btn-secondary" style={{ padding: '0.25rem 0.5rem' }}>
                          View
                        </Link>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
