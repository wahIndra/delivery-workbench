import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { meetingNoteApi } from '../api';

export default function MeetingNotesPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSummarizing, setIsSummarizing] = useState(false);

  const [rawNotes, setRawNotes] = useState('');
  
  const [form, setForm] = useState({
    meetingTitle: '',
    meetingDate: dayjs().format('YYYY-MM-DD'),
    attendees: '',
    discussionSummary: '',
    decisions: '',
    actionItems: '',
    source: 'HUMAN'
  });

  useEffect(() => {
    fetchNotes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchNotes = async () => {
    try {
      const res = await meetingNoteApi.getNotes(id);
      setNotes(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSummarize = async () => {
    if (!rawNotes.trim()) {
      alert('Please paste some raw notes first');
      return;
    }
    setIsSummarizing(true);
    try {
      const res = await meetingNoteApi.summarize(id, rawNotes);
      setForm({
        ...form,
        discussionSummary: res.data.discussionSummary || '',
        decisions: res.data.decisions || '',
        actionItems: res.data.actionItems || '',
        source: 'AI'
      });
    } catch (err) {
      alert('Failed to summarize notes');
    } finally {
      setIsSummarizing(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await meetingNoteApi.create(id, form);
      setForm({
        meetingTitle: '',
        meetingDate: dayjs().format('YYYY-MM-DD'),
        attendees: '',
        discussionSummary: '',
        decisions: '',
        actionItems: '',
        source: 'HUMAN'
      });
      setRawNotes('');
      fetchNotes();
    } catch (err) {
      alert('Failed to save meeting note');
    }
  };

  if (loading) return <div className="p-8 text-center">Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h2>Meeting Notes</h2>
        <button onClick={() => navigate(`/requests/${id}`)} className="btn btn-secondary">Back to Request</button>
      </div>

      <div className="card mb-8">
        <h3>Create New Meeting Note</h3>
        
        <div className="mt-4 mb-6 p-4 bg-indigo-50 rounded border border-indigo-100">
          <label className="form-label text-indigo-900">✨ AI Assistant: Paste Raw Notes Here</label>
          <textarea className="form-control" rows="4" 
            placeholder="Paste your raw, unstructured meeting notes here..."
            value={rawNotes} onChange={e => setRawNotes(e.target.value)}></textarea>
          <button type="button" onClick={handleSummarize} disabled={isSummarizing} className="btn btn-primary bg-indigo-600 border-indigo-600 mt-2 text-white text-sm">
            {isSummarizing ? 'Summarizing...' : 'Summarize with AI'}
          </button>
        </div>

        <form onSubmit={handleCreate} className="flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="form-label">Meeting Title *</label>
              <input type="text" className="form-control" required
                value={form.meetingTitle} onChange={e => setForm({...form, meetingTitle: e.target.value})} />
            </div>
            <div>
              <label className="form-label">Meeting Date *</label>
              <input type="date" className="form-control" required
                value={form.meetingDate} onChange={e => setForm({...form, meetingDate: e.target.value})} />
            </div>
          </div>
          <div>
            <label className="form-label">Attendees</label>
            <input type="text" className="form-control" placeholder="e.g. John, Sarah, IT Team"
              value={form.attendees} onChange={e => setForm({...form, attendees: e.target.value})} />
          </div>
          <div>
            <label className="form-label">Discussion Summary</label>
            <textarea className="form-control" rows="3"
              value={form.discussionSummary} onChange={e => setForm({...form, discussionSummary: e.target.value})}></textarea>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="form-label">Decisions Made</label>
              <textarea className="form-control" rows="3"
                value={form.decisions} onChange={e => setForm({...form, decisions: e.target.value})}></textarea>
            </div>
            <div>
              <label className="form-label">Action Items</label>
              <textarea className="form-control" rows="3"
                value={form.actionItems} onChange={e => setForm({...form, actionItems: e.target.value})}></textarea>
            </div>
          </div>
          <div className="flex items-center justify-between mt-2">
            <span className="text-sm text-gray-500">Source: <span className="font-semibold">{form.source}</span></span>
            <button type="submit" className="btn btn-primary">Save Meeting Note</button>
          </div>
        </form>
      </div>

      <h3>Meeting History</h3>
      <div className="mt-4 flex flex-col gap-4">
        {notes.length === 0 ? (
          <p className="text-gray-500">No meeting notes logged yet.</p>
        ) : (
          notes.map(note => (
            <div key={note.id} className="card border-l-4 border-gray-400">
              <div className="flex justify-between items-start mb-2">
                <h4 className="m-0 text-gray-800">{note.meetingTitle}</h4>
                <div className="text-sm text-gray-500">{dayjs(note.meetingDate).format('MMM D, YYYY')}</div>
              </div>
              <div className="text-xs text-gray-500 mb-4 flex gap-2">
                <span>By {note.createdBy}</span>
                <span>•</span>
                <span>Attendees: {note.attendees || 'None specified'}</span>
                <span>•</span>
                <span className={`badge ${note.source === 'AI' ? 'badge-blue' : 'bg-gray-200 text-gray-800'}`}>
                  {note.source === 'AI' ? '✨ AI Assisted' : 'Manual Entry'}
                </span>
              </div>
              
              <div className="text-sm mb-4">
                <strong>Summary:</strong><br/>
                {note.discussionSummary || 'N/A'}
              </div>
              <div className="grid grid-cols-2 gap-4 text-sm bg-gray-50 p-3 rounded">
                <div>
                  <strong>Decisions:</strong>
                  <div className="whitespace-pre-wrap">{note.decisions || 'None'}</div>
                </div>
                <div>
                  <strong>Action Items:</strong>
                  <div className="whitespace-pre-wrap">{note.actionItems || 'None'}</div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
