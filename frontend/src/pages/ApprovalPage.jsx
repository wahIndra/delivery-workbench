import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { approvalApi } from '../api';

const APPROVAL_TYPES = [
  'REQUIREMENT_SIGNOFF',
  'SOLUTION_DESIGN_APPROVAL',
  'UAT_SIGNOFF',
  'RELEASE_APPROVAL',
  'PRIORITY_APPROVAL',
  'RISK_ACCEPTANCE'
];

const ROLES = [
  'BUSINESS_OWNER',
  'IT_OWNER',
  'SYSTEM_ANALYST',
  'ADMIN'
];

export default function ApprovalPage() {
  const { id } = useParams();
  const [approvals, setApprovals] = useState([]);
  const [newType, setNewType] = useState('REQUIREMENT_SIGNOFF');
  const [newRole, setNewRole] = useState('BUSINESS_OWNER');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const currentUserRole = localStorage.getItem('mockRole');

  useEffect(() => {
    fetchApprovals();
  }, [id]);

  const fetchApprovals = async () => {
    try {
      setLoading(true);
      const res = await approvalApi.getForRequest(id);
      setApprovals(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch approvals.');
    } finally {
      setLoading(false);
    }
  };

  const handleRequestApproval = async (e) => {
    e.preventDefault();
    try {
      setError('');
      await approvalApi.requestApproval(id, {
        approvalType: newType,
        approverRole: newRole
      });
      fetchApprovals();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to request approval.');
    }
  };

  const handleProcess = async (approvalId, status) => {
    try {
      const comment = prompt(`Enter optional comment for ${status}:`);
      if (comment === null) return; // Cancelled
      await approvalApi.processApproval(id, approvalId, { status, comment });
      fetchApprovals();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to process approval.');
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-blue-600 hover:underline mb-2 inline-block">&larr; Back to Request</Link>
          <h1 className="m-0">Approval Center</h1>
        </div>
      </div>

      {error && <div className="p-4 mb-4 text-red-700 bg-red-100 rounded-md">{error}</div>}

      <div className="card mb-8">
        <h3>Request New Approval</h3>
        <form onSubmit={handleRequestApproval} className="grid grid-cols-3 gap-4 items-end mt-4">
          <div>
            <label className="form-label">Approval Type</label>
            <select className="form-control" value={newType} onChange={e => setNewType(e.target.value)}>
              {APPROVAL_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div>
            <label className="form-label">Required Approver Role</label>
            <select className="form-control" value={newRole} onChange={e => setNewRole(e.target.value)}>
              {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
          <div>
            <button type="submit" className="btn btn-primary w-full">Submit Request</button>
          </div>
        </form>
      </div>

      <div className="card">
        <h3>Approval History</h3>
        <div className="mt-4 flex flex-col gap-4">
          {approvals.length === 0 ? (
            <p className="text-gray-500">No approvals found for this request.</p>
          ) : (
            approvals.map(approval => (
              <div key={approval.id} className="border p-4 rounded-md">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <h4 className="m-0 text-indigo-700">{approval.approvalType}</h4>
                    <div className="text-sm text-gray-500">
                      Requested on {dayjs(approval.createdAt).format('MMM D, YYYY HH:mm')}
                    </div>
                  </div>
                  <div>
                    <span className={`badge ${
                      approval.status === 'APPROVED' ? 'bg-green-100 text-green-800' : 
                      approval.status === 'REJECTED' ? 'bg-red-100 text-red-800' : 
                      'bg-yellow-100 text-yellow-800'
                    }`}>
                      {approval.status}
                    </span>
                  </div>
                </div>
                
                <div className="text-sm mb-3">
                  <strong>Assigned Role:</strong> {approval.approverRole}
                </div>

                {approval.status === 'PENDING' && currentUserRole === approval.approverRole && (
                  <div className="flex gap-2">
                    <button onClick={() => handleProcess(approval.id, 'APPROVED')} className="btn bg-green-600 text-white hover:bg-green-700 py-1 px-3">Approve</button>
                    <button onClick={() => handleProcess(approval.id, 'REJECTED')} className="btn bg-red-600 text-white hover:bg-red-700 py-1 px-3">Reject</button>
                  </div>
                )}
                
                {approval.status === 'PENDING' && currentUserRole !== approval.approverRole && (
                  <div className="text-xs text-gray-500 italic">Waiting for {approval.approverRole} to review.</div>
                )}

                {approval.status !== 'PENDING' && (
                  <div className="bg-gray-50 p-3 rounded text-sm mt-3 border">
                    <div><strong>Processed By:</strong> {approval.approverUser}</div>
                    <div><strong>Date:</strong> {dayjs(approval.approvedAt || approval.rejectedAt).format('MMM D, YYYY HH:mm')}</div>
                    {approval.comment && <div><strong>Comment:</strong> {approval.comment}</div>}
                  </div>
                )}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
