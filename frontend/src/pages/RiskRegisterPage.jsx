import { useState, useEffect } from 'react';
import api from '../api';
import dayjs from 'dayjs';
import { useParams } from 'react-router-dom';

export default function RiskRegisterPage({ requestId: propsRequestId }) {
  // Can be used as a standalone page or embedded in RequestDetailPage
  const { id: paramRequestId } = useParams();
  const requestId = propsRequestId || paramRequestId;

  const [risks, setRisks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [generating, setGenerating] = useState(false);

  useEffect(() => {
    if (requestId) {
      fetchRisks();
    }
  }, [requestId]);

  const fetchRisks = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/requests/${requestId}/risks`);
      setRisks(res.data);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch risks', err);
      setError('Failed to load risk register.');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateAISuggestions = async () => {
    try {
      setGenerating(true);
      // Assuming AI integration is via a specific endpoint, but let's mock the generation by fetching from AI service and then creating them.
      // Wait, the AI endpoint would be something like /requests/{id}/ai/risk-suggestions
      const aiRes = await api.get(`/requests/${requestId}/risks/ai-suggestions`);
      const suggestions = aiRes.data;
      
      // Save them
      for (const s of suggestions) {
        await api.post(`/requests/${requestId}/risks`, {
          riskTitle: s.riskTitle,
          riskDescription: s.riskDescription,
          riskCategory: s.riskCategory,
          probability: s.probability,
          impact: s.impact,
          mitigationPlan: 'Pending Review'
        });
      }
      await fetchRisks();
    } catch (err) {
      console.error('Failed to generate risks', err);
      alert('Failed to generate AI risks.');
    } finally {
      setGenerating(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'OPEN': return 'bg-yellow-100 text-yellow-800';
      case 'MITIGATED': return 'bg-blue-100 text-blue-800';
      case 'ACCEPTED': return 'bg-purple-100 text-purple-800';
      case 'CLOSED': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) return <div className="p-4 text-center"><div className="spinner"></div></div>;
  if (error) return <div className="card border-red-500 text-red-700">{error}</div>;

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="m-0">Risk Register</h3>
        <div className="flex gap-2">
          <button onClick={handleGenerateAISuggestions} disabled={generating} className="btn btn-secondary flex items-center gap-2">
            {generating ? <div className="spinner w-4 h-4" /> : <span>✨ Generate AI Risks</span>}
          </button>
          <button onClick={fetchRisks} className="btn btn-secondary">Refresh</button>
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="p-3 font-medium">Risk Title</th>
              <th className="p-3 font-medium">Category</th>
              <th className="p-3 font-medium">Score (P x I)</th>
              <th className="p-3 font-medium">Owner</th>
              <th className="p-3 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {risks.map(risk => (
              <tr key={risk.id} className="border-b last:border-0 hover:bg-gray-50">
                <td className="p-3">
                  <div className="font-medium text-gray-800">{risk.riskTitle}</div>
                  <div className="text-xs text-gray-500 mt-1 max-w-xs truncate">{risk.riskDescription}</div>
                </td>
                <td className="p-3">{risk.riskCategory}</td>
                <td className="p-3">
                  <div className="flex items-center gap-2">
                    <span className="font-bold">{risk.riskScore}</span>
                    <span className="text-xs text-gray-500">({risk.probability.charAt(0)} x {risk.impact.charAt(0)})</span>
                  </div>
                </td>
                <td className="p-3">{risk.owner || '-'}</td>
                <td className="p-3">
                  <span className={`badge ${getStatusColor(risk.status)}`}>{risk.status}</span>
                </td>
              </tr>
            ))}
            {risks.length === 0 && (
              <tr>
                <td colSpan="5" className="p-6 text-center text-gray-500">No risks documented.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
