import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../api';

export default function ClarificationPage() {
  const { id } = useParams();
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [newQuestion, setNewQuestion] = useState('');
  
  // To handle answering
  const [answeringId, setAnsweringId] = useState(null);
  const [answerText, setAnswerText] = useState('');

  const role = localStorage.getItem('mockRole');

  useEffect(() => {
    fetchQuestions();
  }, [id]);

  const fetchQuestions = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/requests/${id}/clarifications`);
      setQuestions(response.data);
    } catch (err) {
      console.error('Error fetching questions', err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateAI = async () => {
    try {
      setGenerating(true);
      const res = await api.post(`/requests/${id}/clarifications/ai-generate`);
      setQuestions([...questions, ...res.data]);
    } catch (err) {
      console.error('Error generating AI questions', err);
      alert('Failed to generate AI questions');
    } finally {
      setGenerating(false);
    }
  };

  const handleAskManual = async (e) => {
    e.preventDefault();
    if (!newQuestion.trim()) return;
    try {
      const res = await api.post(`/requests/${id}/clarifications`, { question: newQuestion });
      setQuestions([...questions, res.data]);
      setNewQuestion('');
    } catch (err) {
      console.error('Error asking question', err);
    }
  };

  const handleAnswer = async (questionId) => {
    if (!answerText.trim()) return;
    try {
      const res = await api.put(`/requests/${id}/clarifications/${questionId}/answer`, { answer: answerText });
      setQuestions(questions.map(q => q.id === questionId ? res.data : q));
      setAnsweringId(null);
      setAnswerText('');
    } catch (err) {
      console.error('Error answering question', err);
    }
  };

  const handleDelete = async (questionId) => {
    if (!confirm('Are you sure you want to delete this question?')) return;
    try {
      await api.delete(`/requests/${id}/clarifications/${questionId}`);
      setQuestions(questions.filter(q => q.id !== questionId));
    } catch (err) {
      console.error('Error deleting question', err);
    }
  };

  if (loading) return <div className="p-8 text-center"><div className="spinner"></div></div>;

  const openCount = questions.filter(q => q.status === 'OPEN').length;

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <Link to={`/requests/${id}`} className="text-muted" style={{ fontSize: '0.875rem' }}>← Back to Request</Link>
          <h1 className="mt-2">Clarification Questions</h1>
        </div>
        <div className="flex gap-2">
          {role === 'SYSTEM_ANALYST' && (
            <button onClick={handleGenerateAI} disabled={generating} className="btn btn-secondary" style={{ borderColor: 'var(--secondary-500)', color: 'var(--secondary-600)' }}>
              {generating ? '✨ Generating...' : '✨ AI Generate Questions'}
            </button>
          )}
        </div>
      </div>

      <div className="grid" style={{ gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
        <div>
          {questions.length === 0 ? (
            <div className="card text-center p-8">
              <h3 className="text-muted">No clarification questions yet.</h3>
              <p>System Analysts can generate AI questions or add manual ones.</p>
            </div>
          ) : (
            <div className="flex flex-col gap-4">
              {questions.map((q) => (
                <div key={q.id} className="card" style={{ borderLeft: q.status === 'OPEN' ? '4px solid var(--warning-600)' : '4px solid var(--success-600)' }}>
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex gap-2 items-center">
                      <span className={`badge ${q.status === 'OPEN' ? 'badge-yellow' : 'badge-green'}`}>{q.status}</span>
                      {q.source === 'AI' && <span className="badge badge-purple">AI Generated</span>}
                      <span className="text-muted" style={{ fontSize: '0.75rem' }}>By {q.askedBy} • {dayjs(q.createdAt).format('MMM D, HH:mm')}</span>
                    </div>
                    {role === 'SYSTEM_ANALYST' && q.status === 'OPEN' && (
                      <button onClick={() => handleDelete(q.id)} className="btn btn-danger" style={{ padding: '0.2rem 0.5rem', fontSize: '0.75rem' }}>Delete</button>
                    )}
                  </div>
                  <h4 className="mb-4">{q.question}</h4>

                  {q.status === 'ANSWERED' ? (
                    <div className="p-4" style={{ backgroundColor: 'var(--bg-surface-hover)', borderRadius: 'var(--radius-md)' }}>
                      <div className="flex justify-between items-center mb-2">
                        <span style={{ fontWeight: 600, fontSize: '0.875rem' }}>Answered by {q.answeredBy}</span>
                        <span className="text-muted" style={{ fontSize: '0.75rem' }}>{dayjs(q.answeredAt).format('MMM D, HH:mm')}</span>
                      </div>
                      <p>{q.answer}</p>
                    </div>
                  ) : (
                    role === 'BUSINESS_USER' && (
                      answeringId === q.id ? (
                        <div className="mt-4">
                          <textarea className="form-control mb-2" rows="3" value={answerText} onChange={e => setAnswerText(e.target.value)} placeholder="Type your answer here..." />
                          <div className="flex gap-2 justify-end">
                            <button onClick={() => setAnsweringId(null)} className="btn btn-secondary">Cancel</button>
                            <button onClick={() => handleAnswer(q.id)} className="btn btn-primary">Submit Answer</button>
                          </div>
                        </div>
                      ) : (
                        <button onClick={() => setAnsweringId(q.id)} className="btn btn-primary mt-4">Answer Question</button>
                      )
                    )
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        <div>
          {role === 'SYSTEM_ANALYST' && (
            <div className="card">
              <h3>Ask Manual Question</h3>
              <form onSubmit={handleAskManual} className="mt-4">
                <textarea 
                  className="form-control mb-4" 
                  rows="4" 
                  placeholder="Type a new question..."
                  value={newQuestion}
                  onChange={e => setNewQuestion(e.target.value)}
                  required
                />
                <button type="submit" className="btn btn-primary w-full">Ask Question</button>
              </form>
            </div>
          )}

          <div className="card mt-4">
            <h3>Summary</h3>
            <div className="flex justify-between items-center mt-4">
              <span>Total Questions</span>
              <span style={{ fontWeight: 600 }}>{questions.length}</span>
            </div>
            <div className="flex justify-between items-center mt-2">
              <span>Open Pending</span>
              <span style={{ fontWeight: 600, color: openCount > 0 ? 'var(--warning-600)' : 'inherit' }}>{openCount}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
