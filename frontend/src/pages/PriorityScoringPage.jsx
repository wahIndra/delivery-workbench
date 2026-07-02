import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { priorityScoreApi } from '../api';
import api from '../api';

const criteriaList = [
  { key: 'businessImpactScore', label: 'Business Impact', positive: true },
  { key: 'urgencyScore', label: 'Urgency', positive: true },
  { key: 'regulatoryImpactScore', label: 'Regulatory & Compliance Impact', positive: true },
  { key: 'customerImpactScore', label: 'Customer Impact', positive: true },
  { key: 'operationalRiskScore', label: 'Operational Risk', positive: true },
  { key: 'technicalComplexityScore', label: 'Technical Complexity', positive: false },
  { key: 'dependencyScore', label: 'Dependencies', positive: false }
];

const PriorityScoringPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [request, setRequest] = useState(null);
  const [score, setScore] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isAiLoading, setIsAiLoading] = useState(false);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [reqRes, scoreRes] = await Promise.all([
        api.get(`/requests/${id}`),
        priorityScoreApi.getScore(id)
      ]);
      setRequest(reqRes.data);
      setScore(scoreRes.data);
    } catch (err) {
      console.error(err);
      setError('Failed to load data. Please ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleScoreChange = (key, value) => {
    setScore(prev => {
      const updated = { ...prev, [key]: parseInt(value) };
      // Recalculate preview total
      updated.totalScore = Math.max(1, 
        updated.businessImpactScore + 
        updated.urgencyScore + 
        updated.regulatoryImpactScore + 
        updated.customerImpactScore + 
        updated.operationalRiskScore - 
        updated.technicalComplexityScore - 
        updated.dependencyScore
      );
      return updated;
    });
  };

  const handleSave = async () => {
    try {
      const updatePayload = {
        businessImpactScore: score.businessImpactScore,
        urgencyScore: score.urgencyScore,
        regulatoryImpactScore: score.regulatoryImpactScore,
        customerImpactScore: score.customerImpactScore,
        operationalRiskScore: score.operationalRiskScore,
        technicalComplexityScore: score.technicalComplexityScore,
        dependencyScore: score.dependencyScore,
        scoringNotes: score.scoringNotes || ''
      };
      const res = await priorityScoreApi.updateScore(id, updatePayload);
      setScore(res.data);
      alert('Priority score saved successfully!');
    } catch (err) {
      console.error(err);
      setError('Failed to save score. Please try again.');
    }
  };

  const handleGenerateAi = async () => {
    setIsAiLoading(true);
    setError('');
    try {
      const res = await priorityScoreApi.generateAIScore(id);
      setScore(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to generate AI recommendation.');
    } finally {
      setIsAiLoading(false);
    }
  };

  if (loading) return <div className="p-8 text-center">Loading priority scoring...</div>;
  if (!request) return <div className="p-8 text-center text-red-500">{error}</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <button 
            onClick={() => navigate(`/requests/${id}`)}
            className="text-sm text-blue-600 hover:text-blue-800 mb-2 flex items-center gap-1"
          >
            ← Back to Request Detail
          </button>
          <h1 className="text-2xl font-bold">Priority Scoring</h1>
          <p className="text-gray-600">{request.requestCode} - {request.title}</p>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500 uppercase tracking-wider font-semibold">Total Score</div>
          <div className="text-4xl font-bold text-blue-600">{score?.totalScore || 0}</div>
          <div className="text-sm mt-1 font-semibold text-gray-700">
            Priority: <span className="text-purple-600">{score?.priorityRecommendation || 'PENDING'}</span>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-4 text-red-700">
          {error}
        </div>
      )}

      {/* Main Form */}
      <div className="card">
        <h2 className="text-lg font-bold mb-4 border-b pb-2">Score Criteria</h2>
        <p className="text-sm text-gray-600 mb-6">
          Rate each criteria from 1 (Low) to 5 (High). Note that Technical Complexity and Dependencies reduce the overall score priority.
        </p>

        <div className="space-y-6">
          {criteriaList.map(c => (
            <div key={c.key} className="flex items-center gap-4">
              <div className="w-1/3">
                <label className="block text-sm font-semibold text-gray-700">{c.label}</label>
                <div className="text-xs text-gray-500">{c.positive ? '(Increases Priority)' : '(Decreases Priority)'}</div>
              </div>
              <div className="w-2/3 flex items-center gap-4">
                <input 
                  type="range" 
                  min="1" 
                  max="5" 
                  value={score?.[c.key] || 1}
                  onChange={(e) => handleScoreChange(c.key, e.target.value)}
                  className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
                <span className="font-bold text-lg w-8 text-center">{score?.[c.key] || 1}</span>
              </div>
            </div>
          ))}
        </div>

        <div className="mt-8">
          <label className="block text-sm font-semibold text-gray-700 mb-2">Scoring Notes & AI Recommendation</label>
          <textarea
            value={score?.scoringNotes || ''}
            onChange={(e) => setScore({...score, scoringNotes: e.target.value})}
            className="w-full p-3 border rounded-md font-mono text-sm"
            rows="6"
            placeholder="Enter manual notes or use AI to generate a recommendation..."
          />
        </div>

        <div className="mt-6 flex gap-4 border-t pt-4">
          <button 
            onClick={handleSave}
            className="btn btn-primary"
          >
            Save Priority Score
          </button>
          
          <button 
            onClick={handleGenerateAi}
            disabled={isAiLoading}
            className="btn border border-purple-600 text-purple-600 hover:bg-purple-50 flex items-center gap-2"
          >
            {isAiLoading ? 'Generating...' : '✨ Generate AI Recommendation'}
          </button>
        </div>
      </div>
      
    </div>
  );
};

export default PriorityScoringPage;
